package com.brunoapp.fittrack.presentation.screens.nutrition.daily

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.R
import com.brunoapp.fittrack.core.constants.AdherenceLevel
import com.brunoapp.fittrack.core.utils.ComplianceCalc
import com.brunoapp.fittrack.domain.model.DailyMeal
import com.brunoapp.fittrack.domain.model.MacroGoals
import com.brunoapp.fittrack.domain.model.MacroSummary
import kotlin.math.roundToInt

@Composable
fun DailyLogScreen(
    viewModel: DailyLogViewModel = hiltViewModel()
) {
    val log by viewModel.todayLog.collectAsState()
    val plan by viewModel.activePlan.collectAsState()
    val weeklyCompliance by viewModel.weeklyCompliance.collectAsState()
    val picker by viewModel.picker.collectAsState()
    val allFoods by viewModel.allFoods.collectAsState()
    val allRecipes by viewModel.allRecipes.collectAsState()
    val startError by viewModel.startError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddMealDialog by remember { mutableStateOf(false) }

    val startErrorMessage = stringResource(R.string.daily_start_error)
    LaunchedEffect(startError) {
        if (startError) {
            snackbarHostState.showSnackbar(startErrorMessage)
            viewModel.onStartErrorShown()
        }
    }

    val currentLog = log
    if (currentLog == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.daily_not_started),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = viewModel::onStartFromPlan,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.daily_start_from_plan))
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = viewModel::onStartEmpty,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.daily_start_empty))
            }
            SnackbarHost(hostState = snackbarHostState)
        }
        return
    }

    val goals = plan?.let {
        if (currentLog.isTrainingDay) it.goalsTraining else it.goalsRest
    } ?: MacroGoals()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Training/rest + compliance row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            FilterChip(
                selected = currentLog.isTrainingDay,
                onClick = {
                    viewModel.onToggleTrainingDay(currentLog.id, currentLog.isTrainingDay)
                },
                label = {
                    Text(
                        stringResource(
                            if (currentLog.isTrainingDay) R.string.diet_training_day
                            else R.string.diet_rest_day
                        )
                    )
                },
                leadingIcon = {
                    if (currentLog.isTrainingDay) {
                        Icon(
                            Icons.Filled.FitnessCenter,
                            contentDescription = null,
                            modifier = Modifier.width(18.dp)
                        )
                    }
                }
            )
            val dailyPercent = ComplianceCalc.dailyPercent(
                currentLog.completedMeals, currentLog.totalMeals
            )
            dailyPercent?.let {
                Text(
                    text = stringResource(R.string.daily_compliance_today, it),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            weeklyCompliance?.let {
                Text(
                    text = stringResource(R.string.daily_compliance_week, it),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Consumed vs goals
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = stringResource(R.string.daily_consumed_vs_goal),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(6.dp))
                DailyMacroBar("Calorías", currentLog.totals.calories, goals.calories.toDouble(), "kcal")
                DailyMacroBar("Proteína", currentLog.totals.protein, goals.protein, "g")
                DailyMacroBar("Carbos", currentLog.totals.carbs, goals.carbs, "g")
                DailyMacroBar("Grasa", currentLog.totals.fat, goals.fat, "g")
                DailyMacroBar("Fibra", currentLog.totals.fiber, goals.fiber, "g")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Meals
        currentLog.meals.forEach { meal ->
            DailyMealCard(
                meal = meal,
                onToggleCompleted = {
                    viewModel.onToggleMealCompleted(meal.id, meal.isCompleted)
                },
                onAddItem = { viewModel.onShowPicker(meal.id) },
                onDeleteMeal = { viewModel.onDeleteMeal(meal.id) },
                onEntryQuantityChange = viewModel::onEntryQuantityChange,
                onDeleteEntry = viewModel::onDeleteEntry
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        OutlinedButton(
            onClick = { showAddMealDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.diet_add_meal))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Adherence
        Text(
            text = stringResource(R.string.daily_adherence),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            listOf(
                AdherenceLevel.FULL, AdherenceLevel.PARTIAL, AdherenceLevel.NONE
            ).forEach { level ->
                FilterChip(
                    selected = currentLog.adherence == level,
                    onClick = { viewModel.onSetAdherence(currentLog.id, level) },
                    label = { Text(level.displayName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showAddMealDialog) {
        var name by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddMealDialog = false },
            title = { Text(stringResource(R.string.diet_add_meal)) },
            text = {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.diet_meal_name)) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onAddMeal(currentLog.id, name, currentLog.meals.size)
                        showAddMealDialog = false
                    }
                ) {
                    Text(stringResource(R.string.action_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMealDialog = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    picker?.let { pickerState ->
        AlertDialog(
            onDismissRequest = viewModel::onDismissPicker,
            title = { Text(stringResource(R.string.diet_add_item)) },
            text = {
                Column {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        SegmentedButton(
                            selected = pickerState.section == 0,
                            onClick = { viewModel.onPickerSection(0) },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) { Text(stringResource(R.string.food_section_foods)) }
                        SegmentedButton(
                            selected = pickerState.section == 1,
                            onClick = { viewModel.onPickerSection(1) },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) { Text(stringResource(R.string.food_section_recipes)) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = pickerState.query,
                        onValueChange = viewModel::onPickerQuery,
                        placeholder = { Text(stringResource(R.string.food_search_hint)) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        if (pickerState.section == 0) {
                            items(
                                allFoods.filter {
                                    pickerState.query.isBlank() ||
                                        it.name.lowercase()
                                            .contains(pickerState.query.trim().lowercase())
                                },
                                key = { it.id }
                            ) { food ->
                                DropdownMenuItem(
                                    text = { Text(food.name) },
                                    onClick = { viewModel.onPickFood(food) }
                                )
                            }
                        } else {
                            items(
                                allRecipes.filter {
                                    pickerState.query.isBlank() ||
                                        it.name.lowercase()
                                            .contains(pickerState.query.trim().lowercase())
                                },
                                key = { it.id }
                            ) { recipe ->
                                DropdownMenuItem(
                                    text = { Text(recipe.name) },
                                    onClick = { viewModel.onPickRecipe(recipe) }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = viewModel::onDismissPicker) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun DailyMacroBar(label: String, value: Double, goal: Double, unit: String) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Row {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${value.roundToInt()} / ${goal.roundToInt()} $unit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LinearProgressIndicator(
            progress = {
                if (goal > 0) (value / goal).toFloat().coerceIn(0f, 1f) else 0f
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DailyMealCard(
    meal: DailyMeal,
    onToggleCompleted: () -> Unit,
    onAddItem: () -> Unit,
    onDeleteMeal: () -> Unit,
    onEntryQuantityChange: (Long, Double) -> Unit,
    onDeleteEntry: (Long) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (meal.isCompleted)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = meal.isCompleted,
                    onCheckedChange = { onToggleCompleted() }
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = meal.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${meal.totals.calories.roundToInt()} kcal · " +
                            "P ${meal.totals.protein.roundToInt()} g",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onAddItem) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = stringResource(R.string.diet_add_item),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDeleteMeal) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(R.string.action_delete),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            meal.entries.forEach { entry ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entry.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${entry.macros.calories.roundToInt()} kcal",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    var quantityText by remember(entry.id, entry.quantity) {
                        mutableStateOf(
                            if (entry.quantity % 1.0 == 0.0) entry.quantity.toInt().toString()
                            else entry.quantity.toString()
                        )
                    }
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { value ->
                            quantityText = value
                            value.replace(',', '.').toDoubleOrNull()?.let {
                                onEntryQuantityChange(entry.id, it)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(80.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = entry.unit,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(52.dp)
                    )
                    IconButton(onClick = { onDeleteEntry(entry.id) }) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
