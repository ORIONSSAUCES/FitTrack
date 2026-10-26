package com.brunoapp.fittrack.presentation.screens.training.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.R
import com.brunoapp.fittrack.core.constants.SetType
import com.brunoapp.fittrack.core.utils.DateUtils
import com.brunoapp.fittrack.presentation.components.ExercisePickerFullScreen
import com.brunoapp.fittrack.presentation.theme.GoldRecord
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant

@Composable
fun ActiveWorkoutScreen(
    onExit: () -> Unit,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val sessionState by viewModel.sessionState.collectAsState()
    val finishing by viewModel.finishing.collectAsState()
    val restTimer by viewModel.restTimer.collectAsState()
    val restPaused by viewModel.restPausedRemaining.collectAsState()
    val restFinished by viewModel.restFinished.collectAsState()
    val newRecord by viewModel.newRecordEvent.collectAsState()
    val inputError by viewModel.inputError.collectAsState()
    val summary by viewModel.summary.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()
    val replaceTarget by viewModel.replaceTarget.collectAsState()
    val replaceWarning by viewModel.replaceWarning.collectAsState()
    val noteTarget by viewModel.noteTarget.collectAsState()
    // Observed so every edit recomposes the affected rows immediately
    val inputs by viewModel.inputs.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Single expanded exercise (accordion). Saved across rotation.
    var expandedId by rememberSaveable { mutableStateOf<Long?>(null) }
    var initialExpandDone by rememberSaveable { mutableStateOf(false) }

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
    LaunchedEffect(restFinished) {
        if (restFinished) {
            delay(4_000)
            viewModel.onRestFinishedShown()
        }
    }

    // ── Summary after finishing ──
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
                        SummaryRow(stringResource(R.string.workout_summary_records), "🏆 ${s.newRecords}")
                    }
                }
            },
            confirmButton = {
                Button(onClick = onExit) { Text(stringResource(R.string.action_confirm)) }
            }
        )
        return
    }

    // Still loading, or finishing (summary on its way)
    if (sessionState.isLoading || (sessionState.session == null && finishing)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val current = sessionState.session
    if (current == null) {
        LaunchedEffect(Unit) { onExit() }
        return
    }

    // Open the first unfinished exercise once
    LaunchedEffect(current.id) {
        if (!initialExpandDone) {
            expandedId = current.exercises.firstOrNull { it.status() != ExerciseStatus.COMPLETED }?.id
                ?: current.exercises.firstOrNull()?.id
            initialExpandDone = true
        }
    }

    // Elapsed time since the workout started (ticks every second)
    val startInstant = remember(current.startTime) { DateUtils.parseInstantOrNull(current.startTime) }
    val elapsedSeconds by produceState(initialValue = 0L, startInstant) {
        while (true) {
            value = startInstant?.let { Duration.between(it, Instant.now()).seconds.coerceAtLeast(0) } ?: 0L
            delay(1_000)
        }
    }

    val allSets = current.exercises.flatMap { it.sets }
    val exercisesDone = current.exercises.count { it.status() == ExerciseStatus.COMPLETED }
    val volume = allSets
        .filter { it.isCompleted && it.type != SetType.WARMUP }
        .sumOf { (it.weightKg ?: 0.0) * (it.reps ?: 0) }
    val allDone = current.exercises.isNotEmpty() && exercisesDone == current.exercises.size
    val tipsById = remember(allExercises) { allExercises.associate { it.id to it.instructions } }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(modifier = Modifier.statusBarsPadding()) {
                WorkoutHeader(
                    routineName = current.name,
                    elapsedSeconds = elapsedSeconds,
                    onMinimize = onExit,
                    onFinish = { showFinishDialog = true },
                    onDiscard = { showDiscardDialog = true }
                )
            }
        },
        bottomBar = {
            val timer = restTimer
            when {
                timer != null -> RestTimerBar(
                    remainingSeconds = restPaused ?: timer.remainingSeconds,
                    totalSeconds = timer.totalSeconds,
                    paused = restPaused != null,
                    onAdjust = viewModel::onAdjustRest,
                    onPauseResume = {
                        if (restPaused != null) viewModel.onResumeRest() else viewModel.onPauseRest()
                    },
                    onSkip = viewModel::onSkipRest
                )
                restFinished -> RestFinishedBar(onDismiss = viewModel::onRestFinishedShown)
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            item(key = "progress") {
                WorkoutProgressCard(
                    exercisesDone = exercisesDone,
                    exercisesTotal = current.exercises.size,
                    setsDone = allSets.count { it.isCompleted },
                    setsTotal = allSets.size,
                    volumeKg = volume,
                    elapsedSeconds = elapsedSeconds
                )
            }

            itemsIndexed(current.exercises, key = { _, ex -> ex.id }) { index, exercise ->
                val nextTarget = current.exercises
                    .drop(index + 1)
                    .firstOrNull { it.status() != ExerciseStatus.COMPLETED }
                    ?: current.exercises.firstOrNull {
                        it.id != exercise.id && it.status() != ExerciseStatus.COMPLETED
                    }

                ExerciseAccordionCard(
                    index = index,
                    exercise = exercise,
                    tip = tipsById[exercise.exerciseId],
                    expanded = expandedId == exercise.id,
                    onToggle = {
                        expandedId = if (expandedId == exercise.id) null else exercise.id
                    },
                    actions = SetTableActions(
                        currentInput = { set -> inputs[set.id] ?: viewModel.currentInput(set) },
                        placeholderWeight = { set -> viewModel.placeholderWeight(exercise, set, inputs) },
                        placeholderReps = viewModel::placeholderReps,
                        onWeightChange = viewModel::onWeightChange,
                        onRepsChange = viewModel::onRepsChange,
                        onConfirm = { set -> viewModel.onConfirmSet(exercise, set) },
                        onUncomplete = { set -> viewModel.onUncompleteSet(set.id) },
                        onRepeatPreviousWeight = { set -> viewModel.onRepeatPreviousWeight(exercise, set) },
                        onDeleteSet = { set -> viewModel.onRemoveSet(set.id) }
                    ),
                    onAddSet = { viewModel.onAddSet(exercise.id) },
                    onReplace = {
                        viewModel.onRequestReplace(exercise.id, exercise.sets.any { it.isCompleted })
                    },
                    onNote = { viewModel.onRequestNote(exercise.id) },
                    onNext = nextTarget?.let { target ->
                        {
                            expandedId = target.id
                            val targetIndex = current.exercises.indexOfFirst { it.id == target.id }
                            // +1 because the progress card is the first list item
                            scope.launch { listState.animateScrollToItem(targetIndex + 1) }
                            Unit
                        }
                    }
                )
            }

            item(key = "finish") {
                FinishWorkoutButton(highlighted = allDone, onClick = { showFinishDialog = true })
            }
        }
    }

    // ── Replace exercise ──
    if (replaceTarget != null) {
        ExercisePickerFullScreen(
            title = stringResource(R.string.workout_replace_exercise),
            exercises = allExercises,
            onSelect = viewModel::onPickReplacement,
            onDismiss = viewModel::onDismissReplacePicker
        )
    }

    if (replaceWarning != null) {
        AlertDialog(
            onDismissRequest = viewModel::onDismissReplaceWarning,
            title = { Text(stringResource(R.string.workout_replace_warning_title)) },
            text = { Text(stringResource(R.string.workout_replace_warning_message)) },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmReplaceWarning) {
                    Text(stringResource(R.string.workout_replace_confirm), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissReplaceWarning) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // ── Exercise note ──
    noteTarget?.let { targetId ->
        val exerciseNotes = current.exercises.firstOrNull { it.id == targetId }?.notes.orEmpty()
        var noteText by remember(targetId) { mutableStateOf(exerciseNotes) }
        AlertDialog(
            onDismissRequest = viewModel::onDismissNote,
            title = { Text(stringResource(R.string.workout_exercise_note)) },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text(stringResource(R.string.workout_note_hint)) },
                    minLines = 2
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onSaveNote(noteText) }) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissNote) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    // ── Discard / finish confirmations ──
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(stringResource(R.string.workout_discard_title)) },
            text = { Text(stringResource(R.string.workout_discard_message)) },
            confirmButton = {
                TextButton(onClick = { showDiscardDialog = false; viewModel.onDiscard() }) {
                    Text(stringResource(R.string.workout_discard), color = MaterialTheme.colorScheme.error)
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
        val pending = allSets.count { !it.isCompleted }
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text(stringResource(R.string.workout_finish_title)) },
            text = {
                Text(
                    if (pending > 0) stringResource(R.string.workout_finish_message_pending, pending)
                    else stringResource(R.string.workout_finish_message)
                )
            },
            confirmButton = {
                Button(onClick = { showFinishDialog = false; viewModel.onFinish() }) {
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
private fun FinishWorkoutButton(highlighted: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .height(52.dp)
            .clip(shape)
            .background(
                if (highlighted) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .clickable(onClick = onClick)
    ) {
        Icon(
            Icons.Filled.Flag, contentDescription = null,
            tint = if (highlighted) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = " " + stringResource(R.string.workout_finish_full),
            style = MaterialTheme.typography.titleMedium,
            color = if (highlighted) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurfaceVariant
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
