package com.brunoapp.fittrack.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.brunoapp.fittrack.R

private fun notify(context: Context, id: Int, title: String, text: String) {
    val granted = ContextCompat.checkSelfPermission(
        context, Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED ||
        android.os.Build.VERSION.SDK_INT < 33
    if (!granted) return

    val notification = NotificationCompat.Builder(context, "reminders")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(text)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()
    NotificationManagerCompat.from(context).notify(id, notification)
}

class TrainingReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        notify(
            applicationContext, 1001,
            applicationContext.getString(R.string.reminder_training_title),
            applicationContext.getString(R.string.reminder_training_text)
        )
        return Result.success()
    }
}

class WeightReminderWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        notify(
            applicationContext, 1002,
            applicationContext.getString(R.string.reminder_weight_title),
            applicationContext.getString(R.string.reminder_weight_text)
        )
        return Result.success()
    }
}
