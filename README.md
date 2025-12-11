# Prody - Your Growth Companion

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="Prody Logo" width="120"/>
</p>

**Prody** is a comprehensive self-improvement Android app that transforms the way you think, learn, and evolve. Built with modern Android development practices using Kotlin, Jetpack Compose, and Material 3 Design.

## Features

### Daily Wisdom
- **Word of the Day**: Expand your vocabulary with curated words, definitions, etymology, and usage examples
- **Quote Collection**: Inspirational quotes from great thinkers
- **Proverbs & Idioms**: Cultural wisdom with meanings and origins
- **Phrases**: Useful phrases for everyday communication

### Buddha - Your Stoic AI Guide
- Journal your thoughts and receive personalized stoic wisdom
- Mood tracking with thoughtful reflections
- Content analysis for contextual responses
- Weekly summaries of your growth journey

### Future Self Messaging
- Write letters to your future self
- Schedule delivery dates
- Countdown to delivery
- Reflect on your past self's intentions

### Gamification
- Achievement system with 20+ unlockable badges
- Streak tracking for consistent engagement
- Points and leveling system
- Leaderboard with peer interactions

### Profile & Stats
- Comprehensive activity statistics
- Achievement showcase
- Customizable settings
- Theme preferences (Light/Dark/System)

## Technology Stack

- **Language**: Kotlin 2.0
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Clean Architecture principles
- **Dependency Injection**: Hilt
- **Database**: Room with Flow
- **Preferences**: DataStore
- **Async**: Kotlin Coroutines
- **Navigation**: Compose Navigation with animations
- **Background Work**: WorkManager
- **Fonts**: Poppins (Google Fonts)

## Screenshots

*Coming soon*

## Building the App

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 35

### Build Steps

1. Clone the repository:
```bash
git clone https://github.com/yourusername/prody.git
cd prody
```

2. Generate a keystore (optional, for release builds):
```bash
cd keystore
keytool -genkeypair -v \
  -keystore prody-release.jks \
  -keyalg RSA -keysize 2048 \
  -validity 10000 -alias prody \
  -storepass prody2024 -keypass prody2024 \
  -dname "CN=Prody App, OU=Prody, O=Prody, L=Unknown, ST=Unknown, C=IN"
```

3. Build debug APK:
```bash
./gradlew assembleDebug
```

4. Build release APK:
```bash
./gradlew assembleRelease
```

The APK will be available at `app/build/outputs/apk/`

## CI/CD

This project includes GitHub Actions workflows for:
- Automated builds on push/PR
- Release APK generation
- Lint checking
- Unit test execution

### Creating a Release

1. Tag your release:
```bash
git tag v1.0.0
git push origin v1.0.0
```

2. GitHub Actions will automatically build and create a release with the signed APK.

## Project Structure

```
app/src/main/java/com/prody/prashant/
├── data/
│   ├── local/
│   │   ├── dao/          # Room DAOs
│   │   ├── database/     # Room Database
│   │   ├── entity/       # Database entities
│   │   └── preferences/  # DataStore preferences
│   └── InitialContentData.kt
├── di/
│   └── AppModule.kt      # Hilt dependency injection
├── domain/
│   └── model/            # Domain models
├── notification/
│   ├── NotificationReceiver.kt
│   ├── NotificationScheduler.kt
│   └── BootReceiver.kt
├── ui/
│   ├── components/       # Reusable UI components
│   ├── navigation/       # Navigation setup
│   ├── screens/          # App screens
│   └── theme/            # Material 3 theme
├── util/
│   ├── BuddhaWisdom.kt   # AI wisdom generator
│   └── NotificationMessages.kt
├── MainActivity.kt
└── ProdyApplication.kt
```

## Contributing

Contributions are welcome! Please read our contributing guidelines before submitting PRs.

## License

This project is open source. See the LICENSE file for details.

## Roadmap

See [NEXT_STEPS.md](NEXT_STEPS.md) for 25+ planned features and improvements.

---

*Built with love for personal growth seekers*
