package com.brunoapp.fittrack.presentation.screens.training.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.domain.model.WorkoutSession
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
class WorkoutDetailViewModel @Inject constructor(
    private val repository: WorkoutRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = checkNotNull(savedStateHandle["sessionId"])

    val session: StateFlow<WorkoutSession?> = repository.observeSession(sessionId)
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
            repository.updateSessionNotes(sessionId, notes.trim())
            _notesDraft.value = null
        }
    }

    fun onDelete() {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
            _deleted.value = true
        }
    }
}
