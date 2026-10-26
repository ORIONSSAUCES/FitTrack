package com.brunoapp.fittrack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.constants.ThemeMode
import com.brunoapp.fittrack.data.database.seed.DatabaseSeeder
import com.brunoapp.fittrack.data.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class StartupState { LOADING, NEEDS_PLAN_CHOICE, READY }

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val seeder: DatabaseSeeder
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = userPreferences.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeMode.AUTO
        )

    private val _startup = MutableStateFlow(StartupState.LOADING)
    val startup: StateFlow<StartupState> = _startup.asStateFlow()

    init {
        viewModelScope.launch {
            seeder.seedIfNeeded()
            _startup.value = if (userPreferences.planPreset.first() == null)
                StartupState.NEEDS_PLAN_CHOICE
            else StartupState.READY
        }
    }

    fun onChoosePlan(preset: String) {
        viewModelScope.launch {
            _startup.value = StartupState.LOADING
            userPreferences.setPlanPreset(preset)
            seeder.seedIfNeeded()
            _startup.value = StartupState.READY
        }
    }
}
