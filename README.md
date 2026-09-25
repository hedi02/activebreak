# Active Break

Reminds you to move, drink water, and gives a motivational quote, only during your working hours.

## Run it
1. Install Android Studio (Koala or newer).
2. File > Open > select this `ActiveBreak` folder. Let Gradle sync (it downloads Gradle 8.9 and libraries once).
3. Press Run on a phone or emulator running Android 8.0 or newer.
4. Allow notifications, set your hours and intervals, and tap **Save & start reminders**.

## How it works
- `Settings.kt`: working hours, work days, and per-reminder interval and on/off, saved in SharedPreferences.
- `ReminderWorker.kt`: one periodic WorkManager job per reminder type. Each run checks working hours and skips if outside them. It survives reboots automatically.
- `QuoteRepository.kt`: loads `assets/quotes.json` (1160 quotes, fully offline) and cycles through them without repeats.
- `NotificationHelper.kt`: three notification channels, so users can mute one type in system settings.
- `tools/generate_quotes.py`: regenerates the quote file; add your own quotes there.

## Limits
- Android's minimum interval for periodic background work is 15 minutes, so the slider starts there.
- Reminders can drift a few minutes because Android batches background work to save battery. Some brands (Xiaomi, Oppo, Vivo, Samsung) kill background apps aggressively. Set battery usage to "Unrestricted" if reminders stop.

## Get an APK without Android Studio (GitHub)
1. Create a new repository on github.com and upload the contents of this folder, including the hidden `.github` folder.
2. Open the **Actions** tab. The "Build APK" job starts automatically (about 3 to 5 minutes).
3. Open the finished run and download **ActiveBreak-apk** under Artifacts. Unzip it to get `app-debug.apk`.
4. Copy it to your phone and install it (allow "Install unknown apps" when asked).
