package com.brunoapp.fittrack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.brunoapp.fittrack.core.constants.ThemeMode
import com.brunoapp.fittrack.data.datastore.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userPreferences: UserPreferences
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = userPreferences.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ThemeMode.AUTO
        )
}
