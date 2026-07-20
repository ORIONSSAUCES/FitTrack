package com.brunoapp.fittrack.presentation.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.R
import com.brunoapp.fittrack.core.constants.Objective
import com.brunoapp.fittrack.core.constants.ThemeMode

private val dayNames = listOf(
    "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"
)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val savedMessage = stringResource(R.string.profile_saved)

    LaunchedEffect(state.justSaved) {
        if (state.justSaved) {
            snackbarHostState.showSnackbar(savedMessage)
            viewModel.onSavedMessageShown()
        }
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            // ── Personal data ──
            SectionCard(title = stringResource(R.string.profile_section_personal)) {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    label = { Text(stringResource(R.string.profile_name)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.heightText,
                    onValueChange = viewModel::onHeightChange,
                    label = { Text(stringResource(R.string.profile_height)) },
                    singleLine = true,
                    isError = state.heightError,
                    supportingText = if (state.heightError) {
                        { Text(stringResource(R.string.error_invalid_height)) }
                    } else null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // ── Goal ──
            SectionCard(title = stringResource(R.string.profile_section_goal)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    Objective.entries.forEach { objective ->
                        FilterChip(
                            selected = state.objective == objective,
                            onClick = { viewModel.onObjectiveChange(objective) },
                            label = { Text(objective.displayName) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = state.weightInitialText,
                        onValueChange = viewModel::onWeightInitialChange,
                        label = { Text(stringResource(R.string.profile_weight_initial)) },
                        singleLine = true,
                        isError = state.weightInitialError,
                        supportingText = if (state.weightInitialError) {
                            { Text(stringResource(R.string.error_invalid_weight)) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = state.weightGoalText,
                        onValueChange = viewModel::onWeightGoalChange,
                        label = { Text(stringResource(R.string.profile_weight_goal)) },
                        singleLine = true,
                        isError = state.weightGoalError,
                        supportingText = if (state.weightGoalError) {
                            { Text(stringResource(R.string.error_invalid_weight)) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Preferences ──
            SectionCard(title = stringResource(R.string.profile_section_preferences)) {
                // Default rest duration
                Text(
                    text = stringResource(R.string.profile_rest_duration, state.defaultRestSeconds),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = state.defaultRestSeconds.toFloat(),
                    onValueChange = { viewModel.onRestSecondsChange(it.toInt()) },
                    valueRange = 15f..600f,
                    steps = 38 // 15 s increments
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Weekly check day
                Text(
                    text = stringResource(R.string.profile_weekly_check_day),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                var dayMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(onClick = { dayMenuExpanded = true }) {
                        Text(dayNames[state.weeklyCheckDay])
                    }
                    DropdownMenu(
                        expanded = dayMenuExpanded,
                        onDismissRequest = { dayMenuExpanded = false }
                    ) {
                        dayNames.forEachIndexed { index, day ->
                            DropdownMenuItem(
                                text = { Text(day) },
                                onClick = {
                                    viewModel.onWeeklyCheckDayChange(index)
                                    dayMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Theme
                Text(
                    text = stringResource(R.string.profile_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = state.themeMode == mode,
                            onClick = { viewModel.onThemeModeChange(mode) },
                            label = { Text(mode.displayName) }
                        )
                    }
                }
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
