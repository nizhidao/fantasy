# Galaxy Battery Remapper (SM-F900 / Android 12)

This is an Android Studio source project. It remaps the displayed battery percentage:
`mapped = clamp(round((original - 20) * 100 / 80), 0, 100)`.

## Important limitation
A non-root third-party app cannot replace Samsung SystemUI's native status-bar percentage. This project draws a small overlay immediately **below** the status bar. It does not cover or modify the native SystemUI percentage. To avoid two numbers, manually disable Samsung's native “Show battery percentage” setting while keeping the battery icon.

It changes display only. It does not change Android's battery level, shutdown threshold, fuel-gauge data, or battery protection.

## Build
1. Open this folder in Android Studio (Giraffe or newer).
2. Let Gradle sync. Use JDK 17.
3. Build > Build Bundle(s) / APK(s) > Build APK(s).
4. Install the generated APK on the SM-F900.

## Setup
1. Open the app.
2. Grant “Display over other apps”.
3. Tap “Start battery mapping display”.
4. In Samsung Settings, turn off “Show battery percentage” (keep the battery icon enabled).
5. Return to the home screen. The mapped percentage appears just below the status bar.

The overlay may not appear over the lock screen, secure screens, or some full-screen apps. Android/Samsung may stop background services under power-management policies; allow the app to run unrestricted if the phone offers that setting.

## Formula examples
100 -> 100; 80 -> 75; 60 -> 50; 40 -> 25; 30 -> 13; 20 -> 0; 10 -> 0.
