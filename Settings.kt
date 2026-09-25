package com.example.activebreak

import android.content.Context

enum class ReminderType(val channelId: String, val channelName: String, val title: String) {
    MOVE("move", "Move reminders", "Time to move 🏃"),
    WATER("water", "Hydration reminders", "Hydrate 💧"),
    QUOTE("quote", "Motivation quotes", "Your motivation ✨")
}

data class Settings(
    val startMinutes: Int = 9 * 60,          // 09:00
    val endMinutes: Int = 17 * 60,           // 17:00
    val workDays: Set<Int> = setOf(1, 2, 3, 4, 5), // 1 = Monday ... 7 = Sunday
    val enabled: Map<ReminderType, Boolean> = ReminderType.entries.associateWith { true },
    val intervals: Map<ReminderType, Int> = mapOf(
        ReminderType.MOVE to 60,
        ReminderType.WATER to 45,
        ReminderType.QUOTE to 120
    )
)

object SettingsStore {
    private const val PREFS = "active_break_settings"

    fun load(context: Context): Settings {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val d = Settings()
        return Settings(
            startMinutes = p.getInt("start", d.startMinutes),
            endMinutes = p.getInt("end", d.endMinutes),
            workDays = p.getStringSet("days", null)?.map { it.toInt() }?.toSet() ?: d.workDays,
            enabled = ReminderType.entries.associateWith { p.getBoolean("en_${it.name}", d.enabled[it]!!) },
            intervals = ReminderType.entries.associateWith { p.getInt("iv_${it.name}", d.intervals[it]!!) }
        )
    }

    fun save(context: Context, s: Settings) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().apply {
            putInt("start", s.startMinutes)
            putInt("end", s.endMinutes)
            putStringSet("days", s.workDays.map { it.toString() }.toSet())
            ReminderType.entries.forEach {
                putBoolean("en_${it.name}", s.enabled[it] ?: true)
                putInt("iv_${it.name}", s.intervals[it] ?: 60)
            }
        }.apply()
    }
}
