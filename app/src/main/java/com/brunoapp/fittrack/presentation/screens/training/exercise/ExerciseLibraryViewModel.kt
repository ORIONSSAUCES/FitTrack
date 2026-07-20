package com.brunoapp.fittrack.presentation.screens.training.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.constants.Equipment
import com.brunoapp.fittrack.core.constants.MuscleGroup
import com.brunoapp.fittrack.core.utils.ExerciseFilter
import com.brunoapp.fittrack.domain.model.Exercise
import com.brunoapp.fittrack.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseFilterState(
    val query: String = "",
    val muscleGroup: MuscleGroup? = null,
    val equipment: Equipment? = null,
    val favoritesOnly: Boolean = false
)

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(ExerciseFilterState())
    val filter: StateFlow<ExerciseFilterState> = _filter.asStateFlow()

    val exercises: StateFlow<List<Exercise>> =
        combine(repository.observeAll(), _filter) { all, filter ->
            ExerciseFilter.apply(
                exercises = all,
                query = filter.query,
                muscleGroup = filter.muscleGroup,
                equipment = filter.equipment,
                favoritesOnly = filter.favoritesOnly
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onQueryChange(query: String) = _filter.update { it.copy(query = query) }

    fun onMuscleGroupSelect(muscle: MuscleGroup?) =
        _filter.update { it.copy(muscleGroup = if (it.muscleGroup == muscle) null else muscle) }

    fun onEquipmentSelect(equipment: Equipment?) =
        _filter.update { it.copy(equipment = if (it.equipment == equipment) null else equipment) }

    fun onToggleFavoritesOnly() =
        _filter.update { it.copy(favoritesOnly = !it.favoritesOnly) }

    fun onToggleFavorite(exercise: Exercise) {
        viewModelScope.launch {
            repository.setFavorite(exercise.id, !exercise.isFavorite)
        }
    }

    private fun MutableStateFlow<ExerciseFilterState>.update(
        transform: (ExerciseFilterState) -> ExerciseFilterState
    ) {
        value = transform(value)
    }
}
