package com.brunoapp.fittrack.presentation.screens.nutrition

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.brunoapp.fittrack.R
import com.brunoapp.fittrack.presentation.screens.nutrition.daily.DailyLogScreen
import com.brunoapp.fittrack.presentation.screens.nutrition.food.FoodLibraryScreen
import com.brunoapp.fittrack.presentation.screens.nutrition.plan.DietPlanScreen

@Composable
fun NutritionScreen(
    onFoodClick: (Long) -> Unit,
    onCreateFood: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    onCreateRecipe: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.nutrition_tab_today),
        stringResource(R.string.nutrition_tab_plan),
        stringResource(R.string.nutrition_tab_foods)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> DailyLogScreen()
            1 -> DietPlanScreen()
            2 -> FoodLibraryScreen(
                onFoodClick = onFoodClick,
                onCreateFood = onCreateFood,
                onRecipeClick = onRecipeClick,
                onCreateRecipe = onCreateRecipe
            )
        }
    }
}
