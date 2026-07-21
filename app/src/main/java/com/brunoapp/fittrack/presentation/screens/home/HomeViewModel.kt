package com.brunoapp.fittrack.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.utils.ComplianceCalc
import com.brunoapp.fittrack.core.utils.DateUtils
import com.brunoapp.fittrack.domain.model.MacroGoals
import com.brunoapp.fittrack.domain.model.Routine
import com.brunoapp.fittrack.domain.repository.DailyLogRepository
import com.brunoapp.fittrack.domain.repository.DietRepository
import com.brunoapp.fittrack.domain.repository.ProfileRepository
import com.brunoapp.fittrack.domain.repository.ProgressRepository
import com.brunoapp.fittrack.domain.repository.RoutineRepository
import com.brunoapp.fittrack.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val todayRoutine: Routine? = null,
    val hasActiveWorkout: Boolean = false,
    val sessionsThisWeek: Int = 0,
    val caloriesConsumed: Int = 0,
    val proteinConsumed: Int = 0,
    val goals: MacroGoals = MacroGoals(),
    val mealsCompleted: Int = 0,
    val mealsTotal: Int = 0,
    val dayStarted: Boolean = false,
    val weeklyCompliance: Int? = null,
    val currentWeight: Double? = null,
    val weightDiff: Double? = null,      // vs previous entry
    val nextWeighInDay: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    routineRepository: RoutineRepository,
    dailyLogRepository: DailyLogRepository,
    dietRepository: DietRepository,
    progressRepository: ProgressRepository,
    profileRepository: ProfileRepository
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()
    private val todayString = today.format(DateTimeFormatter.ISO_LOCAL_DATE)
    private val todayDow = today.dayOfWeek.value - 1  // 0 = Monday

    private val dayNames = listOf(
        "lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"
    )

    private val _navigateToWorkout = MutableStateFlow(false)
    val navigateToWorkout: StateFlow<Boolean> = _navigateToWorkout.asStateFlow()

    private val trainingFlow = combine(
        routineRepository.observeAll(),
        workoutRepository.observeActiveSession(),
        workoutRepository.observeFinishedSessions()
    ) { routines, active, finished ->
        val now = Instant.now()
        Triple(
            routines.firstOrNull { it.dayOfWeek == todayDow },
            active != null,
            finished.count { session ->
                DateUtils.parseInstantOrNull(session.startTime)
                    ?.let { DateUtils.isSameWeek(it, now) } == true
            }
        )
    }

    private val mondayString =
        today.with(DayOfWeek.MONDAY).format(DateTimeFormatter.ISO_LOCAL_DATE)

    private val nutritionFlow = combine(
        dailyLogRepository.observeLog(todayString),
        dailyLogRepository.observeLogsBetween(mondayString, todayString),
        dietRepository.observeActivePlan()
    ) { log, weekLogs, plan ->
        val goals = plan?.let {
            if (log?.isTrainingDay != false) it.goalsTraining else it.goalsRest
        } ?: MacroGoals()
        val weekly = ComplianceCalc.weeklyPercent(
            weekLogs.map { it.completedMeals to it.totalMeals }
        )
        listOf(log, goals, weekly)
    }

    private val bodyFlow = combine(
        progressRepository.observeWeights(),
        profileRepository.observeProfile()
    ) { weights, profile ->
        val sorted = weights.sortedWith(compareBy({ it.date }, { it.time }))
        val current = sorted.lastOrNull()?.weightKg
        val previous = sorted.dropLast(1).lastOrNull()?.weightKg
        val diff = if (current != null && previous != null) current - previous else null
        val checkDay = profile?.weeklyCheckDay ?: 0
        listOf(profile?.name.orEmpty(), current, diff, dayNames[checkDay])
    }

    val uiState: StateFlow<HomeUiState> = combine(
        trainingFlow, nutritionFlow, bodyFlow
    ) { training, nutrition, body ->
        val (todayRoutine, hasActive, weekSessions) = training
        @Suppress("UNCHECKED_CAST")
        val log = nutrition[0] as com.brunoapp.fittrack.domain.model.DailyLog?
        val goals = nutrition[1] as MacroGoals
        val weekly = nutrition[2] as Int?

        HomeUiState(
            userName = body[0] as String,
            todayRoutine = todayRoutine,
            hasActiveWorkout = hasActive,
            sessionsThisWeek = weekSessions,
            caloriesConsumed = log?.totals?.calories?.toInt() ?: 0,
            proteinConsumed = log?.totals?.protein?.toInt() ?: 0,
            goals = goals,
            mealsCompleted = log?.completedMeals ?: 0,
            mealsTotal = log?.totalMeals ?: 0,
            dayStarted = log != null,
            weeklyCompliance = weekly,
            currentWeight = body[1] as Double?,
            weightDiff = body[2] as Double?,
            nextWeighInDay = body[3] as String
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    /** Starts today's routine (or resumes the active session) and navigates. */
    fun onStartWorkout() {
        viewModelScope.launch {
            if (!workoutRepository.hasActiveSession()) {
                uiState.value.todayRoutine?.let {
                    workoutRepository.startFromRoutine(it.id)
                }
            }
            _navigateToWorkout.value = true
        }
    }

    fun onNavigatedToWorkout() {
        _navigateToWorkout.value = false
    }
}
