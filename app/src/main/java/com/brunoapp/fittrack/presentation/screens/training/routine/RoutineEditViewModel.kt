package com.brunoapp.fittrack.presentation.screens.training.routine

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.constants.SetType
import com.brunoapp.fittrack.core.utils.ListUtils
import com.brunoapp.fittrack.domain.model.Exercise
import com.brunoapp.fittrack.domain.model.Routine
import com.brunoapp.fittrack.domain.model.RoutineExercise
import com.brunoapp.fittrack.domain.model.SetTemplate
import com.brunoapp.fittrack.domain.repository.ExerciseRepository
import com.brunoapp.fittrack.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** In-memory editable representation of one exercise inside the routine. */
data class EditableExercise(
    val exerciseId: Long,
    val exerciseName: String,
    val restSeconds: Int = 120,
    val notes: String = "",
    val sets: List<EditableSet> = listOf(EditableSet(), EditableSet(), EditableSet())
)

data class EditableSet(
    val type: SetType = SetType.NORMAL,
    val repsMin: Int = 8,
    val repsMax: Int = 12,
    val targetRir: Int? = null
)

data class RoutineEditUiState(
    val name: String = "",
    val description: String = "",
    val dayOfWeek: Int? = null,
    val exercises: List<EditableExercise> = emptyList(),
    val nameError: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false,
    val showExercisePicker: Boolean = false,
    val pickerQuery: String = ""
)

@HiltViewModel
class RoutineEditViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val routineId: Long = savedStateHandle["routineId"] ?: -1L

    private val _uiState = MutableStateFlow(RoutineEditUiState())
    val uiState: StateFlow<RoutineEditUiState> = _uiState.asStateFlow()

    val allExercises: StateFlow<List<Exercise>> = exerciseRepository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    init {
        if (routineId > 0) {
            viewModelScope.launch {
                routineRepository.getById(routineId)?.let { routine ->
                    _uiState.value = RoutineEditUiState(
                        name = routine.name,
                        description = routine.description,
                        dayOfWeek = routine.dayOfWeek,
                        exercises = routine.exercises.map { exercise ->
                            EditableExercise(
                                exerciseId = exercise.exerciseId,
                                exerciseName = exercise.exerciseName,
                                restSeconds = exercise.restSeconds,
                                notes = exercise.notes,
                                sets = exercise.sets.map { set ->
                                    EditableSet(
                                        type = set.type,
                                        repsMin = set.repsMin,
                                        repsMax = set.repsMax,
                                        targetRir = set.targetRir
                                    )
                                }
                            )
                        },
                        isEditMode = true
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) = update { it.copy(name = value, nameError = false) }
    fun onDescriptionChange(value: String) = update { it.copy(description = value) }
    fun onDayChange(day: Int?) = update { it.copy(dayOfWeek = day) }

    fun onShowPicker() = update { it.copy(showExercisePicker = true, pickerQuery = "") }
    fun onDismissPicker() = update { it.copy(showExercisePicker = false) }
    fun onPickerQueryChange(query: String) = update { it.copy(pickerQuery = query) }

    fun onAddExercise(exercise: Exercise) = update {
        it.copy(
            exercises = it.exercises + EditableExercise(
                exerciseId = exercise.id,
                exerciseName = exercise.name
            ),
            showExercisePicker = false
        )
    }

    fun onRemoveExercise(index: Int) = update {
        it.copy(exercises = it.exercises.filterIndexed { i, _ -> i != index })
    }

    fun onMoveExercise(from: Int, to: Int) = update {
        it.copy(exercises = ListUtils.move(it.exercises, from, to))
    }

    fun onRestChange(index: Int, seconds: Int) = updateExercise(index) {
        it.copy(restSeconds = seconds.coerceIn(0, 600))
    }

    fun onExerciseNotesChange(index: Int, notes: String) = updateExercise(index) {
        it.copy(notes = notes)
    }

    fun onAddSet(exerciseIndex: Int) = updateExercise(exerciseIndex) {
        val last = it.sets.lastOrNull() ?: EditableSet()
        it.copy(sets = it.sets + last.copy())
    }

    fun onRemoveSet(exerciseIndex: Int, setIndex: Int) = updateExercise(exerciseIndex) {
        if (it.sets.size <= 1) it
        else it.copy(sets = it.sets.filterIndexed { i, _ -> i != setIndex })
    }

    fun onSetTypeChange(exerciseIndex: Int, setIndex: Int, type: SetType) =
        updateSet(exerciseIndex, setIndex) { it.copy(type = type) }

    fun onSetRepsMinChange(exerciseIndex: Int, setIndex: Int, value: Int) =
        updateSet(exerciseIndex, setIndex) {
            it.copy(repsMin = value.coerceIn(1, 200), repsMax = maxOf(it.repsMax, value.coerceIn(1, 200)))
        }

    fun onSetRepsMaxChange(exerciseIndex: Int, setIndex: Int, value: Int) =
        updateSet(exerciseIndex, setIndex) {
            it.copy(repsMax = value.coerceIn(1, 200), repsMin = minOf(it.repsMin, value.coerceIn(1, 200)))
        }

    fun onSetRirChange(exerciseIndex: Int, setIndex: Int, rir: Int?) =
        updateSet(exerciseIndex, setIndex) { it.copy(targetRir = rir?.coerceIn(0, 10)) }

    fun onSaveClick() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            update { it.copy(nameError = true) }
            return
        }
        viewModelScope.launch {
            routineRepository.save(
                Routine(
                    id = if (routineId > 0) routineId else 0,
                    name = state.name.trim(),
                    description = state.description.trim(),
                    dayOfWeek = state.dayOfWeek,
                    exercises = state.exercises.mapIndexed { index, exercise ->
                        RoutineExercise(
                            exerciseId = exercise.exerciseId,
                            position = index,
                            restSeconds = exercise.restSeconds,
                            notes = exercise.notes.trim(),
                            sets = exercise.sets.mapIndexed { setIndex, set ->
                                SetTemplate(
                                    setNumber = setIndex + 1,
                                    type = set.type,
                                    repsMin = set.repsMin,
                                    repsMax = set.repsMax,
                                    targetRir = set.targetRir
                                )
                            }
                        )
                    }
                )
            )
            update { it.copy(isSaved = true) }
        }
    }

    private fun update(transform: (RoutineEditUiState) -> RoutineEditUiState) {
        _uiState.value = transform(_uiState.value)
    }

    private fun updateExercise(index: Int, transform: (EditableExercise) -> EditableExercise) =
        update { state ->
            state.copy(
                exercises = state.exercises.mapIndexed { i, exercise ->
                    if (i == index) transform(exercise) else exercise
                }
            )
        }

    private fun updateSet(
        exerciseIndex: Int,
        setIndex: Int,
        transform: (EditableSet) -> EditableSet
    ) = updateExercise(exerciseIndex) { exercise ->
        exercise.copy(
            sets = exercise.sets.mapIndexed { i, set ->
                if (i == setIndex) transform(set) else set
            }
        )
    }
}
