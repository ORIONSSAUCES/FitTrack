package com.brunoapp.fittrack.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.brunoapp.fittrack.presentation.screens.home.HomeScreen
import com.brunoapp.fittrack.presentation.screens.nutrition.NutritionScreen
import com.brunoapp.fittrack.presentation.screens.nutrition.food.FoodEditScreen
import com.brunoapp.fittrack.presentation.screens.nutrition.food.RecipeEditScreen
import com.brunoapp.fittrack.presentation.screens.profile.ProfileScreen
import com.brunoapp.fittrack.presentation.screens.progress.ProgressScreen
import com.brunoapp.fittrack.presentation.screens.training.TrainingScreen
import com.brunoapp.fittrack.presentation.screens.training.exercise.ExerciseDetailScreen
import com.brunoapp.fittrack.presentation.screens.training.exercise.ExerciseEditScreen
import com.brunoapp.fittrack.presentation.screens.training.routine.RoutineEditScreen
import com.brunoapp.fittrack.presentation.screens.training.history.WorkoutDetailScreen
import com.brunoapp.fittrack.presentation.screens.training.workout.ActiveWorkoutScreen

@Composable
fun FitTrackApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Hide bottom bar on full-screen detail routes
    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy
                            ?.any { it.route == item.screen.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = stringResource(item.labelRes)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(item.labelRes),
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }

            composable(Screen.Training.route) {
                TrainingScreen(
                    onExerciseClick = { id ->
                        navController.navigate("exercise_detail/$id")
                    },
                    onCreateExercise = {
                        navController.navigate("exercise_edit")
                    },
                    onRoutineClick = { id ->
                        navController.navigate("routine_edit?routineId=$id")
                    },
                    onCreateRoutine = {
                        navController.navigate("routine_edit")
                    },
                    onOpenWorkout = {
                        navController.navigate(Screen.ActiveWorkout.route)
                    },
                    onSessionClick = { id ->
                        navController.navigate("workout_detail/$id")
                    }
                )
            }

            composable(
                route = "exercise_detail/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
            ) {
                ExerciseDetailScreen(
                    onBack = { navController.popBackStack() },
                    onEdit = { id -> navController.navigate("exercise_edit?exerciseId=$id") }
                )
            }

            composable(
                route = "exercise_edit?exerciseId={exerciseId}",
                arguments = listOf(
                    navArgument("exerciseId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                ExerciseEditScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = "routine_edit?routineId={routineId}",
                arguments = listOf(
                    navArgument("routineId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                RoutineEditScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = "workout_detail/{sessionId}",
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) {
                WorkoutDetailScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.ActiveWorkout.route) {
                ActiveWorkoutScreen(onExit = { navController.popBackStack() })
            }

            composable(Screen.Nutrition.route) {
                NutritionScreen(
                    onFoodClick = { id -> navController.navigate("food_edit?foodId=$id") },
                    onCreateFood = { navController.navigate("food_edit") },
                    onRecipeClick = { id -> navController.navigate("recipe_edit?recipeId=$id") },
                    onCreateRecipe = { navController.navigate("recipe_edit") }
                )
            }

            composable(
                route = "food_edit?foodId={foodId}",
                arguments = listOf(
                    navArgument("foodId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                FoodEditScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = "recipe_edit?recipeId={recipeId}",
                arguments = listOf(
                    navArgument("recipeId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                RecipeEditScreen(onBack = { navController.popBackStack() })
            }
            composable(Screen.Progress.route) { ProgressScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
        }
    }
}
