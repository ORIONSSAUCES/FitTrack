package com.brunoapp.fittrack.worker

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.brunoapp.fittrack.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Fires when the rest timer ends, even with the screen off. */
class RestAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val granted = android.os.Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        if (!granted) return

        val notification = NotificationCompat.Builder(context, "rest_timer")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.rest_done_title))
            .setContentText(context.getString(R.string.rest_done_text))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
        NotificationManagerCompat.from(context).notify(2001, notification)
    }
}

/** Schedules/cancels the exact rest-end alarm. */
@Singleton
class RestAlarm @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager get() =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun pendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            2001,
            Intent(context, RestAlarmReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    fun schedule(endTimeMs: Long) {
        runCatching {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                endTimeMs,
                pendingIntent()
            )
        }
    }

    fun cancel() {
        runCatching { alarmManager.cancel(pendingIntent()) }
        NotificationManagerCompat.from(context).cancel(2001)
    }
}
