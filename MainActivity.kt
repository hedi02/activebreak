package com.example.activebreak

import android.Manifest
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.createChannels(this)
        setContent {
            val scheme = if (Build.VERSION.SDK_INT >= 31) dynamicLightColorScheme(this) else lightColorScheme()
            MaterialTheme(colorScheme = scheme) {
                Surface(Modifier.fillMaxSize()) { SettingsScreen() }
            }
        }
    }
}

private val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

private fun fmt(minutes: Int) = "%02d:%02d".format(minutes / 60, minutes % 60)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var settings by remember { mutableStateOf(SettingsStore.load(context)) }
    var quote by remember { mutableStateOf(QuoteRepository.all(context).random()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) Toast.makeText(context, "Notifications are off. Reminders won't appear.", Toast.LENGTH_LONG).show()
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    fun pickTime(current: Int, onPicked: (Int) -> Unit) {
        TimePickerDialog(context, { _, h, m -> onPicked(h * 60 + m) }, current / 60, current % 60, true).show()
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Active Break", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        // Quote preview
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("“$quote”", fontStyle = FontStyle.Italic, style = MaterialTheme.typography.bodyLarge)
                TextButton(onClick = { quote = QuoteRepository.all(context).random() }) { Text("Another quote") }
                Text("${QuoteRepository.all(context).size} quotes stored offline",
                    style = MaterialTheme.typography.labelSmall)
            }
        }

        // Working hours
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Working hours", style = MaterialTheme.typography.titleMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { pickTime(settings.startMinutes) { settings = settings.copy(startMinutes = it) } }) {
                        Text("Start ${fmt(settings.startMinutes)}")
                    }
                    OutlinedButton(onClick = { pickTime(settings.endMinutes) { settings = settings.copy(endMinutes = it) } }) {
                        Text("End ${fmt(settings.endMinutes)}")
                    }
                }
                Text("Work days", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    dayLabels.forEachIndexed { i, label ->
                        val day = i + 1
                        FilterChip(
                            selected = day in settings.workDays,
                            onClick = {
                                val days = settings.workDays.toMutableSet()
                                if (!days.add(day)) days.remove(day)
                                settings = settings.copy(workDays = days)
                            },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }

        // Reminder cards
        ReminderType.entries.forEach { type ->
            val enabled = settings.enabled[type] == true
            val interval = settings.intervals[type] ?: 60
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(type.channelName, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Switch(checked = enabled, onCheckedChange = {
                            settings = settings.copy(enabled = settings.enabled + (type to it))
                        })
                    }
                    Text("Every ${if (interval >= 60 && interval % 60 == 0) "${interval / 60} h" else "$interval min"}")
                    Slider(
                        value = interval.toFloat(),
                        onValueChange = {
                            val v = (Math.round(it / 15f) * 15).coerceIn(15, 240)
                            settings = settings.copy(intervals = settings.intervals + (type to v))
                        },
                        valueRange = 15f..240f,
                        steps = 14,
                        enabled = enabled
                    )
                    TextButton(onClick = { NotificationHelper.show(context, type) }) { Text("Send a test now") }
                }
            }
        }

        Button(
            onClick = {
                SettingsStore.save(context, settings)
                ReminderScheduler.apply(context, settings)
                Toast.makeText(context, "Saved. Reminders are scheduled.", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save & start reminders") }

        Text(
            "Tip: Android may delay reminders by a few minutes to save battery. " +
                "If they stop entirely, set this app's battery usage to \"Unrestricted\".",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
