package com.example.activebreak

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ReminderWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val type = ReminderType.valueOf(inputData.getString(KEY_TYPE) ?: return Result.success())
        val settings = SettingsStore.load(applicationContext)
        if (settings.enabled[type] == true && isWorkingTime(settings)) {
            NotificationHelper.show(applicationContext, type)
        }
        return Result.success()
    }

    companion object {
        const val KEY_TYPE = "type"

        fun isWorkingTime(s: Settings, now: LocalDateTime = LocalDateTime.now()): Boolean {
            if (now.dayOfWeek.value !in s.workDays) return false
            val minutes = now.hour * 60 + now.minute
            return if (s.startMinutes <= s.endMinutes) {
                minutes in s.startMinutes until s.endMinutes
            } else { // overnight shift, e.g. 22:00 - 06:00
                minutes >= s.startMinutes || minutes < s.endMinutes
            }
        }
    }
}

object ReminderScheduler {
    /** Android's minimum interval for periodic background work is 15 minutes. */
    const val MIN_INTERVAL = 15

    fun apply(context: Context, s: Settings) {
        val wm = WorkManager.getInstance(context)
        ReminderType.entries.forEach { type ->
            val name = "reminder_${type.name}"
            if (s.enabled[type] == true) {
                val minutes = (s.intervals[type] ?: 60).coerceAtLeast(MIN_INTERVAL).toLong()
                val request = PeriodicWorkRequestBuilder<ReminderWorker>(minutes, TimeUnit.MINUTES)
                    .setInitialDelay(minutes, TimeUnit.MINUTES)
                    .setInputData(workDataOf(ReminderWorker.KEY_TYPE to type.name))
                    .build()
                wm.enqueueUniquePeriodicWork(name, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, request)
            } else {
                wm.cancelUniqueWork(name)
            }
        }
    }
}
