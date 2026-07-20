package com.brunoapp.fittrack.presentation.screens.training.exercise

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.brunoapp.fittrack.core.constants.Equipment
import com.brunoapp.fittrack.core.constants.MuscleGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseEditScreen(
    onBack: () -> Unit,
    viewModel: ExerciseEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

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
                            if (state.isEditMode) R.string.exercise_edit
                            else R.string.exercise_create
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
            // Name
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.exercise_name)) },
                singleLine = true,
                isError = state.nameError,
                supportingText = if (state.nameError) {
                    { Text(stringResource(R.string.error_name_required)) }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )

            // Primary muscle group
            LabeledDropdown(
                label = stringResource(R.string.exercise_muscle_primary),
                selected = state.muscleGroup.displayName,
                options = MuscleGroup.entries.map { it.displayName },
                onSelect = { index -> viewModel.onMuscleGroupChange(MuscleGroup.entries[index]) }
            )

            // Secondary muscles
            Column {
                Text(
                    text = stringResource(R.string.exercise_muscle_secondary),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    MuscleGroup.entries
                        .filter { it != state.muscleGroup }
                        .forEach { muscle ->
                            FilterChip(
                                selected = muscle in state.secondaryMuscles,
                                onClick = { viewModel.onSecondaryMuscleToggle(muscle) },
                                label = { Text(muscle.displayName) }
                            )
                        }
                }
            }

            // Equipment
            LabeledDropdown(
                label = stringResource(R.string.exercise_equipment),
                selected = state.equipment.displayName,
                options = Equipment.entries.map { it.displayName },
                onSelect = { index -> viewModel.onEquipmentChange(Equipment.entries[index]) }
            )

            // Instructions
            OutlinedTextField(
                value = state.instructions,
                onValueChange = viewModel::onInstructionsChange,
                label = { Text(stringResource(R.string.exercise_instructions)) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            // Personal notes
            OutlinedTextField(
                value = state.personalNotes,
                onValueChange = viewModel::onNotesChange,
                label = { Text(stringResource(R.string.exercise_notes)) },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

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
}

@Composable
private fun LabeledDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (Int) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selected)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
