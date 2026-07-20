package com.brunoapp.fittrack.presentation.screens.training.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.R
import com.brunoapp.fittrack.core.constants.SetType
import com.brunoapp.fittrack.domain.model.WorkoutExercise
import com.brunoapp.fittrack.domain.model.WorkoutSet
import com.brunoapp.fittrack.presentation.theme.GoldRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onExit: () -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val sessionState by viewModel.sessionState.collectAsState()
    val finishing by viewModel.finishing.collectAsState()
    val restTimer by viewModel.restTimer.collectAsState()
    val newRecord by viewModel.newRecordEvent.collectAsState()
    val inputError by viewModel.inputError.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    val recordMessage = stringResource(R.string.workout_new_record)
    val errorMessage = stringResource(R.string.workout_input_error)

    LaunchedEffect(newRecord) {
        if (newRecord) {
            snackbarHostState.showSnackbar(recordMessage)
            viewModel.onRecordEventShown()
        }
    }
    LaunchedEffect(inputError) {
        if (inputError) {
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.onInputErrorShown()
        }
    }

    // Summary dialog after finishing
    summary?.let { s ->
        AlertDialog(
            onDismissRequest = onExit,
            icon = { Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = GoldRecord) },
            title = { Text(stringResource(R.string.workout_summary_title)) },
            text = {
                Column {
                    SummaryRow(stringResource(R.string.workout_summary_duration), "${s.durationMinutes} min")
                    SummaryRow(stringResource(R.string.workout_summary_exercises), "${s.exercisesDone}")
                    SummaryRow(stringResource(R.string.workout_summary_sets), "${s.setsCompleted}")
                    SummaryRow(stringResource(R.string.workout_summary_volume), "${s.totalVolumeKg.toInt()} kg")
                    if (s.newRecords > 0) {
                        SummaryRow(
                            stringResource(R.string.workout_summary_records),
                            "🏆 ${s.newRecords}"
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = onExit) { Text(stringResource(R.string.action_confirm)) }
            }
        )
        return
    }

    // Still loading from DB, or finishing (summary on its way): show a spinner
    if (sessionState.isLoading || (sessionState.session == null && finishing)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val current = sessionState.session
    if (current == null) {
        // Confirmed: no active session (discarded) — leave the screen
        LaunchedEffect(Unit) { onExit() }
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(current.name, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onExit) {
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = stringResource(R.string.workout_minimize)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { showDiscardDialog = true }) {
                        Text(
                            stringResource(R.string.workout_discard),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Button(onClick = { showFinishDialog = true }) {
                        Text(stringResource(R.string.workout_finish))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            restTimer?.let { timer ->
                RestTimerBar(
                    timer = timer,
                    onSkip = viewModel::onSkipRest,
                    onAdjust = viewModel::onAdjustRest
                )
            }
        }
    ) { padding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            items(current.exercises.size) { index ->
                ExerciseWorkoutCard(
                    exercise = current.exercises[index],
                    viewModel = viewModel
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.workout_discard_title)) },
            text = { Text(stringResource(R.string.workout_discard_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        viewModel.onDiscard()
                    }
                ) {
                    Text(
                        stringResource(R.string.workout_discard),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text(stringResource(R.string.workout_finish_title)) },
            text = { Text(stringResource(R.string.workout_finish_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showFinishDialog = false
                        viewModel.onFinish()
                    }
                ) {
                    Text(stringResource(R.string.workout_finish))
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RestTimerBar(
    timer: RestTimerState,
    onSkip: () -> Unit,
    onAdjust: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        LinearProgressIndicator(
            progress = {
                if (timer.totalSeconds > 0)
                    timer.remainingSeconds.toFloat() / timer.totalSeconds
                else 0f
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatTime(timer.remainingSeconds),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(onClick = { onAdjust(-15) }) { Text("-15 s") }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { onAdjust(15) }) { Text("+15 s") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onSkip) { Text(stringResource(R.string.workout_skip_rest)) }
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
private fun ExerciseWorkoutCard(
    exercise: WorkoutExercise,
    viewModel: ActiveWorkoutViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = exercise.exerciseName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (exercise.notes.isNotBlank()) {
                Text(
                    text = exercise.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Header row
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "#",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(28.dp)
                )
                Text(
                    text = stringResource(R.string.workout_previous),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1.1f)
                )
                Text(
                    text = "kg",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "reps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(88.dp))
            }

            exercise.sets.forEach { set ->
                SetRow(set = set, exercise = exercise, viewModel = viewModel)
            }

            TextButton(onClick = { viewModel.onAddSet(exercise.id) }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.routine_add_set))
            }
        }
    }
}

@Composable
private fun SetRow(
    set: WorkoutSet,
    exercise: WorkoutExercise,
    viewModel: ActiveWorkoutViewModel
) {
    val inputs by viewModel.inputs.collectAsState()
    val input = inputs[set.id] ?: viewModel.inputFor(set.id, set.weightKg, set.reps, set.rir)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Set number + type indicator
        Column(modifier = Modifier.width(28.dp)) {
            Text(
                text = "${set.setNumber}",
                style = MaterialTheme.typography.titleMedium,
                color = when (set.type) {
                    SetType.WARMUP -> MaterialTheme.colorScheme.onSurfaceVariant
                    SetType.FAILURE -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.primary
                }
            )
            if (set.isPersonalRecord) {
                Icon(
                    Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = GoldRecord,
                    modifier = Modifier.width(16.dp)
                )
            }
        }

        // Previous
        Text(
            text = if (set.previousWeightKg != null && set.previousReps != null)
                "${formatW(set.previousWeightKg)}×${set.previousReps}"
            else "${set.targetRepsMin}–${set.targetRepsMax}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = if (set.previousWeightKg == null) TextDecoration.None else TextDecoration.None,
            modifier = Modifier.weight(1.1f)
        )

        // Weight input
        OutlinedTextField(
            value = input.weightText,
            onValueChange = { viewModel.onWeightChange(set.id, it) },
            singleLine = true,
            enabled = !set.isCompleted,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        )

        // Reps input
        OutlinedTextField(
            value = input.repsText,
            onValueChange = { viewModel.onRepsChange(set.id, it) },
            singleLine = true,
            enabled = !set.isCompleted,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        )

        // Complete / uncomplete
        IconButton(
            onClick = {
                if (set.isCompleted) viewModel.onUncompleteSet(set.id)
                else viewModel.onCompleteSet(set.id, set.weightKg, set.reps, exercise.restSeconds)
            }
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = stringResource(R.string.workout_complete_set),
                tint = if (set.isCompleted) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Remove (only incomplete sets)
        IconButton(
            onClick = { viewModel.onRemoveSet(set.id) },
            enabled = !set.isCompleted
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatW(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
