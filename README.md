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

### Daily Wisdom
- **Word of the Day**: Expand your vocabulary with curated words, definitions, etymology, and usage examples
- **Quote Collection**: Inspirational quotes from great thinkers across 7 themes
- **Proverbs & Idioms**: Cultural wisdom with meanings and origins
- **Phrases**: Useful phrases for everyday communication

### Buddha - Your Stoic AI Guide
- Journal your thoughts and receive personalized stoic wisdom
- Mood tracking with thoughtful reflections (16 emotion states)
- Content analysis for contextual responses
- Weekly summaries of your growth journey
- Multiple AI provider support (OpenAI, Gemini, OpenRouter, Ollama)

### Future Self Messaging
- Write letters to your future self
- Schedule delivery dates (time capsule feature)
- Countdown to delivery with categories
- Reflect on your past self's intentions

### Gamification
- **Achievement System**: 20+ unlockable badges across 6 categories
- **Rarity Tiers**: Common, Uncommon, Rare, Epic, Legendary
- **Streak Tracking**: Visual streak flames with intensity levels
- **XP & Leveling**: Points system with level progression
- **Leaderboard**: Peer interactions with boosts and congratulations
- **Community Challenges**: Time-limited group challenges

### Profile & Stats
- Comprehensive activity statistics (daily, weekly, monthly, all-time)
- Achievement showcase with celebration animations
- Customizable avatars, banners, and titles
- Theme preferences (Light/Dark/System)

---

## Technology Stack

| Category | Technology |
|----------|------------|
| **Language** | Kotlin 2.0 |
| **UI Framework** | Jetpack Compose with Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt |
| **Database** | Room with Flow |
| **Preferences** | DataStore |
| **Async** | Kotlin Coroutines |
| **Navigation** | Compose Navigation with animations |
| **Background Work** | WorkManager |
| **Fonts** | Poppins + Playfair Display |
| **AI Integration** | OpenAI, Gemini, OpenRouter, Ollama |

---

## AI Integration

Prody supports multiple AI providers for Buddha's wisdom responses. Configure your preferred provider in the app settings.

### Supported Providers

| Provider | Models | Use Case |
|----------|--------|----------|
| **OpenAI** | GPT-4, GPT-3.5 Turbo | Production, high quality |
| **Google Gemini** | Gemini Pro, Gemini Flash | Fast responses, free tier |
| **OpenRouter** | Multiple models | Flexibility, aggregator |
| **Ollama** | Llama 2, Mistral, etc. | Local/offline, privacy |

### API Key Setup

#### Option 1: Environment Variables (Recommended for Development)

Create a `local.properties` file in the project root (gitignored):

```properties
# OpenAI Configuration
OPENAI_API_KEY=sk-your-openai-key-here
OPENAI_MODEL=gpt-4

# Google Gemini Configuration
GEMINI_API_KEY=your-gemini-api-key-here
GEMINI_MODEL=gemini-pro

# OpenRouter Configuration
OPENROUTER_API_KEY=your-openrouter-key-here
OPENROUTER_MODEL=anthropic/claude-3-opus

# Ollama Configuration (Local)
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=llama2
```

#### Option 2: Gradle Build Config

Add to your `app/build.gradle.kts`:

```kotlin
android {
    defaultConfig {
        buildConfigField("String", "OPENAI_API_KEY", "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY") ?: ""}\"")
        buildConfigField("String", "OPENROUTER_API_KEY", "\"${project.findProperty("OPENROUTER_API_KEY") ?: ""}\"")
    }
}
```

#### Option 3: In-App Configuration

Users can configure API keys directly in Settings > AI Settings within the app.

### Getting API Keys

