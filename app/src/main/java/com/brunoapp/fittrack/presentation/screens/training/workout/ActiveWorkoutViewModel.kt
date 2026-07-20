package com.brunoapp.fittrack.presentation.screens.training.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.utils.Validators
import com.brunoapp.fittrack.domain.model.WorkoutSession
import com.brunoapp.fittrack.domain.model.WorkoutSummary
import com.brunoapp.fittrack.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Local text inputs for one set (kept out of DB until completion). */
data class SetInput(
    val weightText: String = "",
    val repsText: String = "",
    val rirText: String = ""
)

data class RestTimerState(
    val remainingSeconds: Int,
    val totalSeconds: Int
)

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val repository: WorkoutRepository
) : ViewModel() {

    val session: StateFlow<WorkoutSession?> = repository.observeActiveSession()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _inputs = MutableStateFlow<Map<Long, SetInput>>(emptyMap())
    val inputs: StateFlow<Map<Long, SetInput>> = _inputs.asStateFlow()

    private val _restTimer = MutableStateFlow<RestTimerState?>(null)
    val restTimer: StateFlow<RestTimerState?> = _restTimer.asStateFlow()

    private val _newRecordEvent = MutableStateFlow(false)
    val newRecordEvent: StateFlow<Boolean> = _newRecordEvent.asStateFlow()

    private val _inputError = MutableStateFlow(false)
    val inputError: StateFlow<Boolean> = _inputError.asStateFlow()

    private val _summary = MutableStateFlow<WorkoutSummary?>(null)
    val summary: StateFlow<WorkoutSummary?> = _summary.asStateFlow()

    private var timerJob: Job? = null
    private var restEndTimeMs: Long? = null
    private var restTotalSeconds: Int = 0

    init {
        // Restore rest timer after process death
        viewModelScope.launch {
            repository.getRestTimer()?.let { (endMs, total) ->
                restEndTimeMs = endMs
                restTotalSeconds = total
                startTicker()
            }
        }
    }

    // ── Inputs ──

    fun onWeightChange(setId: Long, value: String) = updateInput(setId) { it.copy(weightText = value) }
    fun onRepsChange(setId: Long, value: String) = updateInput(setId) { it.copy(repsText = value) }
    fun onRirChange(setId: Long, value: String) = updateInput(setId) { it.copy(rirText = value) }

    /** Effective input for a set: local edit, or values already stored on the set. */
    fun inputFor(setId: Long, storedWeight: Double?, storedReps: Int?, storedRir: Int?): SetInput {
        _inputs.value[setId]?.let { return it }
        return SetInput(
            weightText = storedWeight?.let { formatWeight(it) }.orEmpty(),
            repsText = storedReps?.toString().orEmpty(),
            rirText = storedRir?.toString().orEmpty()
        )
    }

    // ── Set actions ──

    fun onCompleteSet(setId: Long, storedWeight: Double?, storedReps: Int?, restSeconds: Int) {
        val input = inputFor(setId, storedWeight, storedReps, null)
        val weight = Validators.parseLiftWeight(input.weightText).getOrNull()
        val reps = Validators.parseReps(input.repsText).getOrNull()
        val rir = input.rirText.trim().toIntOrNull()?.coerceIn(0, 10)

        if (weight == null || reps == null) {
            _inputError.value = true
            return
        }

        viewModelScope.launch {
            val result = repository.completeSet(setId, weight, reps, rir)
            if (result.isNewRecord) _newRecordEvent.value = true
            startRest(restSeconds)
        }
    }

    fun onUncompleteSet(setId: Long) {
        viewModelScope.launch { repository.uncompleteSet(setId) }
    }

    fun onAddSet(workoutExerciseId: Long) {
        viewModelScope.launch { repository.addSet(workoutExerciseId) }
    }

    fun onRemoveSet(setId: Long) {
        viewModelScope.launch { repository.removeSet(setId) }
    }

    fun onRecordEventShown() { _newRecordEvent.value = false }
    fun onInputErrorShown() { _inputError.value = false }

    // ── Rest timer ──

    fun startRest(seconds: Int) {
        if (seconds <= 0) return
        restEndTimeMs = System.currentTimeMillis() + seconds * 1000L
        restTotalSeconds = seconds
        viewModelScope.launch { repository.saveRestTimer(restEndTimeMs, seconds) }
        startTicker()
    }

    fun onSkipRest() {
        timerJob?.cancel()
        restEndTimeMs = null
        _restTimer.value = null
        viewModelScope.launch { repository.saveRestTimer(null, null) }
    }

    fun onAdjustRest(deltaSeconds: Int) {
        val end = restEndTimeMs ?: return
        val newEnd = maxOf(System.currentTimeMillis(), end + deltaSeconds * 1000L)
        restEndTimeMs = newEnd
        restTotalSeconds = maxOf(restTotalSeconds + deltaSeconds, 1)
        viewModelScope.launch { repository.saveRestTimer(newEnd, restTotalSeconds) }
        startTicker()
    }

    private fun startTicker() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val end = restEndTimeMs ?: break
                val remaining = ((end - System.currentTimeMillis()) / 1000L).toInt()
                if (remaining <= 0) {
                    _restTimer.value = null
                    restEndTimeMs = null
                    repository.saveRestTimer(null, null)
                    break
                }
                _restTimer.value = RestTimerState(remaining, restTotalSeconds)
                delay(250L)
            }
        }
    }

    // ── Finish / discard ──

    fun onFinish() {
        viewModelScope.launch {
            onSkipRest()
            _summary.value = repository.finishSession()
        }
    }

    fun onDiscard() {
        viewModelScope.launch {
            onSkipRest()
            repository.discardSession()
        }
    }

    private fun updateInput(setId: Long, transform: (SetInput) -> SetInput) {
        val current = _inputs.value[setId] ?: SetInput()
        _inputs.value = _inputs.value + (setId to transform(current))
    }

    private fun formatWeight(value: Double): String =
        if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
}
