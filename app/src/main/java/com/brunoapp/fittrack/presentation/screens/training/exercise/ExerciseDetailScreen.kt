package com.brunoapp.fittrack.presentation.screens.training.exercise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.brunoapp.fittrack.core.utils.DateUtils
import com.brunoapp.fittrack.presentation.components.SimpleLineChart
import com.brunoapp.fittrack.presentation.theme.GoldRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    viewModel: ExerciseDetailViewModel = hiltViewModel()
) {
    val exercise by viewModel.exercise.collectAsState()
    val history by viewModel.history.collectAsState()
    val notesDraft by viewModel.notesDraft.collectAsState()
    val deleted by viewModel.deleted.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(deleted) {
        if (deleted) onBack()
    }

    val current = exercise ?: return

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(current.name, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onToggleFavorite) {
                        Icon(
                            imageVector = if (current.isFavorite) Icons.Filled.Star
                            else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = if (current.isFavorite) GoldRecord
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (current.isCustom) {
                        IconButton(onClick = { onEdit(current.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.action_edit))
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.action_delete),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Muscles + equipment
            InfoCard(title = stringResource(R.string.exercise_info)) {
                InfoRow(
                    label = stringResource(R.string.exercise_muscle_primary),
                    value = current.muscleGroup.displayName
                )
                if (current.secondaryMuscles.isNotEmpty()) {
                    InfoRow(
                        label = stringResource(R.string.exercise_muscle_secondary),
                        value = current.secondaryMuscles.joinToString(", ") { it.displayName }
                    )
                }
                InfoRow(
                    label = stringResource(R.string.exercise_equipment),
                    value = current.equipment.displayName
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Instructions
            if (current.instructions.isNotBlank()) {
                InfoCard(title = stringResource(R.string.exercise_instructions)) {
                    Text(
                        text = current.instructions,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Personal notes (editable inline)
            InfoCard(title = stringResource(R.string.exercise_notes)) {
                OutlinedTextField(
                    value = notesDraft ?: current.personalNotes,
                    onValueChange = viewModel::onNotesChange,
                    placeholder = { Text(stringResource(R.string.exercise_notes_hint)) },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                if (notesDraft != null && notesDraft != current.personalNotes) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = viewModel::onSaveNotes) {
                        Text(stringResource(R.string.action_save))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Records + progression
            InfoCard(title = stringResource(R.string.exercise_records)) {
                if (history.isEmpty()) {
                    Text(
                        text = stringResource(R.string.exercise_no_records),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val best = history.maxByOrNull { it.estimated1rm }
                    if (best != null) {
                        InfoRow(
                            label = stringResource(R.string.exercise_best_set),
                            value = "${formatWeight(best.weightKg)} kg × ${best.reps}"
                        )
                        InfoRow(
                            label = stringResource(R.string.exercise_best_1rm),
                            value = "${formatWeight(best.estimated1rm)} kg"
                        )
                    }

                    // Progression chart: best estimated 1RM per session
                    val bestPerSession = history
                        .groupBy { it.sessionId }
                        .toList()
                        .sortedBy { (_, sets) -> sets.first().date }
                        .map { (_, sets) -> sets.maxOf { it.estimated1rm } }

                    if (bestPerSession.size >= 2) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.exercise_progression),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SimpleLineChart(
                            values = bestPerSession,
                            minLabel = "${formatWeight(bestPerSession.min())} kg",
                            maxLabel = "${formatWeight(bestPerSession.max())} kg"
                        )
                    }

                    // Recent sets
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.exercise_recent_sets),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    history.takeLast(8).reversed().forEach { set ->
                        Text(
                            text = "${DateUtils.formatShortDate(set.date)}  ·  " +
                                "${formatWeight(set.weightKg)} kg × ${set.reps}" +
                                if (set.isPersonalRecord) "  🏆" else "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.exercise_delete_title)) },
            text = { Text(stringResource(R.string.exercise_delete_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.onDelete()
                    }
                ) {
                    Text(
                        stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


private fun formatWeight(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString()
    else "%.1f".format(value)
