# Prody - Your Growth Companion

<p align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp" alt="Prody Logo" width="120"/>
</p>

<p align="center">
  <strong>Transform the way you think, learn, and evolve</strong>
</p>

<p align="center">
  <a href="#features">Features</a> |
  <a href="#technology-stack">Tech Stack</a> |
  <a href="#ai-integration">AI Setup</a> |
  <a href="#building-the-app">Build</a> |
  <a href="#architecture">Architecture</a>
</p>

---

**Prody** is a comprehensive self-improvement Android app that transforms the way you think, learn, and evolve. Built with modern Android development practices using Kotlin, Jetpack Compose, and Material 3 Design.

## Features

### Journaling
- Daily journaling with AI-powered prompts
- Emotional insights from Buddha (AI assistant)
- Pattern detection over time
- Templates for different reflection types
- Mood tracking with thoughtful reflections

### Gamification
- XP and leveling system with 10 rank tiers
- Achievement badges with rarity tiers (Common to Legendary)
- Customizable profile banners
- Competitive leaderboard
- Streak tracking with celebration milestones

### Daily Wisdom
- **Word of the Day**: Expand your vocabulary with curated words, definitions, etymology, and usage examples
- **Quote Collection**: Inspirational quotes from great thinkers across 7 themes
- **Proverbs & Idioms**: Cultural wisdom with meanings and origins
- **Phrases**: Useful phrases for everyday communication
- Personalized quotes based on your journey
- Not preachy, just helpful

### Buddha - Your Stoic AI Guide
- Journal your thoughts and receive personalized stoic wisdom
- Content analysis for contextual responses
- Weekly summaries of your growth journey
- Mood-appropriate wisdom delivery

### Future Self Messaging
- Write letters to your future self
- Schedule delivery from 1 week to 1 year
- Multiple categories: Goal, Motivation, Promise, General
- Receive motivation when you need it
- Countdown to delivery

### Profile & Stats
- Track your progress with detailed statistics
- Weekly and monthly summaries
- Achievement showcase
- Customizable profile with banners and badges
- Visual journey timeline

### Notifications (NEW)
- User-configurable morning notification time
- User-configurable evening notification time
- Separate toggles for wisdom and journal reminders
- Schedule adjusts automatically when times change

### AI Configuration (NEW)
- AI configuration status indicator on home screen
- Warning banner when API key is not configured
- Secure API key storage infrastructure
- Fallback to curated content when AI unavailable

## Tech Stack

| Component | Technology |
|-----------|------------|
| **Language** | Kotlin 2.0 |
| **UI** | Jetpack Compose with Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt |
| **AI** | Gemini API (Buddha) |
| **Database** | Room with Flow |
| **Preferences** | DataStore |
| **Async** | Kotlin Coroutines |
| **Navigation** | Compose Navigation with animations |
| **Background Work** | WorkManager |
| **Fonts** | Poppins + Playfair Display |

## Design System

Prody features a comprehensive design system:

- **Colors**: 100+ semantic colors including brand, mood, gamification, and rarity colors
- **Typography**: Full Material 3 type scale plus custom styles for wisdom, stats, badges
- **Shapes**: 40+ shape definitions for cards, buttons, badges, and special components
- **Dimensions**: Complete spacing system based on 8dp grid
- **Design Tokens**: Centralized token system for consistent theming

## Setup

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android SDK 35

### AI Features (Optional)
Prody now provisions AI access at runtime instead of bundling provider secrets in builds.

Runtime credential flow:
1. Fetch endpoint/policy from remote config.
2. Perform device attestation.
3. Exchange attestation for short-lived AI session tokens.

If a token is unavailable, AI surfaces gracefully fall back to offline/static experiences.

### Notification Settings
- Notifications can be configured in Settings > Notifications
- Morning notification time (default: 9:00 AM)
- Evening notification time (default: 8:00 PM)
- Separate toggles for Daily Wisdom and Journal Reminders

### Build Steps

1. **Clone the repository:**
```bash
git clone https://github.com/prashantchataut/prody.git
cd prody
```

2. Build debug APK:
```bash
./gradlew assembleDebug
```

