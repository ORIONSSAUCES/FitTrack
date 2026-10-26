package com.brunoapp.fittrack.presentation.screens.training.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.brunoapp.fittrack.R
import com.brunoapp.fittrack.core.constants.SetType
import com.brunoapp.fittrack.core.utils.SetEntryResolver
import com.brunoapp.fittrack.domain.model.WorkoutExercise
import com.brunoapp.fittrack.domain.model.WorkoutSet
import com.brunoapp.fittrack.presentation.components.AssetImage
import com.brunoapp.fittrack.presentation.theme.GoldRecord

// ═════════════════════════ Shared helpers ═════════════════════════

enum class ExerciseStatus { PENDING, IN_PROGRESS, COMPLETED }

fun WorkoutExercise.status(): ExerciseStatus = when {
    sets.isNotEmpty() && sets.all { it.isCompleted } -> ExerciseStatus.COMPLETED
    sets.any { it.isCompleted } -> ExerciseStatus.IN_PROGRESS
    else -> ExerciseStatus.PENDING
}

internal fun formatKg(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else "%.1f".format(value).replace(",", ".")

internal fun formatClock(totalSeconds: Long): String {
    val h = totalSeconds / 3600
    val m = (totalSeconds % 3600) / 60
    val s = totalSeconds % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

// ═════════════════════════ Header ═════════════════════════

/**
 * Top bar: day + muscle group, elapsed time and an overflow menu.
 * "Finalizar" lives in the menu so it never competes with set logging.
 */
@Composable
fun WorkoutHeader(
    routineName: String,
    elapsedSeconds: Long,
    onMinimize: () -> Unit,
    onFinish: () -> Unit,
    onDiscard: () -> Unit
) {
    val parts = routineName.split(" — ", " - ", limit = 2)
    val title = parts.first().trim()
    val subtitle = parts.getOrNull(1)?.trim()
    var menuOpen by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
    ) {
        IconButton(onClick = onMinimize) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.workout_minimize)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Icon(
                Icons.Filled.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = formatClock(elapsedSeconds),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Box {
            IconButton(onClick = { menuOpen = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = null)
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.workout_minimize)) },
                    onClick = { menuOpen = false; onMinimize() }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.workout_finish_full)) },
                    onClick = { menuOpen = false; onFinish() }
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(R.string.workout_discard_full),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = { menuOpen = false; onDiscard() }
                )
            }
        }
    }
}

// ═════════════════════════ Progress card ═════════════════════════

