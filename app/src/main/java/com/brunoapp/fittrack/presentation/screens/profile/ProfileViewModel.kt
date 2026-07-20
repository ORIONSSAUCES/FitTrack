package com.brunoapp.fittrack.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.constants.Objective
import com.brunoapp.fittrack.core.constants.ThemeMode
import com.brunoapp.fittrack.core.utils.Validators
import com.brunoapp.fittrack.data.datastore.UserPreferences
import com.brunoapp.fittrack.domain.model.Profile
import com.brunoapp.fittrack.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val heightText: String = "",
    val weightInitialText: String = "",
    val weightGoalText: String = "",
    val objective: Objective = Objective.MAINTAIN,
    val defaultRestSeconds: Int = 120,
    val weeklyCheckDay: Int = 0,
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val heightError: Boolean = false,
    val weightInitialError: Boolean = false,
    val weightGoalError: Boolean = false,
    val justSaved: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = repository.getProfile()
            val theme = userPreferences.themeMode.first()
            _uiState.value = ProfileUiState(
                name = profile?.name.orEmpty(),
                heightText = profile?.heightCm?.formatClean().orEmpty(),
                weightInitialText = profile?.weightInitialKg?.formatClean().orEmpty(),
                weightGoalText = profile?.weightGoalKg?.formatClean().orEmpty(),
                objective = profile?.objective ?: Objective.MAINTAIN,
                defaultRestSeconds = profile?.defaultRestSeconds ?: 120,
                weeklyCheckDay = profile?.weeklyCheckDay ?: 0,
                themeMode = theme,
                isLoading = false
            )
        }
    }

    fun onNameChange(value: String) = update { it.copy(name = value, justSaved = false) }

    fun onHeightChange(value: String) =
        update { it.copy(heightText = value, heightError = false, justSaved = false) }

    fun onWeightInitialChange(value: String) =
        update { it.copy(weightInitialText = value, weightInitialError = false, justSaved = false) }

    fun onWeightGoalChange(value: String) =
        update { it.copy(weightGoalText = value, weightGoalError = false, justSaved = false) }

    fun onObjectiveChange(value: Objective) =
        update { it.copy(objective = value, justSaved = false) }

    fun onRestSecondsChange(value: Int) =
        update { it.copy(defaultRestSeconds = value.coerceIn(15, 600), justSaved = false) }

    fun onWeeklyCheckDayChange(value: Int) =
        update { it.copy(weeklyCheckDay = value.coerceIn(0, 6), justSaved = false) }

    fun onThemeModeChange(value: ThemeMode) {
        update { it.copy(themeMode = value) }
        viewModelScope.launch { userPreferences.setThemeMode(value) }
    }

    fun onSaveClick() {
        val state = _uiState.value

        val height = Validators.parseHeight(state.heightText)
        val weightInitial = Validators.parseBodyWeight(state.weightInitialText)
        val weightGoal = Validators.parseBodyWeight(state.weightGoalText)

        val hasErrors = height.isFailure || weightInitial.isFailure || weightGoal.isFailure
        if (hasErrors) {
            update {
                it.copy(
                    heightError = height.isFailure,
                    weightInitialError = weightInitial.isFailure,
                    weightGoalError = weightGoal.isFailure
                )
            }
            return
        }

        viewModelScope.launch {
            repository.saveProfile(
                Profile(
                    name = state.name.trim(),
                    heightCm = height.getOrNull(),
                    weightInitialKg = weightInitial.getOrNull(),
                    weightGoalKg = weightGoal.getOrNull(),
                    objective = state.objective,
                    defaultRestSeconds = state.defaultRestSeconds,
                    weeklyCheckDay = state.weeklyCheckDay
                )
            )
            update { it.copy(justSaved = true) }
        }
    }

    fun onSavedMessageShown() = update { it.copy(justSaved = false) }

    private fun update(transform: (ProfileUiState) -> ProfileUiState) {
        _uiState.value = transform(_uiState.value)
    }
}

/** Formats 80.0 as "80" and 80.5 as "80.5". */
private fun Double.formatClean(): String =
    if (this % 1.0 == 0.0) toInt().toString() else toString()
