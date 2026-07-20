package com.brunoapp.fittrack.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/** All navigation destinations. */
sealed class Screen(val route: String) {
    // Bottom navigation roots
    data object Home : Screen("home")
    data object Training : Screen("training")
    data object Nutrition : Screen("nutrition")
    data object Progress : Screen("progress")
    data object Profile : Screen("profile")

    // Detail routes (added per module)
    data object ActiveWorkout : Screen("active_workout")
}

data class BottomNavItem(
    val screen: Screen,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        Screen.Home,
        com.brunoapp.fittrack.R.string.nav_home,
        Icons.Filled.Home,
        Icons.Outlined.Home
    ),
    BottomNavItem(
        Screen.Training,
        com.brunoapp.fittrack.R.string.nav_training,
        Icons.Filled.FitnessCenter,
        Icons.Outlined.FitnessCenter
    ),
    BottomNavItem(
        Screen.Nutrition,
        com.brunoapp.fittrack.R.string.nav_nutrition,
        Icons.Filled.Restaurant,
        Icons.Outlined.Restaurant
    ),
    BottomNavItem(
        Screen.Progress,
        com.brunoapp.fittrack.R.string.nav_progress,
        Icons.Filled.TrendingUp,
        Icons.Outlined.TrendingUp
    ),
    BottomNavItem(
        Screen.Profile,
        com.brunoapp.fittrack.R.string.nav_profile,
        Icons.Filled.Person,
        Icons.Outlined.Person
    )
)
