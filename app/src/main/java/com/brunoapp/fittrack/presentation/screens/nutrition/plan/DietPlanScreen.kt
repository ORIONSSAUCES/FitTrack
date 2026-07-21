package com.brunoapp.fittrack.presentation.screens.nutrition.plan

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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.brunoapp.fittrack.domain.model.DietDay
import com.brunoapp.fittrack.domain.model.MacroGoals
import com.brunoapp.fittrack.domain.model.MacroSummary
import com.brunoapp.fittrack.domain.model.PlannedMeal
import kotlin.math.roundToInt

private val dayLetters = listOf("L", "M", "X", "J", "V", "S", "D")

@Composable
fun DietPlanScreen(
    viewModel: DietPlanViewModel = hiltViewModel()
) {
    val plan by viewModel.activePlan.collectAsState()
    val selectedDay by viewModel.selectedDay.collectAsState()
    val picker by viewModel.picker.collectAsState()
    val allFoods by viewModel.allFoods.collectAsState()
    val allRecipes by viewModel.allRecipes.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showAddMealDialog by remember { mutableStateOf(false) }

    val currentPlan = plan
    if (currentPlan == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.diet_no_plan),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { showCreateDialog = true }) {
                Text(stringResource(R.string.diet_create_plan))
            }
        }
        if (showCreateDialog) {
            CreatePlanDialog(
                onConfirm = { name ->
                    viewModel.onCreatePlan(name)
                    showCreateDialog = false
                },
                onDismiss = { showCreateDialog = false }
            )
        }
        return
    }

    val day = currentPlan.days.firstOrNull { it.dayOfWeek == selectedDay }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = currentPlan.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Day selector
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            dayLetters.forEachIndexed { index, letter ->
                FilterChip(
                    selected = selectedDay == index,
                    onClick = { viewModel.onSelectDay(index) },
                    label = { Text(letter) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (day == null) {
            Text(
                text = stringResource(R.string.diet_day_empty),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Training/rest toggle + goals summary
            val goals = if (day.isTrainingDay) currentPlan.goalsTraining else currentPlan.goalsRest
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = day.isTrainingDay,
                    onClick = { viewModel.onToggleTrainingDay(day.id, day.isTrainingDay) },
                    label = {
                        Text(
                            stringResource(
                                if (day.isTrainingDay) R.string.diet_training_day
                                else R.string.diet_rest_day
                            )
                        )
                    },
                    leadingIcon = {
                        if (day.isTrainingDay) {
                            Icon(
                                Icons.Filled.FitnessCenter,
                                contentDescription = null,
                                modifier = Modifier.width(18.dp)
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            DayTotalsCard(totals = day.totals, goals = goals)
            Spacer(modifier = Modifier.height(12.dp))

            day.meals.forEach { meal ->
                MealCard(
                    meal = meal,
                    onAddItem = { viewModel.onShowPicker(meal.id) },
                    onDeleteMeal = { viewModel.onDeleteMeal(meal.id) },
                    onItemQuantityChange = viewModel::onItemQuantityChange,
                    onDeleteItem = viewModel::onDeleteItem
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

            if (showAddMealDialog) {
                AddMealDialog(
                    onConfirm = { name ->
                        viewModel.onAddMeal(day.id, name, day.meals.size)
                        showAddMealDialog = false
                    },
                    onDismiss = { showAddMealDialog = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
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
private fun DayTotalsCard(totals: MacroSummary, goals: MacroGoals) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.diet_day_totals),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            MacroBar(
                label = "Calorías",
                value = totals.calories,
                goal = goals.calories.toDouble(),
                unit = "kcal"
            )
            MacroBar(label = "Proteína", value = totals.protein, goal = goals.protein, unit = "g")
            MacroBar(label = "Carbos", value = totals.carbs, goal = goals.carbs, unit = "g")
            MacroBar(label = "Grasa", value = totals.fat, goal = goals.fat, unit = "g")
            MacroBar(label = "Fibra", value = totals.fiber, goal = goals.fiber, unit = "g")
        }
    }
}

@Composable
private fun MacroBar(label: String, value: Double, goal: Double, unit: String) {
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
private fun MealCard(
    meal: PlannedMeal,
    onAddItem: () -> Unit,
    onDeleteMeal: () -> Unit,
    onItemQuantityChange: (Long, Double) -> Unit,
    onDeleteItem: (Long) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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

            meal.items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${item.macros.calories.roundToInt()} kcal",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    var quantityText by remember(item.id, item.quantity) {
                        mutableStateOf(
                            if (item.quantity % 1.0 == 0.0) item.quantity.toInt().toString()
                            else item.quantity.toString()
                        )
                    }
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { value ->
                            quantityText = value
                            value.replace(',', '.').toDoubleOrNull()?.let {
                                onItemQuantityChange(item.id, it)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(80.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.unit,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(52.dp)
                    )
                    IconButton(onClick = { onDeleteItem(item.id) }) {
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

@Composable
private fun CreatePlanDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.diet_create_plan)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.diet_plan_name)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}

@Composable
private fun AddMealDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
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
            TextButton(onClick = { onConfirm(name) }) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
