package com.brunoapp.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.brunoapp.fittrack.presentation.screens.onboarding.PlanChoiceScreen
import com.brunoapp.fittrack.core.constants.ThemeMode
import com.brunoapp.fittrack.presentation.navigation.FitTrackApp
import com.brunoapp.fittrack.presentation.theme.FitTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val darkTheme = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.AUTO -> isSystemInDarkTheme()
            }
            val startup by viewModel.startup.collectAsState()
            FitTrackTheme(darkTheme = darkTheme) {
                when (startup) {
                    StartupState.LOADING -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                    StartupState.NEEDS_PLAN_CHOICE ->
                        PlanChoiceScreen(onChoose = viewModel::onChoosePlan)
                    StartupState.READY -> FitTrackApp()
                }
            }
        }
    }
}
