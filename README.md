# Diagram Smali Methods

<img src="https://img.shields.io/github/v/release/TimScriptov/diagram-smali-methods?include_prereleases&amp;label=Release" alt="Release">

# Screenshots

| ![Main](/art/desktop.png) | ![Main](/art/android.jpg) |
|---------------------------|---------------------------|

## Before running!

- install JDK 8 on your machine
- add `local.properties` file to the project root and set a path to Android SDK there

### Android

To run the application on android device/emulator:

- open project in Android Studio and run imported android run configuration

To build the application bundle:

- run `./gradlew :composeApp:assembleDebug`
- find `.apk` file in `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### Desktop

Run the desktop application: `./gradlew :composeApp:run`
