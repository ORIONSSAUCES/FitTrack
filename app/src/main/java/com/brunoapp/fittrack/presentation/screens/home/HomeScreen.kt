package com.brunoapp.fittrack.presentation.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.R

@Composable
fun HomeScreen(
    onOpenWorkout: () -> Unit,
    onGoToNutrition: () -> Unit,
    onGoToProgress: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val navigate by viewModel.navigateToWorkout.collectAsState()

    LaunchedEffect(navigate) {
        if (navigate) {
            viewModel.onNavigatedToWorkout()
            onOpenWorkout()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (state.userName.isBlank())
                stringResource(R.string.home_greeting_anon)
            else stringResource(R.string.home_greeting, state.userName),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // ── Today's workout ──
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.FitnessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = stringResource(R.string.home_today_workout),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        state.hasActiveWorkout -> stringResource(R.string.workout_in_progress)
                        state.todayRoutine != null -> state.todayRoutine!!.name
                        else -> stringResource(R.string.home_no_routine_today)
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_week_sessions, state.sessionsThisWeek),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (state.hasActiveWorkout || state.todayRoutine != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = viewModel::onStartWorkout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.padding(2.dp))
                        Text(
                            text = stringResource(
                                if (state.hasActiveWorkout) R.string.workout_continue
                                else R.string.workout_start
                            ),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }

        // ── Nutrition: guide (GYM preset) or daily log ──
        if (state.showGuide) {
            HomeGuideCard(
                phase = state.guidePhase,
                currentWeight = state.currentWeight,
                onClick = onGoToNutrition
            )
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onGoToNutrition)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Restaurant,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = stringResource(R.string.home_nutrition_today),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!state.dayStarted) {
                        Text(
                            text = stringResource(R.string.home_day_not_started),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        HomeMacroBar(
                            label = stringResource(R.string.home_calories),
                            value = state.caloriesConsumed,
                            goal = state.goals.calories,
                            unit = "kcal"
                        )
                        HomeMacroBar(
                            label = stringResource(R.string.home_protein),
                            value = state.proteinConsumed,
                            goal = state.goals.protein.toInt(),
                            unit = "g"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text(
                                text = stringResource(
                                    R.string.home_meals_completed,
                                    state.mealsCompleted, state.mealsTotal
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f)
                            )
                            state.weeklyCompliance?.let {
                                Text(
                                    text = stringResource(R.string.daily_compliance_week, it),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Weight ──
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onGoToProgress)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.MonitorWeight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = stringResource(R.string.home_weight),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = state.currentWeight?.let { "%.1f kg".format(it) }
                            ?: stringResource(R.string.home_no_weight),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    state.weightDiff?.let { diff ->
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(
                            text = (if (diff >= 0) "+" else "") + "%.1f".format(diff),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.home_next_weigh_in, state.nextWeighInDay),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun HomeMacroBar(label: String, value: Int, goal: Int, unit: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$value / $goal $unit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = {
                if (goal > 0) (value.toFloat() / goal).coerceIn(0f, 1f) else 0f
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
private fun HomeGuideCard(phase: Int, currentWeight: Double?, onClick: () -> Unit) {
    val target = com.brunoapp.fittrack.data.guide.NutritionGuide.TARGET_WEIGHT_KG
    val start = com.brunoapp.fittrack.data.guide.NutritionGuide.START_WEIGHT_KG
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text(
                    text = if (phase == 2) "Guía · Fase 2 — Volumen limpio" else "Guía · Fase 1 — Descenso",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (phase == 1) {
                val current = currentWeight ?: start
                val remaining = (current - target).coerceAtLeast(0.0)
                Text(
                    text = if (remaining > 0) "Faltan %.1f kg para %.0f kg".format(remaining, target).replace('.', ',')
                    else "¡Meta alcanzada! Ya puedes iniciar la Fase 2",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = {
                        com.brunoapp.fittrack.core.utils.GuideProgress.fraction(start, current, target)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Hoy: proteína primero, 200 g o más, y 3 L de agua.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Proteína 190–200 g · subir 0,5–1 kg por mes",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
