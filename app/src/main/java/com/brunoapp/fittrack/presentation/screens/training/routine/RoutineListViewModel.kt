package com.brunoapp.fittrack.presentation.screens.training.routine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.domain.model.Routine
import com.brunoapp.fittrack.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineListViewModel @Inject constructor(
    private val repository: RoutineRepository
) : ViewModel() {

    val routines: StateFlow<List<Routine>> = repository.observeAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onDuplicate(routine: Routine) {
        viewModelScope.launch { repository.duplicate(routine.id) }
    }

    fun onDelete(routine: Routine) {
        viewModelScope.launch { repository.delete(routine.id) }
    }
}
