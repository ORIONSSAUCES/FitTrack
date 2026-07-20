package com.brunoapp.fittrack.presentation.screens.training.routine

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.R
import com.brunoapp.fittrack.core.constants.SetType

private val dayNames = listOf(
    "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineEditScreen(
    onBack: () -> Unit,
    viewModel: RoutineEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val allExercises by viewModel.allExercises.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onBack()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (state.isEditMode) R.string.routine_edit
                            else R.string.routine_create
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.routine_name)) },
                singleLine = true,
                isError = state.nameError,
                supportingText = if (state.nameError) {
                    { Text(stringResource(R.string.error_name_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text(stringResource(R.string.routine_description)) },
                minLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            // Day selector
            Column {
                Text(
                    text = stringResource(R.string.routine_day),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                var dayMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { dayMenuExpanded = true }) {
                        Text(
                            state.dayOfWeek?.let { dayNames[it] }
                                ?: stringResource(R.string.routine_day_none)
                        )
                    }
                    DropdownMenu(
                        expanded = dayMenuExpanded,
                        onDismissRequest = { dayMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.routine_day_none)) },
                            onClick = {
                                viewModel.onDayChange(null)
                                dayMenuExpanded = false
                            }
                        )
                        dayNames.forEachIndexed { index, day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    viewModel.onDayChange(index)
                                    dayMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Exercises
            Text(
                text = stringResource(R.string.routine_exercises),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            state.exercises.forEachIndexed { index, exercise ->
                ExerciseEditorCard(
                    exercise = exercise,
                    index = index,
                    totalCount = state.exercises.size,
                    viewModel = viewModel
                )
            }

            OutlinedButton(
                onClick = viewModel::onShowPicker,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.routine_add_exercise))
            }

            Button(
                onClick = viewModel::onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = stringResource(R.string.action_save),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (state.showExercisePicker) {
        ExercisePickerDialog(
            query = state.pickerQuery,
            exercises = allExercises.filter {
                state.pickerQuery.isBlank() ||
                    it.name.lowercase().contains(state.pickerQuery.trim().lowercase())
            },
            onQueryChange = viewModel::onPickerQueryChange,
            onSelect = viewModel::onAddExercise,
            onDismiss = viewModel::onDismissPicker
        )
    }
}

@Composable
private fun ExerciseEditorCard(
    exercise: EditableExercise,
    index: Int,
    totalCount: Int,
    viewModel: RoutineEditViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: name + reorder + remove
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${index + 1}. ${exercise.exerciseName}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { viewModel.onMoveExercise(index, index - 1) },
                    enabled = index > 0
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null)
                }
                IconButton(
                    onClick = { viewModel.onMoveExercise(index, index + 1) },
                    enabled = index < totalCount - 1
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null)
                }
                IconButton(onClick = { viewModel.onRemoveExercise(index) }) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(R.string.action_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Sets
            exercise.sets.forEachIndexed { setIndex, set ->
                SetEditorRow(
                    set = set,
                    setIndex = setIndex,
                    exerciseIndex = index,
                    canRemove = exercise.sets.size > 1,
                    viewModel = viewModel
                )
            }

            TextButton(onClick = { viewModel.onAddSet(index) }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(stringResource(R.string.routine_add_set))
            }

            // Rest seconds
            var restText by remember(exercise.restSeconds) {
                mutableStateOf(exercise.restSeconds.toString())
            }
            OutlinedTextField(
                value = restText,
                onValueChange = { value ->
                    restText = value
                    value.toIntOrNull()?.let { viewModel.onRestChange(index, it) }
                },
                label = { Text(stringResource(R.string.routine_rest_seconds)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Notes
            OutlinedTextField(
                value = exercise.notes,
                onValueChange = { viewModel.onExerciseNotesChange(index, it) },
                label = { Text(stringResource(R.string.exercise_notes)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SetEditorRow(
    set: EditableSet,
    setIndex: Int,
    exerciseIndex: Int,
    canRemove: Boolean,
    viewModel: RoutineEditViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "${setIndex + 1}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(20.dp)
        )

        // Set type selector
        var typeMenuExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { typeMenuExpanded = true }) {
                Text(set.type.displayName, style = MaterialTheme.typography.labelMedium)
            }
            DropdownMenu(
                expanded = typeMenuExpanded,
                onDismissRequest = { typeMenuExpanded = false }
            ) {
                SetType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName) },
                        onClick = {
                            viewModel.onSetTypeChange(exerciseIndex, setIndex, type)
                            typeMenuExpanded = false
                        }
                    )
                }
            }
        }

        // Reps min–max
        var minText by remember(set.repsMin) { mutableStateOf(set.repsMin.toString()) }
        OutlinedTextField(
            value = minText,
            onValueChange = { value ->
                minText = value
                value.toIntOrNull()?.let {
                    viewModel.onSetRepsMinChange(exerciseIndex, setIndex, it)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )
        Text("–", color = MaterialTheme.colorScheme.onSurfaceVariant)
        var maxText by remember(set.repsMax) { mutableStateOf(set.repsMax.toString()) }
        OutlinedTextField(
            value = maxText,
            onValueChange = { value ->
                maxText = value
                value.toIntOrNull()?.let {
                    viewModel.onSetRepsMaxChange(exerciseIndex, setIndex, it)
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )

        if (canRemove) {
            IconButton(onClick = { viewModel.onRemoveSet(exerciseIndex, setIndex) }) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ExercisePickerDialog(
    query: String,
    exercises: List<com.brunoapp.fittrack.domain.model.Exercise>,
    onQueryChange: (String) -> Unit,
    onSelect: (com.brunoapp.fittrack.domain.model.Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.routine_pick_exercise)) },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text(stringResource(R.string.exercise_search_hint)) },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.height(320.dp)) {
                    items(exercises, key = { it.id }) { exercise ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(exercise.name)
                                    Text(
                                        text = exercise.muscleGroup.displayName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = { onSelect(exercise) }
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
