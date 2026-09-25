package com.example.activebreak

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationHelper {

    fun createChannels(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java)
        ReminderType.entries.forEach {
            nm.createNotificationChannel(
                NotificationChannel(it.channelId, it.channelName, NotificationManager.IMPORTANCE_DEFAULT)
            )
        }
    }

    fun messageFor(context: Context, type: ReminderType): String = when (type) {
        ReminderType.MOVE -> Tips.move.random()
        ReminderType.WATER -> Tips.water.random()
        ReminderType.QUOTE -> QuoteRepository.next(context)
    }

    fun show(context: Context, type: ReminderType, text: String = messageFor(context, type)) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        val openApp = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, type.channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(type.title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openApp)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context).notify(type.ordinal + 1, notification)
        } catch (_: SecurityException) { }
    }
}
