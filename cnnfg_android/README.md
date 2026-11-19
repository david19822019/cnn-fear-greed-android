# CNN Fear & Greed Android app starter (Android 6.0+)

A Java Android Studio project (minSdk 23 / Android 6.0) showing CNN's Fear & Greed Index, its rating, previous-close/week/month/year comparisons and a simple 90-point history chart.


## Build APK
1. Install Android Studio on a computer and open this project folder.
2. Allow Gradle sync. The project uses Android Gradle Plugin 8.6.1 and compile SDK 35.
3. Select **Build > Build APK(s)**.
4. The APK is usually generated at `app/build/outputs/apk/debug/app-debug.apk`.
5. Transfer APK to the Android 6 phone and install. You may need to enable installation from unknown sources for your file manager.


## Data source
The app calls CNN's website endpoint directly:
`https://production.dataviz.cnn.io/index/fearandgreed/graphdata/2021-02-01`

This is an undocumented endpoint used by CNN's website, not an official stable API. CNN may block requests or change the response format; if that happens, the app will show a fetch error. CNN access is not guaranteed.

## Features in this initial build
- 0–100 score gauge with five sentiment zones
- Chinese sentiment label
- Previous close, 1-week, 1-month and 1-year comparison
- Approx. 90 daily history chart
- Manual refresh
- Refresh every 30 minutes while app is running

This starter does not yet include an Android home-screen widget, alert notifications, offline cache, or full selectable chart ranges.

## Disclaimer
For personal informational use only. Not investment advice. Not affiliated with CNN.

## Cloud build (no Android Studio required on your own computer)
This repository includes `.github/workflows/build-apk.yml` for GitHub Actions.

1. Create a GitHub repository and upload/push the contents of this project folder to it.
2. Open the repository's **Actions** tab and select **Build Android APK**.
3. Press **Run workflow** (workflow_dispatch) and wait for the job to finish successfully.
4. Open the completed run, scroll to **Artifacts**, and download **CNN-Fear-Greed-APK**.
5. Extract the artifact ZIP; it contains `app-debug.apk`. Transfer it to your Android phone and install it.

The workflow uses Ubuntu cloud runners, Java 17, Gradle 8.7, and Android SDK platform 35. The debug APK is unsigned for Play Store release but is installable for personal testing.
