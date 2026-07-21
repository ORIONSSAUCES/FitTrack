package com.brunoapp.fittrack.presentation.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.data.backup.BackupManager
import com.brunoapp.fittrack.data.backup.BackupResult
import com.brunoapp.fittrack.data.backup.CsvExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BackupUiEvent {
    data object ExportOk : BackupUiEvent()
    data object ImportOk : BackupUiEvent()
    data object CsvOk : BackupUiEvent()
    data class Failed(val message: String) : BackupUiEvent()
}

@HiltViewModel
class BackupViewModel @Inject constructor(
    private val backupManager: BackupManager,
    private val csvExporter: CsvExporter
) : ViewModel() {

    private val _event = MutableStateFlow<BackupUiEvent?>(null)
    val event: StateFlow<BackupUiEvent?> = _event.asStateFlow()

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    fun onExport(uri: Uri?) {
        uri ?: return
        launchWork {
            when (val r = backupManager.exportToUri(uri)) {
                is BackupResult.Success -> BackupUiEvent.ExportOk
                is BackupResult.Error -> BackupUiEvent.Failed(r.message)
            }
        }
    }

    fun onImport(uri: Uri?) {
        uri ?: return
        launchWork {
            when (val r = backupManager.importFromUri(uri)) {
                is BackupResult.Success -> BackupUiEvent.ImportOk
                is BackupResult.Error -> BackupUiEvent.Failed(r.message)
            }
        }
    }

    fun onExportWeightsCsv(uri: Uri?) {
        uri ?: return
        launchWork {
            when (val r = csvExporter.exportWeights(uri)) {
                is BackupResult.Success -> BackupUiEvent.CsvOk
                is BackupResult.Error -> BackupUiEvent.Failed(r.message)
            }
        }
    }

    fun onExportWorkoutsCsv(uri: Uri?) {
        uri ?: return
        launchWork {
            when (val r = csvExporter.exportWorkouts(uri)) {
                is BackupResult.Success -> BackupUiEvent.CsvOk
                is BackupResult.Error -> BackupUiEvent.Failed(r.message)
            }
        }
    }

    fun onEventShown() {
        _event.value = null
    }

    private fun launchWork(work: suspend () -> BackupUiEvent) {
        viewModelScope.launch {
            _busy.value = true
            _event.value = work()
            _busy.value = false
        }
    }
}
