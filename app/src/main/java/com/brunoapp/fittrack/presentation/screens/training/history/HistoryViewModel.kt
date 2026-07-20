package com.brunoapp.fittrack.presentation.screens.training.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.utils.DateUtils
import com.brunoapp.fittrack.domain.model.WorkoutSession
import com.brunoapp.fittrack.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import javax.inject.Inject

data class HistoryUiState(
    val sessions: List<WorkoutSession> = emptyList(),
    val sessionsThisWeek: Int = 0,
    val sessionsThisMonth: Int = 0,
    val volumeThisWeekKg: Double = 0.0
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    repository: WorkoutRepository
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = repository.observeFinishedSessions()
        .map { sessions ->
            val now = Instant.now()
            val thisWeek = sessions.filter { session ->
                DateUtils.parseInstantOrNull(session.startTime)
                    ?.let { DateUtils.isSameWeek(it, now) } == true
            }
            val thisMonth = sessions.count { session ->
                DateUtils.parseInstantOrNull(session.startTime)
                    ?.let { DateUtils.isSameMonth(it, now) } == true
            }
            HistoryUiState(
                sessions = sessions,
                sessionsThisWeek = thisWeek.size,
                sessionsThisMonth = thisMonth,
                volumeThisWeekKg = thisWeek.sumOf { it.totalVolumeKg }
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HistoryUiState()
        )
}
