# Out Of Topic

**Out Of Topic** is an Indonesian party game app that makes hangouts more fun and interactive. It offers two game modes: **Truth or Dare** with a slot-machine question roll animation, and **Random Questions** organized by theme.

---

## Features

- **Truth or Dare** — Select Truth or Dare, tap roll, and watch questions animate in with a slot-machine effect before landing on the final question.
- **Random Questions** — Browse themes (e.g. Relationship, Childhood, Work), then swipe through shuffled questions with a horizontal pager and 3D card animation.
- **Offline-first** — Questions are fetched from remote (Firebase Hosting) on first launch and cached in a local Room database. Falls back to a bundled `assets/question.json` if network is unavailable.

---

## Tech Stack

| Layer | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose (type-safe routes via `@Serializable`) |
| DI | Koin 4 |
| Local storage | Room 2.6 |
| Networking | Ktor 3 (OkHttp engine) |
| Serialization | Kotlinx Serialization |
| Analytics | Firebase Analytics |
| Build | AGP 8 · KSP · Kotlin 2 |

---

## Architecture

The project follows **Clean Architecture** with three layers:

```
presentation/   → Compose screens + ViewModels (UI state, user events)
domain/         → Repository interfaces
data/           → Repository implementations, Room entities/DAOs, Ktor remote
```

Dependency injection is handled by Koin. All modules are declared in `CoreModule.kt` and started in `OutOfTopicApp`.

---

## Project Structure

```
app/src/main/java/com/poetralabs/outoftopic/
├── OutOfTopicApp.kt                     # Application — Koin init
├── MainActivity.kt                      # Entry point — NavHost setup
│
├── core/
│   ├── data/
│   │   ├── local/
│   │   │   ├── entity/
│   │   │   │   ├── QuestionEntity.kt    # Room entity: id, themeId, question
│   │   │   │   └── ThemeEntity.kt       # Room entity: id, displayName, description
│   │   │   └── room/
│   │   │       ├── AppDatabase.kt       # Room database (version 2)
│   │   │       └── QuestionDao.kt       # CRUD + flow queries
│   │   ├── remote/
│   │   │   └── response/
│   │   │       └── QuestionResponse.kt  # Ktor response DTOs (@Serializable)
│   │   └── repository/
│   │       └── QuestionRepositoryImpl.kt
│   ├── domain/
│   │   └── repository/
│   │       └── QuestionRepository.kt    # Interface
│   ├── di/
│   │   └── CoreModule.kt               # Koin modules
│   ├── navigation/
│   │   ├── Route.kt                    # Serializable route objects
│   │   └── NavigationGraph.kt          # composable<Route> declarations
│   └── theme/
│       ├── Color.kt
│       ├── Type.kt                     # BebasNeue + PlayfairDisplay typography
│       └── Theme.kt
│
└── presentation/
    ├── component/
    │   ├── HomeMenuCard.kt             # Reusable menu card (icon, title, description)
    │   └── ThemeCard.kt               # Theme selection card
    ├── home/
    │   └── HomeScreen.kt              # Landing screen with two menu cards
    ├── question/
    │   ├── QuestionScreen.kt          # HorizontalPager with shuffled questions
    │   ├── QuestionViewModel.kt
    │   └── theme/
    │       ├── QuestionThemeScreen.kt # 2-column theme grid
    │       └── QuestionThemeViewModel.kt
    └── truthordare/
        ├── TruthOrDareScreen.kt       # Selection + rolling animation screen
        └── TruthOrDareViewModel.kt    # 15 truth + 15 dare questions, roll logic
```

---

## Navigation

```
HomeRoute
├── TruthOrDareRoute              (back → HomeRoute)
└── QuestionThemeRoute            (back → HomeRoute)
    └── QuestionRoute(themeId, themeName)  (back → QuestionThemeRoute)
```

Routes are defined as `@Serializable` objects/data classes in `Route.kt` and registered with type-safe `composable<T>` in `NavigationGraph.kt`.

---

## Data Flow

### Remote → Local (on app start)

```
MainActivity.onCreate()
  └── QuestionRepository.prepopulateDatabase()
        └── Ktor GET https://outoftopic-8df32.web.app/question.json
              └── Parse QuestionResponse → ThemeEntity + QuestionEntity
                    └── Room insert (replace on conflict)
```

Falls back to `assets/question.json` if the network call fails.

### Local → UI (question screen)

```
QuestionViewModel
  └── QuestionRepository.getQuestionByTheme(themeId) : Flow<List<QuestionEntity>>
        └── QuestionDao.getQuestionsByTheme()
              └── Shuffled + displayed via HorizontalPager
```

---

## Screens

### Home
Two tappable `HomeMenuCard`s routing to Truth or Dare and Random Questions.

### Truth or Dare
1. Select **TRUTH** (purple) or **DARE** (orange) from the bottom buttons.
2. Tap **Mulai!** to trigger the roll.
3. The question card animates through random questions (fast → medium → slow phases) before landing on the final pick.
4. Tap again to roll a new question.

### Question Theme
Grid of theme cards fetched from the local DB. Selecting one logs a `SELECT_CONTENT` event to Firebase Analytics and navigates to the question screen.

### Question
Swipeable horizontal pager. Each card shows the question text with a dynamic color. A progress counter (`X / Total`) is shown at the top. On completion, an "All Done!" screen offers restart or back options.

---

## Color Palette

| Name | Hex | Usage |
|---|---|---|
| `BackgroundWhite` | `#F8F8F8` | Screen backgrounds |
| `Taro` | `#797DFF` | Truth mode, primary brand |
| `DarkTaro` | `#666BFF` | Truth mode pressed state |
| `LightOrange` | `#FFAE43` | Dare mode |
| `DarkOrange` | `#FF9509` | Dare mode pressed state |

---

## Fonts

- **Bebas Neue** (Regular) — display and headline text
- **Playfair Display** (Regular / Medium / SemiBold / Bold) — body and UI text

---

## Build & Run

### Requirements
- Android Studio Hedgehog or later
- JDK 11
- `google-services.json` placed in `app/` (Firebase project: `outoftopic-8df32`)

### Steps

```bash
git clone <repo-url>
cd OutOfTopic
# Place your google-services.json in app/
./gradlew assembleDebug
```

### Release build

```bash
./gradlew assembleRelease
```

Minification (`R8`) and resource shrinking are enabled for release. ProGuard rules for Room, Ktor, Koin, Kotlin Serialization, and Firebase are in `app/proguard-rules.pro`.

---

## Requirements

- Android 7.0+ (API 24)
- Internet connection for the initial question sync
