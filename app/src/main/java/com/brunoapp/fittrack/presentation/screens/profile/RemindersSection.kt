package com.brunoapp.fittrack.presentation.screens.profile

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.brunoapp.fittrack.R

@Composable
fun RemindersSection(
    viewModel: RemindersViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    var pendingEnable by remember { mutableStateOf<(() -> Unit)?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pendingEnable?.invoke()
        pendingEnable = null
    }

    fun withNotificationPermission(action: () -> Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            pendingEnable = action
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            action()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.reminders_section_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Training reminder
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.reminder_training_label),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.reminder_training_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.trainingEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            withNotificationPermission {
                                viewModel.onTrainingReminderChange(
                                    true, settings.trainingHour, settings.trainingMinute
                                )
                            }
                        } else {
                            viewModel.onTrainingReminderChange(
                                false, settings.trainingHour, settings.trainingMinute
                            )
                        }
                    }
                )
            }

            if (settings.trainingEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                var hourText by remember(settings.trainingHour) {
                    mutableStateOf(settings.trainingHour.toString())
                }
                var minuteText by remember(settings.trainingMinute) {
                    mutableStateOf("%02d".format(settings.trainingMinute))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.reminder_time_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = hourText,
                        onValueChange = { value ->
                            hourText = value
                            value.toIntOrNull()?.let { hour ->
                                if (hour in 0..23) {
                                    viewModel.onTrainingReminderChange(
                                        true, hour,
                                        minuteText.toIntOrNull()?.coerceIn(0, 59) ?: 0
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(72.dp)
                    )
                    Text(" : ", color = MaterialTheme.colorScheme.onSurface)
                    OutlinedTextField(
                        value = minuteText,
                        onValueChange = { value ->
                            minuteText = value
                            value.toIntOrNull()?.let { minute ->
                                if (minute in 0..59) {
                                    viewModel.onTrainingReminderChange(
                                        true,
                                        hourText.toIntOrNull()?.coerceIn(0, 23) ?: 17,
                                        minute
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(72.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weight reminder
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.reminder_weight_label),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.reminder_weight_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = settings.weightEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            withNotificationPermission {
                                viewModel.onWeightReminderChange(true)
                            }
                        } else {
                            viewModel.onWeightReminderChange(false)
                        }
                    }
                )
            }
        }
    }
}
