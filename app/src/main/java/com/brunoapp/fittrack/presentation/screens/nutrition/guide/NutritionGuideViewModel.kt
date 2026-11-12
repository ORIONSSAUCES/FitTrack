package com.brunoapp.fittrack.presentation.screens.nutrition.guide

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.utils.GuideProgress
import com.brunoapp.fittrack.core.utils.Validators
import com.brunoapp.fittrack.data.datastore.UserPreferences
import com.brunoapp.fittrack.data.guide.NutritionGuide
import com.brunoapp.fittrack.domain.model.WeightEntry
import com.brunoapp.fittrack.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class GuideUiState(
    val loading: Boolean = true,
    val phase: Int = 1,
    val currentWeight: Double? = null,
    val weeklyLoss: Double? = null,
    val pace: GuideProgress.Pace = GuideProgress.Pace.NO_DATA,
    val weeksLeft: Int? = null,
    val notes: String = ""
) {
    val reachedTarget: Boolean
        get() = currentWeight != null && currentWeight <= NutritionGuide.TARGET_WEIGHT_KG
    val remainingKg: Double
        get() = ((currentWeight ?: NutritionGuide.START_WEIGHT_KG) - NutritionGuide.TARGET_WEIGHT_KG)
            .coerceAtLeast(0.0)
    val progress: Float
        get() = GuideProgress.fraction(
            NutritionGuide.START_WEIGHT_KG,
            currentWeight ?: NutritionGuide.START_WEIGHT_KG,
            NutritionGuide.TARGET_WEIGHT_KG
        )
}

@HiltViewModel
class NutritionGuideViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<GuideUiState> = combine(
        progressRepository.observeWeights(),
        userPreferences.guidePhase,
        userPreferences.guideNotes
    ) { weights, phase, notes ->
        val today = LocalDate.now()
        val parsed = weights.mapNotNull { entry ->
            runCatching { LocalDate.parse(entry.date) to entry.weightKg }.getOrNull()
        }
        val latest = weights.maxWithOrNull(compareBy({ it.date }, { it.time }))?.weightKg
        val loss = GuideProgress.weeklyLoss(parsed, today)
        GuideUiState(
            loading = false,
            phase = phase,
            currentWeight = latest,
            weeklyLoss = loss,
            pace = GuideProgress.pace(loss),
            weeksLeft = latest?.let {
                GuideProgress.weeksLeft(it, NutritionGuide.TARGET_WEIGHT_KG, loss)
            },
            notes = notes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), GuideUiState())

    /** Plan preset of this install; "LOADING" until DataStore answers. */
    val planPreset: StateFlow<String> = userPreferences.planPreset
        .map { it ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "LOADING")

    /** Returns false when the value is invalid. */
    fun onLogWeight(text: String): Boolean {
        val weight = Validators.parseBodyWeight(text).getOrNull() ?: return false
        viewModelScope.launch {
            progressRepository.saveWeight(
                WeightEntry(
                    date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
                    time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
                    weightKg = weight,
                    notes = "Guía"
                )
            )
        }
        return true
    }

    fun onStartPhase2() {
        viewModelScope.launch { userPreferences.setGuidePhase(2) }
    }

    fun onBackToPhase1() {
        viewModelScope.launch { userPreferences.setGuidePhase(1) }
    }

    fun onSaveNotes(notes: String) {
        viewModelScope.launch { userPreferences.setGuideNotes(notes.trim()) }
    }
}
