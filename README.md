# Radio Explorer - KMM App

A Kotlin Multiplatform Mobile (KMM) radio streaming application with a clean, modern UI built using Jetpack Compose.

## Features

- 🎵 Stream radio stations from around the world
- 📱 Modern UI with Material Design
- 🔍 Search and browse stations by genre
- ⭐ Save favorite stations
- 🎧 Background playback support
- 🔄 Recently played stations tracking

## Architecture

This app is built using Kotlin Multiplatform Mobile (KMM) with a few key technologies:

- **Jetpack Compose**: For building the modern, declarative UI
- **ExoPlayer**: For audio streaming
- **Navigation Compose**: For screen navigation with the bottom navigation bar
- **Coroutines**: For asynchronous operations

## Screens

### Home Screen
- Recently played stations
- Now Playing card
- Editor's picks

### Browse Screen
- Grid view of all available stations
- Search functionality

### Favorites Screen
- List of favorite stations
- Remove from favorites functionality

### Profile Screen
- User profile information
- Settings and preferences

## Project Structure

This is a Kotlin Multiplatform project targeting Android, iOS.

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that's common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.

* `/iosApp` contains iOS applications. Even if you're sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `commonMain`. If preferred, you can add code to the platform-specific folders here too.

## Getting Started

To run the app:

1. Clone the repository
2. Open the project in Android Studio
3. Run the app on an Android device or emulator

## License

This project is licensed under the MIT License.

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…# radioApp