| Provider | Steps |
|----------|-------|
| **OpenAI** | 1. Visit [platform.openai.com](https://platform.openai.com) <br> 2. Create account & add billing <br> 3. Generate API key in API Keys section |
| **Gemini** | 1. Visit [aistudio.google.com](https://aistudio.google.com) <br> 2. Click "Get API Key" <br> 3. Create key in new/existing project |
| **OpenRouter** | 1. Visit [openrouter.ai](https://openrouter.ai) <br> 2. Sign up with GitHub/Google <br> 3. Generate API key in dashboard |
| **Ollama** | 1. Install from [ollama.ai](https://ollama.ai) <br> 2. Run `ollama pull llama2` <br> 3. Start with `ollama serve` |

### AI Response Caching

Buddha responses are cached locally to:
- Reduce API costs
- Enable offline access to previous wisdom
- Improve response times for similar entries

Cache TTL: 7 days (configurable)

---

## Building the App

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 35
- (Optional) AI API keys for Buddha functionality

### Build Steps

1. **Clone the repository:**
```bash
git clone https://github.com/yourusername/prody.git
cd prody
```

2. **Configure API keys (optional):**
```bash
# Create local.properties with your API keys
echo "OPENAI_API_KEY=sk-your-key" >> local.properties
echo "GEMINI_API_KEY=your-key" >> local.properties
```

3. **Generate a keystore (for release builds):**
```bash
cd keystore
keytool -genkeypair -v \
  -keystore prody-release.jks \
  -keyalg RSA -keysize 2048 \
  -validity 10000 -alias prody \
  -storepass prody2024 -keypass prody2024 \
  -dname "CN=Prody App, OU=Prody, O=Prody, L=Unknown, ST=Unknown, C=IN"
```

4. **Build debug APK:**
```bash
./gradlew assembleDebug
```

5. **Build release APK:**
```bash
./gradlew assembleRelease
```

The APK will be available at `app/build/outputs/apk/`

---

## Architecture

### Project Structure

```
app/src/main/java/com/prody/prashant/
├── data/
│   ├── local/
│   │   ├── dao/              # Room DAOs
│   │   ├── database/         # Room Database
│   │   ├── entity/           # Database entities
│   │   └── preferences/      # DataStore preferences
│   └── content/              # Content libraries
│       ├── NotificationContent.kt
│       ├── WisdomContent.kt
│       └── JournalPrompts.kt
├── di/
│   └── AppModule.kt          # Hilt dependency injection
├── domain/
│   └── model/                # Domain models
├── notification/
│   ├── NotificationReceiver.kt
│   ├── NotificationScheduler.kt
│   └── BootReceiver.kt
├── ui/
│   ├── components/           # Reusable UI components
│   ├── navigation/           # Navigation setup
│   ├── screens/              # App screens
│   └── theme/                # Material 3 theme
│       ├── Color.kt          # Color system
│       ├── Type.kt           # Typography system
│       ├── Dimensions.kt     # Spacing & sizing
│       ├── Shape.kt          # Shape definitions
│       └── Theme.kt          # Theme composition
├── util/
│   ├── BuddhaWisdom.kt       # AI wisdom generator
│   └── NotificationMessages.kt
├── MainActivity.kt
└── ProdyApplication.kt
```

### Design System

Prody uses a comprehensive design system:

| Component | Description |
|-----------|-------------|
| **Colors** | 150+ colors including gamification, emotions, rarity tiers |
| **Typography** | 40+ text styles with Poppins (UI) and Playfair (wisdom) |
| **Dimensions** | 4dp grid system with accessibility-compliant touch targets |
| **Shapes** | Consistent corner radii across components |

### Database Schema

See [docs/database_schema.md](docs/database_schema.md) for complete database documentation including:
- 19 entity tables
- Entity relationships
- Migration strategy
- Query patterns

---

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

---

## Content Libraries

Prody includes extensive content libraries:

| Library | Count | Categories |
|---------|-------|------------|
| **Journal Prompts** | 100+ | Morning, Evening, Gratitude, Reflection, Growth, Emotional, Quick, Creative, Milestone |
| **Wisdom Quotes** | 60+ | Growth, Resilience, Gratitude, Mindfulness, Action, Self-Compassion, Perspective |
| **Notifications** | 90+ | Inactive, Celebration, Competitive, Level Up, Streak, Journal, Future Message, Wisdom, Check-In, Morning, Evening |

---

## Accessibility

Prody is built with accessibility in mind:

- **Screen Reader Support**: Comprehensive content descriptions
- **Touch Targets**: Minimum 48dp (WCAG 2.1 Level AA)
- **Color Contrast**: High contrast mode available
- **Reduce Motion**: Animation preference support
- **Font Scaling**: Respects system font size

---

## Contributing

Contributions are welcome! Please read our contributing guidelines before submitting PRs.

### Development Setup

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Run tests: `./gradlew test`
5. Run lint: `./gradlew lint`
6. Submit a pull request

---

## Roadmap

See [NEXT_STEPS.md](NEXT_STEPS.md) for 25+ planned features and improvements.

Upcoming features:
- Cloud sync with Firebase
- Social sharing features
- Widget support
- Wear OS companion app
- Advanced spaced repetition

---

## Support & Contact

### Bug Reports & Feature Requests

Please use [GitHub Issues](https://github.com/yourusername/prody/issues) for:
- Bug reports
- Feature requests
- Documentation improvements

### Developer Contact

- **Email**: developer@prodyapp.com
- **Twitter**: [@ProdyApp](https://twitter.com/prodyapp)
- **Discord**: [Prody Community](https://discord.gg/prody)

### FAQ

**Q: Does Buddha work offline?**
A: Yes, previously cached responses work offline. New journal entries queue for AI response when online.

**Q: How secure are my journal entries?**
A: All data is stored locally on your device. AI requests use encrypted connections. No personal data is stored on servers.

**Q: Can I export my data?**
A: Yes, Settings > Export Data creates a JSON backup of all your entries and progress.

---

## License

This project is open source. See the [LICENSE](LICENSE) file for details.

---

<p align="center">
  <sub>Built with love for personal growth seekers</sub>
</p>
