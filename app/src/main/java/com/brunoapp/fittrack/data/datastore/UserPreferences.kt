package com.brunoapp.fittrack.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.brunoapp.fittrack.core.constants.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * User preferences backed by DataStore.
 * Provided as a singleton via [com.brunoapp.fittrack.di.AppModule].
 */
object PlanPreset {
    const val GYM = "GYM"     // Bruno's gym routines + diet plan
    const val HOME = "HOME"   // 4-day at-home routines
    const val NONE = "NONE"   // empty: user builds everything
}

class UserPreferences(
    private val context: Context
) {
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PLAN_PRESET = stringPreferencesKey("plan_preset")
        val SEEDED_ROUTINES = stringSetPreferencesKey("seeded_routines")
        val SEEDED_DIETS = stringSetPreferencesKey("seeded_diets")
        val TRAINING_REMINDER_ENABLED = booleanPreferencesKey("training_reminder_enabled")
        val TRAINING_REMINDER_HOUR = intPreferencesKey("training_reminder_hour")
        val TRAINING_REMINDER_MINUTE = intPreferencesKey("training_reminder_minute")
        val WEIGHT_REMINDER_ENABLED = booleanPreferencesKey("weight_reminder_enabled")
        val WEIGHT_REMINDER_HOUR = intPreferencesKey("weight_reminder_hour")
        val WEIGHT_REMINDER_MINUTE = intPreferencesKey("weight_reminder_minute")
    }

    data class ReminderSettings(
        val trainingEnabled: Boolean = false,
        val trainingHour: Int = 17,
        val trainingMinute: Int = 0,
        val weightEnabled: Boolean = false,
        val weightHour: Int = 8,
        val weightMinute: Int = 0
    )

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        runCatching { ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.AUTO.name) }
            .getOrDefault(ThemeMode.AUTO)
    }

    /** Which personal plan this install uses; null until chosen on first launch. */
    val planPreset: Flow<String?> = context.dataStore.data.map { it[Keys.PLAN_PRESET] }

    /** Names of plan routines already seeded once (so deleted ones don't come back). */
    val seededRoutines: Flow<Set<String>> =
        context.dataStore.data.map { it[Keys.SEEDED_ROUTINES] ?: emptySet() }

    suspend fun addSeededRoutines(names: Set<String>) {
        context.dataStore.edit {
            it[Keys.SEEDED_ROUTINES] = (it[Keys.SEEDED_ROUTINES] ?: emptySet()) + names
        }
    }

    /** Names of diet plans already seeded once. */
    val seededDiets: Flow<Set<String>> =
        context.dataStore.data.map { it[Keys.SEEDED_DIETS] ?: emptySet() }

    suspend fun addSeededDiet(name: String) {
        context.dataStore.edit {
            it[Keys.SEEDED_DIETS] = (it[Keys.SEEDED_DIETS] ?: emptySet()) + name
        }
    }

    suspend fun setPlanPreset(preset: String) {
        context.dataStore.edit { it[Keys.PLAN_PRESET] = preset }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    val reminderSettings: Flow<ReminderSettings> = context.dataStore.data.map { prefs ->
        ReminderSettings(
            trainingEnabled = prefs[Keys.TRAINING_REMINDER_ENABLED] ?: false,
            trainingHour = prefs[Keys.TRAINING_REMINDER_HOUR] ?: 17,
            trainingMinute = prefs[Keys.TRAINING_REMINDER_MINUTE] ?: 0,
            weightEnabled = prefs[Keys.WEIGHT_REMINDER_ENABLED] ?: false,
            weightHour = prefs[Keys.WEIGHT_REMINDER_HOUR] ?: 8,
            weightMinute = prefs[Keys.WEIGHT_REMINDER_MINUTE] ?: 0
        )
    }

    suspend fun setTrainingReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.TRAINING_REMINDER_ENABLED] = enabled
            it[Keys.TRAINING_REMINDER_HOUR] = hour
            it[Keys.TRAINING_REMINDER_MINUTE] = minute
        }
    }

    suspend fun setWeightReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.WEIGHT_REMINDER_ENABLED] = enabled
            it[Keys.WEIGHT_REMINDER_HOUR] = hour
            it[Keys.WEIGHT_REMINDER_MINUTE] = minute
        }
    }
}