3. Build release APK:
```bash
./gradlew assembleRelease
```

The APK will be available at `app/build/outputs/apk/`

## Project Structure

```
app/src/main/java/com/prody/prashant/
├── data/
│   ├── content/           # Content libraries (Wisdom, Prompts)
│   ├── local/
│   │   ├── dao/          # Room DAOs
│   │   ├── database/     # Room Database
│   │   ├── entity/       # Database entities
│   │   └── preferences/  # DataStore preferences
│   └── repository/       # Repository implementations
├── di/
│   └── AppModule.kt          # Hilt dependency injection
├── domain/
│   ├── identity/         # Achievements, Ranks, Banners
│   ├── model/            # Domain models
│   └── repository/       # Repository interfaces
├── notification/
│   ├── NotificationReceiver.kt
│   ├── NotificationScheduler.kt
│   └── BootReceiver.kt
├── ui/
│   ├── components/       # Reusable UI components
│   ├── navigation/       # Navigation setup
│   ├── screens/          # App screens
│   └── theme/            # Design system (Colors, Typography, Shapes, Dimensions, Tokens)
├── util/
│   ├── BuddhaWisdom.kt   # AI wisdom generator
│   └── NotificationMessages.kt  # 100+ notification messages
├── MainActivity.kt
└── ProdyApplication.kt
```

## Content Library

Prody includes extensive static content:

| Content Type | Count | Categories |
|--------------|-------|------------|
| **Wisdom Quotes** | 75+ | Growth, Resilience, Gratitude, Mindfulness, Action, Self-Compassion, Perspective |
| **Journal Prompts** | 80+ | Morning, Evening, Gratitude, Reflection, Growth, Emotional, Quick, Creative |
| **Notification Messages** | 100+ | Re-engagement, Celebration, Competitive, Streak, Journal, Future Message, etc. |
| **Achievements** | 20+ | Wisdom, Reflection, Consistency, Presence, Temporal, Mastery, Social, Explorer |
| **Ranks** | 10 | Seeker to Awakened |

## Documentation

- [Database Schema](docs/database_schema.md) - Complete entity definitions and relationships
- [NEXT_STEPS.md](NEXT_STEPS.md) - Roadmap with 25+ planned features
- [DEVELOPMENT_ROADMAP.md](DEVELOPMENT_ROADMAP.md) - Technical implementation phases

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

## Contributing

This is a personal project by Prashant Chataut. Contributions, suggestions, and feedback are welcome!

## Contact

- **Instagram:** [@prashantchataut_](https://www.instagram.com/prashantchataut_/)
- **Website:** [knowprashant.vercel.app](https://knowprashant.vercel.app)
- **GitHub:** [prashantchataut](https://github.com/prashantchataut)

## License

This project is open source. See the LICENSE file for details.

Upcoming features:
- Cloud sync with Firebase
- Social sharing features
- Widget support
- Wear OS companion app
- Advanced spaced repetition

---

## Support & Contact

### Bug Reports & Feature Requests

Please use [GitHub Issues](https://github.com/prashantchataut/prody/issues) for:
- Bug reports
- Feature requests
- Documentation improvements

### Developer Contact

- **Email**: [http](https://knowprashant.vercel.app/)
- **Instagram**: [@prashantchataut_](https://www.instagram.com/prashantchataut_/))
- **Discord**: [Prody Community](doesn't exist yet sorry :( )

### FAQ

**Q: Does Buddha work offline?**
A: Yes, previously cached responses work offline. New journal entries queue for AI response when online.

**Q: How secure are my journal entries?**
A: All data is stored locally on your device. AI requests use encrypted connections. No personal data is stored on servers.

**Q: Can I export my data?**
A: Yes, Settings > Export Data creates a JSON backup of all your entries and progress.

---
CODERABIT: ![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/prashantchataut/Prody?utm_source=oss&utm_medium=github&utm_campaign=prashantchataut%2FPrody&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)


## License

This project is open source. See the [LICENSE](LICENSE) file for details.

---

<p align="center">
  <sub>Built with love for personal growth seekers</sub>
</p>
