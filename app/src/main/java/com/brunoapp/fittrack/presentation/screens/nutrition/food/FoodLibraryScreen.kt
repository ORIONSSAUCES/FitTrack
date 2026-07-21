package com.brunoapp.fittrack.presentation.screens.nutrition.food

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.R
import com.brunoapp.fittrack.core.utils.NutritionCalc
import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.model.Recipe
import com.brunoapp.fittrack.presentation.theme.GoldRecord
import kotlin.math.roundToInt

@Composable
fun FoodLibraryScreen(
    onFoodClick: (Long) -> Unit,
    onCreateFood: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    onCreateRecipe: () -> Unit,
    viewModel: FoodLibraryViewModel = hiltViewModel()
) {
    val foods by viewModel.foods.collectAsState()
    val recipes by viewModel.recipes.collectAsState()
    val filter by viewModel.filter.collectAsState()
    var section by rememberSaveable { mutableIntStateOf(0) }
    var foodToDelete by remember { mutableStateOf<FoodItem?>(null) }
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SegmentedButton(
                    selected = section == 0,
                    onClick = { section = 0 },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text(stringResource(R.string.food_section_foods)) }
                SegmentedButton(
                    selected = section == 1,
                    onClick = { section = 1 },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text(stringResource(R.string.food_section_recipes)) }
            }

            OutlinedTextField(
                value = filter.query,
                onValueChange = viewModel::onQueryChange,
                placeholder = { Text(stringResource(R.string.food_search_hint)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            if (section == 0) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    FilterChip(
                        selected = filter.favoritesOnly,
                        onClick = viewModel::onToggleFavoritesOnly,
                        label = { Text(stringResource(R.string.exercise_filter_favorites)) },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (filter.favoritesOnly) GoldRecord
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(foods, key = { it.id }) { food ->
                        FoodCard(
                            food = food,
                            onClick = { onFoodClick(food.id) },
                            onFavorite = { viewModel.onToggleFavorite(food) },
                            onLongDelete = { foodToDelete = food }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(recipes, key = { it.id }) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) },
                            onDelete = { recipeToDelete = recipe }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = { if (section == 0) onCreateFood() else onCreateRecipe() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.action_add))
        }
    }

    foodToDelete?.let { food ->
        AlertDialog(
            onDismissRequest = { foodToDelete = null },
            title = { Text(stringResource(R.string.food_delete_title)) },
            text = { Text(stringResource(R.string.food_delete_message, food.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDeleteFood(food)
                        foodToDelete = null
                    }
                ) {
                    Text(
                        stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { foodToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }

    recipeToDelete?.let { recipe ->
        AlertDialog(
            onDismissRequest = { recipeToDelete = null },
            title = { Text(stringResource(R.string.recipe_delete_title)) },
            text = { Text(stringResource(R.string.food_delete_message, recipe.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDeleteRecipe(recipe)
                        recipeToDelete = null
                    }
                ) {
                    Text(
                        stringResource(R.string.action_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { recipeToDelete = null }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        )
    }
}

@Composable
private fun FoodCard(
    food: FoodItem,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    onLongDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (food.brand.isBlank()) food.name else "${food.name} · ${food.brand}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${food.caloriesPer100.roundToInt()} kcal · " +
                        "P ${food.proteinPer100.roundToInt()} · " +
                        "C ${food.carbsPer100.roundToInt()} · " +
                        "G ${food.fatPer100.roundToInt()}  (por 100 ${food.servingUnit})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onFavorite) {
                Icon(
                    imageVector = if (food.isFavorite) Icons.Filled.Star
                    else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = if (food.isFavorite) GoldRecord
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onLongDelete) {
                Text(
                    stringResource(R.string.action_delete),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        R.string.recipe_ingredient_count,
                        recipe.ingredients.size
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onDelete) {
                Text(
                    stringResource(R.string.action_delete),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
