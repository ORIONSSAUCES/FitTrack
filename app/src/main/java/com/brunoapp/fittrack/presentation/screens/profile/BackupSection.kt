package com.brunoapp.fittrack.presentation.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.R
import java.time.LocalDate

@Composable
fun BackupSection(
    snackbarHostState: SnackbarHostState,
    viewModel: BackupViewModel = hiltViewModel()
) {
    val event by viewModel.event.collectAsState()
    val busy by viewModel.busy.collectAsState()
    var showImportConfirm by remember { mutableStateOf(false) }

    val exportOkMsg = stringResource(R.string.backup_export_ok)
    val importOkMsg = stringResource(R.string.backup_import_ok)
    val csvOkMsg = stringResource(R.string.backup_csv_ok)
    val failedPrefix = stringResource(R.string.backup_failed)

    LaunchedEffect(event) {
        event?.let { e ->
            val message = when (e) {
                is BackupUiEvent.ExportOk -> exportOkMsg
                is BackupUiEvent.ImportOk -> importOkMsg
                is BackupUiEvent.CsvOk -> csvOkMsg
                is BackupUiEvent.Failed -> "$failedPrefix: ${e.message}"
            }
            snackbarHostState.showSnackbar(message)
            viewModel.onEventShown()
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> viewModel.onExport(uri) }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> viewModel.onImport(uri) }

    val weightsCsvLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri -> viewModel.onExportWeightsCsv(uri) }

    val workoutsCsvLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri -> viewModel.onExportWorkoutsCsv(uri) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.backup_section_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.backup_section_hint),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            val today = LocalDate.now().toString()
            Button(
                onClick = { exportLauncher.launch("fittrack_backup_$today.json") },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.backup_export)) }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showImportConfirm = true },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.backup_import)) }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { weightsCsvLauncher.launch("fittrack_pesos_$today.csv") },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.backup_csv_weights)) }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { workoutsCsvLauncher.launch("fittrack_entrenos_$today.csv") },
                enabled = !busy,
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.backup_csv_workouts)) }
        }
    }

    if (showImportConfirm) {
        AlertDialog(
            onDismissRequest = { showImportConfirm = false },
            title = { Text(stringResource(R.string.backup_import_title)) },
            text = { Text(stringResource(R.string.backup_import_warning)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImportConfirm = false
                        importLauncher.launch(arrayOf("application/json", "application/octet-stream"))
                    }
                ) {
                    Text(
                        stringResource(R.string.backup_import_confirm),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirm = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}
