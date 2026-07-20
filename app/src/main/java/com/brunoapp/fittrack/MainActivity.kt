package com.brunoapp.fittrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
            FitTrackTheme(darkTheme = darkTheme) {
                FitTrackApp()
            }
        }
    }
}