@Composable
fun WorkoutProgressCard(
    exercisesDone: Int,
    exercisesTotal: Int,
    setsDone: Int,
    setsTotal: Int,
    volumeKg: Double,
    elapsedSeconds: Long
) {
    val fraction = if (setsTotal > 0) setsDone.toFloat() / setsTotal else 0f
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.workout_progress_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.workout_progress_exercises, exercisesDone, exercisesTotal),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { fraction },
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Timer, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = " " + formatClock(elapsedSeconds),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.workout_progress_volume, volumeKg.toInt()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${(fraction * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ═════════════════════════ Exercise accordion card ═════════════════════════

/** Callbacks for everything the set table can do, grouped to keep signatures short. */
class SetTableActions(
    val currentInput: (WorkoutSet) -> SetInput,
    val placeholderWeight: (WorkoutSet) -> Double?,
    val placeholderReps: (WorkoutSet) -> Int?,
    val onWeightChange: (Long, String) -> Unit,
    val onRepsChange: (Long, String) -> Unit,
    val onConfirm: (WorkoutSet) -> Unit,
    val onUncomplete: (WorkoutSet) -> Unit,
    val onRepeatPreviousWeight: (WorkoutSet) -> Unit,
    val onDeleteSet: (WorkoutSet) -> Unit
)

@Composable
fun ExerciseAccordionCard(
    index: Int,
    exercise: WorkoutExercise,
    tip: String?,
    expanded: Boolean,
    onToggle: () -> Unit,
    actions: SetTableActions,
    onAddSet: () -> Unit,
    onReplace: () -> Unit,
    onNote: () -> Unit,
    onNext: (() -> Unit)?
) {
    val status = exercise.status()
    val borderColor by animateColorAsState(
        if (expanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f) else Color.Transparent,
        label = "cardBorder"
    )
    val repsRange = exercise.sets.firstOrNull()?.let { "${it.targetRepsMin}-${it.targetRepsMax}" }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
    ) {
        Column {
            // ── Collapsed header (always visible) ──
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(12.dp)
            ) {
                ExerciseImageWithNumber(index = index, imagePath = exercise.exerciseImagePath)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = exercise.exerciseName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (exercise.notes.isNotBlank()) {
                        Text(
                            text = exercise.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        InfoChip(stringResource(R.string.workout_chip_sets, exercise.sets.size))
                        repsRange?.let { InfoChip(stringResource(R.string.workout_chip_reps, it)) }
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    StatusBadge(status = status, active = expanded)
                    Icon(
                        if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            // ── Expanded content ──
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp)) {
                    if (!tip.isNullOrBlank()) {
                        TipRow(tip)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    SetTable(exercise = exercise, actions = actions)
                    Spacer(modifier = Modifier.height(10.dp))
                    ExerciseFooter(
                        completed = status == ExerciseStatus.COMPLETED,
                        onAddSet = onAddSet,
                        onReplace = onReplace,
                        onNote = onNote,
                        onNext = onNext
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseImageWithNumber(index: Int, imagePath: String?) {
    Box {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (imagePath != null) Color.White else MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (imagePath != null) {
                AssetImage(
                    assetPath = imagePath,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(64.dp)
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(3.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "${index + 1}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun StatusBadge(status: ExerciseStatus, active: Boolean) {
    val primary = MaterialTheme.colorScheme.primary
    when (status) {
        ExerciseStatus.COMPLETED -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(primary)
        ) {
            Icon(
                Icons.Filled.Check, contentDescription = stringResource(R.string.workout_status_done),
                tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp)
            )
        }
        else -> Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(if (active || status == ExerciseStatus.IN_PROGRESS) primary else Color.Transparent)
                .border(
                    1.5.dp,
                    if (active || status == ExerciseStatus.IN_PROGRESS) primary
                    else MaterialTheme.colorScheme.outline,
                    CircleShape
                )
        ) {
            Icon(
                Icons.Filled.PlayArrow,
                contentDescription = null,
                tint = if (active || status == ExerciseStatus.IN_PROGRESS)
                    MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun TipRow(tip: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            .padding(10.dp)
    ) {
        Icon(
            Icons.Filled.Lightbulb, contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = tip,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun ExerciseFooter(
    completed: Boolean,
    onAddSet: () -> Unit,
    onReplace: () -> Unit,
    onNote: () -> Unit,
    onNext: (() -> Unit)?
) {
    var menuOpen by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(50))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
                .clickable(onClick = onAddSet)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(
                text = " " + stringResource(R.string.routine_add_set),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        if (completed && onNext != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onNext)
            ) {
                Text(
                    text = stringResource(R.string.workout_next_exercise),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Icon(
                    Icons.Filled.SkipNext, contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    .clickable { menuOpen = true }
            ) {
                Icon(Icons.Filled.MoreVert, contentDescription = null)
            }
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.workout_replace_exercise)) },
                    onClick = { menuOpen = false; onReplace() }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.workout_exercise_note)) },
                    onClick = { menuOpen = false; onNote() }
                )
            }
        }
    }
}

// ═════════════════════════ Set table ═════════════════════════

@Composable
fun SetTable(exercise: WorkoutExercise, actions: SetTableActions) {
    val activeSetId = exercise.sets.firstOrNull { !it.isCompleted }?.id
    Column {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp)
        ) {
            HeaderCell(stringResource(R.string.workout_col_set), Modifier.width(36.dp))
            HeaderCell(stringResource(R.string.workout_previous), Modifier.weight(1.15f))
            HeaderCell(stringResource(R.string.workout_col_weight), Modifier.weight(1f))
            HeaderCell(stringResource(R.string.workout_col_reps), Modifier.weight(0.85f))
            Spacer(modifier = Modifier.width(44.dp))
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        exercise.sets.forEach { set ->
            SetRow(
                exercise = exercise,
                set = set,
                isActive = set.id == activeSetId,
                actions = actions
            )
        }
    }
}

@Composable
private fun HeaderCell(text: String, modifier: Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun SetRow(
    exercise: WorkoutExercise,
    set: WorkoutSet,
    isActive: Boolean,
    actions: SetTableActions
) {
    val input = actions.currentInput(set)
    val weightHint = actions.placeholderWeight(set)?.let { formatKg(it) }
    val repsHint = actions.placeholderReps(set)?.toString()
    var menuOpen by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }

    val rowBackground = when {
        set.isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        isActive -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        else -> Color.Transparent
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(rowBackground)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        // Set number (tap → contextual menu)
        Box(modifier = Modifier.width(36.dp), contentAlignment = Alignment.Center) {
            SetNumberBadge(set = set, isActive = isActive, onClick = { menuOpen = true })
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                if (!set.isCompleted && weightHint != null) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.workout_repeat_weight, weightHint)) },
                        onClick = { menuOpen = false; actions.onRepeatPreviousWeight(set) }
                    )
                }
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(R.string.workout_delete_set),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    onClick = {
                        menuOpen = false
                        if (set.isCompleted) confirmDelete = true else actions.onDeleteSet(set)
                    }
                )
            }
        }

        // Previous session
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1.15f)
        ) {
            Text(
                text = if (set.previousWeightKg != null && set.previousReps != null)
                    "${formatKg(set.previousWeightKg)} × ${set.previousReps}" else "—",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            if (set.isCompleted && SetEntryResolver.isImprovement(
                    set.weightKg, set.reps, set.previousWeightKg, set.previousReps
                )
            ) {
                Text(
                    text = " ▲",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Weight
        CompactNumberField(
            value = input.weightText,
            placeholder = weightHint,
            enabled = !set.isCompleted,
            decimal = true,
            imeAction = ImeAction.Next,
            onValueChange = { actions.onWeightChange(set.id, it) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 3.dp)
        )

        // Reps
        CompactNumberField(
            value = input.repsText,
            placeholder = repsHint,
            enabled = !set.isCompleted,
            decimal = false,
            imeAction = ImeAction.Done,
            onValueChange = { actions.onRepsChange(set.id, it) },
            modifier = Modifier
                .weight(0.85f)
                .padding(horizontal = 3.dp)
        )

        // Confirm / undo
        Box(modifier = Modifier.width(44.dp), contentAlignment = Alignment.Center) {
            ConfirmSetButton(
                completed = set.isCompleted,
                isRecord = set.isPersonalRecord,
                onClick = {
                    if (set.isCompleted) actions.onUncomplete(set) else actions.onConfirm(set)
                }
            )
        }
    }

    if (confirmDelete) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(stringResource(R.string.workout_delete_set_title)) },
            text = { Text(stringResource(R.string.workout_delete_set_message)) },
            confirmButton = {
                TextButton(onClick = { confirmDelete = false; actions.onDeleteSet(set) }) {
                    Text(stringResource(R.string.action_delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun SetNumberBadge(set: WorkoutSet, isActive: Boolean, onClick: () -> Unit) {
    val label = when (set.type) {
        SetType.WARMUP -> "C"
        SetType.DROPSET -> "D"
        SetType.FAILURE -> "F"
        else -> "${set.setNumber}"
    }
    val (bg, fg) = when {
        set.isCompleted -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        isActive -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurface
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = when (set.type) {
                SetType.FAILURE -> if (set.isCompleted) fg else MaterialTheme.colorScheme.error
                SetType.WARMUP -> if (set.isCompleted) fg else MaterialTheme.colorScheme.onSurfaceVariant
                else -> fg
            }
        )
    }
}

/**
 * Compact 40 dp numeric input. Shows a grey placeholder that is used as the
 * value when the set is confirmed with the field left empty.
 */
@Composable
private fun CompactNumberField(
    value: String,
    placeholder: String?,
    enabled: Boolean,
    decimal: Boolean,
    imeAction: ImeAction,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor = if (enabled) MaterialTheme.colorScheme.onSurface
    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
    BasicTextField(
        value = value,
        onValueChange = { new ->
            // Allow only digits plus one decimal separator for weights
            val filtered = new.filter { it.isDigit() || (decimal && (it == '.' || it == ',')) }
            if (filtered.count { it == '.' || it == ',' } <= 1 && filtered.length <= 6) {
                onValueChange(filtered)
            }
        },
        enabled = enabled,
        singleLine = true,
        textStyle = MaterialTheme.typography.titleMedium.copy(
            color = textColor,
            textAlign = TextAlign.Center
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number,
            imeAction = imeAction
        ),
        modifier = modifier,
        decorationBox = { inner ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (enabled) MaterialTheme.colorScheme.background
                        else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (enabled) MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                        else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
            ) {
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                        textAlign = TextAlign.Center
                    )
                }
                inner()
            }
        }
    )
}

@Composable
private fun ConfirmSetButton(completed: Boolean, isRecord: Boolean, onClick: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (completed) primary else Color.Transparent)
            .border(
                1.5.dp,
                if (completed) primary else MaterialTheme.colorScheme.outline,
                CircleShape
            )
            .clickable(onClick = onClick)
    ) {
        if (completed) {
            Icon(
                if (isRecord) Icons.Filled.EmojiEvents else Icons.Filled.Check,
                contentDescription = stringResource(R.string.workout_complete_set),
                tint = if (isRecord) GoldRecord else MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                Icons.Filled.Check,
                contentDescription = stringResource(R.string.workout_complete_set),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ═════════════════════════ Rest timer ═════════════════════════

/** Compact rest bar docked at the bottom; survives exercise switching. */
@Composable
fun RestTimerBar(
    remainingSeconds: Int,
    totalSeconds: Int,
    paused: Boolean,
    onAdjust: (Int) -> Unit,
    onPauseResume: () -> Unit,
    onSkip: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            LinearProgressIndicator(
                progress = {
                    if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(
                            if (paused) R.string.workout_rest_paused else R.string.workout_rest_label
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatClock(remainingSeconds.toLong()),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                PillButton("−15", enabled = !paused) { onAdjust(-15) }
                Spacer(modifier = Modifier.width(6.dp))
                PillButton("+15", enabled = !paused) { onAdjust(15) }
                Spacer(modifier = Modifier.width(6.dp))
                RoundIconButton(
                    icon = if (paused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                    filled = false,
                    onClick = onPauseResume
                )
                Spacer(modifier = Modifier.width(6.dp))
                RoundIconButton(icon = Icons.Filled.SkipNext, filled = true, onClick = onSkip)
            }
        }
    }
}

/** Shown briefly when the rest ends. */
@Composable
fun RestFinishedBar(onDismiss: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDismiss)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(vertical = 14.dp)
        ) {
            Icon(
                Icons.Filled.PlayArrow, contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = " " + stringResource(R.string.workout_rest_done_banner),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun PillButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(50))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(50))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    filled: Boolean,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (filled) MaterialTheme.colorScheme.primary else Color.Transparent)
            .border(
                1.dp,
                if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                CircleShape
            )
            .clickable(onClick = onClick)
    ) {
        Icon(
            icon, contentDescription = null,
            tint = if (filled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}
