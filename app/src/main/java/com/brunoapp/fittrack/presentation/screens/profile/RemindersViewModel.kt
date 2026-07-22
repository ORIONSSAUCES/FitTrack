package com.brunoapp.fittrack.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.data.datastore.UserPreferences
import com.brunoapp.fittrack.domain.repository.ProfileRepository
import com.brunoapp.fittrack.worker.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val scheduler: ReminderScheduler,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val settings: StateFlow<UserPreferences.ReminderSettings> =
        userPreferences.reminderSettings
            .stateIn(
                viewModelScope, SharingStarted.WhileSubscribed(5_000),
                UserPreferences.ReminderSettings()
            )

    fun onTrainingReminderChange(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            userPreferences.setTrainingReminder(enabled, hour, minute)
            if (enabled) scheduler.scheduleTrainingReminder(hour, minute)
            else scheduler.cancelTrainingReminder()
        }
    }

    fun onWeightReminderChange(enabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            userPreferences.setWeightReminder(enabled, hour, minute)
            if (enabled) {
                val checkDay = profileRepository.getProfile()?.weeklyCheckDay ?: 0
                scheduler.scheduleWeightReminder(checkDay, hour, minute)
            } else {
                scheduler.cancelWeightReminder()
            }
        }
    }
}
