package com.brunoapp.fittrack.presentation.screens.training.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.domain.model.Routine
import com.brunoapp.fittrack.domain.model.WorkoutSession
import com.brunoapp.fittrack.domain.repository.RoutineRepository
import com.brunoapp.fittrack.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineListViewModel @Inject constructor(
    private val repository: RoutineRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    val routines: StateFlow<List<Routine>> = repository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val activeSession: StateFlow<WorkoutSession?> = workoutRepository.observeActiveSession()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    /** Routine waiting for confirmation because another workout is active. */
    private val _pendingStart = MutableStateFlow<Routine?>(null)
    val pendingStart: StateFlow<Routine?> = _pendingStart.asStateFlow()

    private val _navigateToWorkout = MutableStateFlow(false)
    val navigateToWorkout: StateFlow<Boolean> = _navigateToWorkout.asStateFlow()

    fun onStartWorkout(routine: Routine) {
        viewModelScope.launch {
            if (workoutRepository.hasActiveSession()) {
                _pendingStart.value = routine
            } else {
                workoutRepository.startFromRoutine(routine.id)
                _navigateToWorkout.value = true
            }
        }
    }

    /** User chose to discard the active session and start the pending routine. */
    fun onConfirmReplaceActive() {
        val routine = _pendingStart.value ?: return
        viewModelScope.launch {
            workoutRepository.discardSession()
            workoutRepository.startFromRoutine(routine.id)
            _pendingStart.value = null
            _navigateToWorkout.value = true
        }
    }

    fun onContinueActive() {
        _pendingStart.value = null
        _navigateToWorkout.value = true
    }

    fun onDismissPendingStart() {
        _pendingStart.value = null
    }

    fun onNavigatedToWorkout() {
        _navigateToWorkout.value = false
    }

    fun onDuplicate(routine: Routine) {
        viewModelScope.launch { repository.duplicate(routine.id) }
    }

    fun onDelete(routine: Routine) {
        viewModelScope.launch { repository.delete(routine.id) }
    }
}
