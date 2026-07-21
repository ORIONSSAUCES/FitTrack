package com.brunoapp.fittrack

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.brunoapp.fittrack.data.database.seed.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FitTrackApp : Application() {

    @Inject
    lateinit var seeder: DatabaseSeeder

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        applicationScope.launch {
            seeder.seedIfNeeded()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "reminders",
            getString(R.string.notification_channel_reminders),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }
}
