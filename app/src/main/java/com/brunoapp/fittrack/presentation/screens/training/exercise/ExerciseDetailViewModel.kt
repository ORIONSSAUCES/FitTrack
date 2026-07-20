package com.brunoapp.fittrack.presentation.screens.training.exercise

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.domain.model.Exercise
import com.brunoapp.fittrack.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    private val repository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val exerciseId: Long = checkNotNull(savedStateHandle["exerciseId"])

    val exercise: StateFlow<Exercise?> = repository.observeById(exerciseId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _notesDraft = MutableStateFlow<String?>(null)
    val notesDraft: StateFlow<String?> = _notesDraft.asStateFlow()

    private val _deleted = MutableStateFlow(false)
    val deleted: StateFlow<Boolean> = _deleted.asStateFlow()

    fun onNotesChange(notes: String) {
        _notesDraft.value = notes
    }

    fun onSaveNotes() {
        val notes = _notesDraft.value ?: return
        viewModelScope.launch {
            repository.updateNotes(exerciseId, notes.trim())
            _notesDraft.value = null
        }
    }

    fun onToggleFavorite() {
        val current = exercise.value ?: return
        viewModelScope.launch {
            repository.setFavorite(current.id, !current.isFavorite)
        }
    }

    fun onDelete() {
        val current = exercise.value ?: return
        if (!current.isCustom) return
        viewModelScope.launch {
            repository.delete(current)
            _deleted.value = true
        }
    }
}
