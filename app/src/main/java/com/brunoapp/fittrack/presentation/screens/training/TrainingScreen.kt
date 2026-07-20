package com.brunoapp.fittrack.presentation.screens.training

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
import com.brunoapp.fittrack.presentation.screens.training.exercise.ExerciseLibraryScreen
import com.brunoapp.fittrack.presentation.screens.training.history.HistoryScreen
import com.brunoapp.fittrack.presentation.screens.training.routine.RoutineListScreen

@Composable
fun TrainingScreen(
    onExerciseClick: (Long) -> Unit,
    onCreateExercise: () -> Unit,
    onRoutineClick: (Long) -> Unit,
    onCreateRoutine: () -> Unit,
    onOpenWorkout: () -> Unit,
    onSessionClick: (Long) -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.training_tab_routines),
        stringResource(R.string.training_tab_exercises),
        stringResource(R.string.training_tab_history)
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
            0 -> RoutineListScreen(
                onRoutineClick = onRoutineClick,
                onCreateClick = onCreateRoutine,
                onOpenWorkout = onOpenWorkout
            )
            1 -> ExerciseLibraryScreen(
                onExerciseClick = onExerciseClick,
                onCreateClick = onCreateExercise
            )
            2 -> HistoryScreen(onSessionClick = onSessionClick)
        }
    }
}
