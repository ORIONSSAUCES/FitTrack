package com.brunoapp.fittrack.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager get() = WorkManager.getInstance(context)

    fun scheduleTrainingReminder(hour: Int, minute: Int) {
        val delay = delayUntilNext(hour, minute)
        val request = PeriodicWorkRequestBuilder<TrainingReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay.toMinutes(), TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "training_reminder", ExistingPeriodicWorkPolicy.REPLACE, request
        )
    }

    fun cancelTrainingReminder() {
        workManager.cancelUniqueWork("training_reminder")
    }

    /** Weekly reminder on the profile's check day at 08:00. */
    fun scheduleWeightReminder(dayOfWeek: Int) {
        val delay = delayUntilNextWeekday(dayOfWeek, 8, 0)
        val request = PeriodicWorkRequestBuilder<WeightReminderWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(delay.toMinutes(), TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "weight_reminder", ExistingPeriodicWorkPolicy.REPLACE, request
        )
    }

    fun cancelWeightReminder() {
        workManager.cancelUniqueWork("weight_reminder")
    }

    private fun delayUntilNext(hour: Int, minute: Int): Duration {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(LocalTime.of(hour, minute))
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next)
    }

    private fun delayUntilNextWeekday(dayOfWeek: Int, hour: Int, minute: Int): Duration {
        val now = LocalDateTime.now()
        val targetDow = DayOfWeek.of(dayOfWeek + 1)  // 0 = Monday → DayOfWeek.MONDAY
        var next = now.toLocalDate()
            .with(TemporalAdjusters.nextOrSame(targetDow))
            .atTime(LocalTime.of(hour, minute))
        if (!next.isAfter(now)) {
            next = now.toLocalDate()
                .with(TemporalAdjusters.next(targetDow))
                .atTime(LocalTime.of(hour, minute))
        }
        return Duration.between(now, next)
    }
}
