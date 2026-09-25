package com.example.activebreak

import android.content.Context
import org.json.JSONArray
import kotlin.random.Random

/**
 * Loads quotes from assets/quotes.json (fully offline).
 * Cycles through a shuffled order so no quote repeats until all have been shown.
 */
object QuoteRepository {
    private var cache: List<String>? = null
    private const val PREFS = "quote_state"

    fun all(context: Context): List<String> {
        cache?.let { return it }
        val json = context.assets.open("quotes.json").bufferedReader().use { it.readText() }
        val arr = JSONArray(json)
        val list = List(arr.length()) { arr.getString(it) }
        cache = list
        return list
    }

    fun next(context: Context): String {
        val quotes = all(context)
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        var seed = p.getLong("seed", System.currentTimeMillis())
        var cursor = p.getInt("cursor", 0)
        if (cursor >= quotes.size) {          // finished a full cycle: reshuffle
            seed = System.currentTimeMillis()
            cursor = 0
        }
        val order = quotes.indices.shuffled(Random(seed))
        val quote = quotes[order[cursor]]
        p.edit().putLong("seed", seed).putInt("cursor", cursor + 1).apply()
        return quote
    }
}

object Tips {
    val move = listOf(
        "Stand up and stretch your arms overhead for 30 seconds.",
        "Take a 2-minute walk. Refill your water on the way.",
        "Roll your shoulders back 10 times, then forward 10 times.",
        "Do 10 slow squats next to your desk.",
        "Look at something 6 metres away for 20 seconds to rest your eyes.",
        "Stretch your neck gently: left, right, forward. Hold each 10 seconds.",
        "Stand up, rise onto your toes 15 times.",
        "Open your chest: clasp your hands behind your back and hold 20 seconds.",
        "Take the stairs for one floor, up and down.",
        "Stand for your next phone call or meeting.",
        "Do 10 wall push-ups.",
        "Twist gently at the waist, 5 times each side.",
        "Shake out your hands and wrists for 20 seconds.",
        "March in place for one minute.",
        "Stretch your hamstrings: reach toward your toes and breathe slowly."
    )
    val water = listOf(
        "Drink a glass of water now.",
        "Take a few sips of water. Small and often works best.",
        "Is your bottle empty? Time for a refill.",
        "Feeling tired? Mild dehydration can do that. Have some water.",
        "Pair this reminder with a habit: finish your glass before your next task.",
        "Swap one sugary drink for water today.",
        "Coffee counts less than you think. Have a glass of water too.",
        "Keep your water where you can see it.",
        "Headache creeping in? Try water first.",
        "Hydration helps focus. Take a drink."
    )
}
