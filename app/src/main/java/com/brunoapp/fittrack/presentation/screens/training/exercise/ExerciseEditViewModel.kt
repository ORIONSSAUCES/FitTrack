package com.brunoapp.fittrack.presentation.screens.training.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.constants.Equipment
import com.brunoapp.fittrack.core.constants.MuscleGroup
import com.brunoapp.fittrack.domain.model.Exercise
import com.brunoapp.fittrack.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseEditUiState(
    val name: String = "",
    val muscleGroup: MuscleGroup = MuscleGroup.CHEST,
    val secondaryMuscles: Set<MuscleGroup> = emptySet(),
    val equipment: Equipment = Equipment.BARBELL,
    val instructions: String = "",
    val personalNotes: String = "",
    val nameError: Boolean = false,
    val isSaved: Boolean = false,
    val isEditMode: Boolean = false
)

@HiltViewModel
class ExerciseEditViewModel @Inject constructor(
    private val repository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exerciseId: Long = savedStateHandle["exerciseId"] ?: -1L

    private val _uiState = MutableStateFlow(ExerciseEditUiState())
    val uiState: StateFlow<ExerciseEditUiState> = _uiState.asStateFlow()

    init {
        if (exerciseId > 0) {
            viewModelScope.launch {
                repository.getById(exerciseId)?.let { exercise ->
                    _uiState.value = ExerciseEditUiState(
                        name = exercise.name,
                        muscleGroup = exercise.muscleGroup,
                        secondaryMuscles = exercise.secondaryMuscles.toSet(),
                        equipment = exercise.equipment,
                        instructions = exercise.instructions,
                        personalNotes = exercise.personalNotes,
                        isEditMode = true
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) =
        update { it.copy(name = value, nameError = false) }

    fun onMuscleGroupChange(value: MuscleGroup) =
        update { it.copy(muscleGroup = value, secondaryMuscles = it.secondaryMuscles - value) }

    fun onSecondaryMuscleToggle(muscle: MuscleGroup) = update {
        if (muscle == it.muscleGroup) it
        else it.copy(
            secondaryMuscles = if (muscle in it.secondaryMuscles)
                it.secondaryMuscles - muscle else it.secondaryMuscles + muscle
        )
    }

    fun onEquipmentChange(value: Equipment) = update { it.copy(equipment = value) }

    fun onInstructionsChange(value: String) = update { it.copy(instructions = value) }

    fun onNotesChange(value: String) = update { it.copy(personalNotes = value) }

    fun onSaveClick() {
        val state = _uiState.value
        if (state.name.isBlank()) {
            update { it.copy(nameError = true) }
            return
        }
        viewModelScope.launch {
            repository.save(
                Exercise(
                    id = if (exerciseId > 0) exerciseId else 0,
                    name = state.name.trim(),
                    muscleGroup = state.muscleGroup,
                    secondaryMuscles = state.secondaryMuscles.toList(),
                    equipment = state.equipment,
                    instructions = state.instructions.trim(),
                    personalNotes = state.personalNotes.trim(),
                    isCustom = true
                )
            )
            update { it.copy(isSaved = true) }
        }
    }

    private fun update(transform: (ExerciseEditUiState) -> ExerciseEditUiState) {
        _uiState.value = transform(_uiState.value)
    }
}
