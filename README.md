# Resonance_Music_App
Resonance is a dark-themed desktop music player built with Java 11 and Swing. It features playlist management, real-time search, shuffle/repeat modes, animated album art, and a custom Java2D UI. Designed with MVC + Service architecture, observer callbacks, and 36 unit tests for a clean, testable codebase.

<div align="center">

# 🎵 Resonance Music Player

**A feature-rich desktop music player built with Java 11 + Swing**

![Java](https://img.shields.io/badge/Java-11%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8%2B-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-5.10-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Version](https://img.shields.io/badge/Version-1.0.0-purple?style=for-the-badge)

*A cross-platform, dark-themed music player with animated album art, real-time search,*  
*playlist management, and a fully custom Java2D UI — zero native dependencies.*

</div>

---

## 📸 Preview

```
┌─────────────────────────────────────────────────────────────────────┐
│  Resonance — Music Player                              [─] [□] [✕]  │
├──────────────┬──────────────────────────────────────────────────────┤
│  ♪            │  Library — 20 songs              [  Search...     ]  │
│  Resonance   ├─────┬────┬──────────────────┬──────────┬────────────┤
│              │     │  # │ Title            │ Artist   │       Time │
│  MENU        ├─────┼────┼──────────────────┼──────────┼────────────┤
│  ▦ Library   │  ♥  │  1 │ Blinding Lights  │ The Week │       3:20 │
│  ♥ Favorites │  ○  │  2 │ Levitating       │ Dua Lipa │       3:23 │
│  ≡ Queue     │  ♥  │  3 │ Bohemian Rhaps…  │ Queen    │       5:55 │
│              │  ○  │  4 │ Hotel California  │ Eagles   │       6:30 │
│  PLAYLISTS   ├─────┴────┴──────────────────┴──────────┴────────────┤
│  ● Top Hits  │  ○  │  5 │ Shape of You     │ Ed Sheer │       3:53 │
│  ● Rock Cls  │  ♥  │  6 │ Uptown Funk      │ Mark Ron │       4:30 │
│  ● Hip-Hop   ├─────┴────┴──────────────────┴──────────┴────────────┤
│  ● Chill     │                                                       │
│  + New…      │                                                       │
├──────────────┴──────────────────────────────────────────────────────┤
│  [disc] Not Playing    [⇄] [⏮] [▶] [⏭] [↺]   ▁▂▄▇▄▂▁    🔊 ────●  │
│  —                     0:00 ──────────────────────── 0:00           │
└─────────────────────────────────────────────────────────────────────┘
```

---

## ✨ Features

| Feature | Description |
|---|---|
| **Library View** | Browse all tracks in a sortable, alternating-row styled table |
| **Favorites** | Heart-toggle songs inline and filter to your liked tracks |
| **Queue View** | Inspect the live playback queue at any time |
| **Playlists** | 4 built-in playlists + create unlimited custom ones from the sidebar |
| **Real-time Search** | Instant filter across title, artist, album, and genre |
| **Transport Controls** | Play, Pause, Next, Previous, and drag-to-seek progress bar |
| **Shuffle Mode** | Randomised queue traversal |
| **Repeat Modes** | Cycle through Off → Repeat All → Repeat One |
| **Volume Control** | Smooth draggable slider with dynamic icon feedback |
| **Animated Album Art** | Gradient vinyl disc that rotates during playback |
| **Audio Visualiser** | Animated equaliser bars that bounce with the music |
| **Cross-platform Icons** | Every icon drawn in pure Java2D — no font dependencies whatsoever |
| **Structured Logging** | SLF4J + Logback, rolling log file written to `logs/` |

---

## 🏗️ Architecture

Resonance follows a clean **MVC + Service** layered pattern:

```
┌─────────────────────────────────────────────────────────────────────┐
│  UI Layer  (Swing / Event Dispatch Thread)                           │
│                                                                      │
│  MainWindow                                                          │
│  ├── SidebarPanel      (navigation + playlist list)                  │
│  ├── LibraryPanel      (song table + search field)                   │
│  └── PlayerBar         (transport controls + seek + volume)          │
│              │  Consumer<T> observer callbacks                       │
├──────────────┼──────────────────────────────────────────────────────┤
│  Controller Layer                                                    │
│  PlayerController      (Singleton — playback state machine)          │
│              │  reads / mutates model objects                        │
├──────────────┼──────────────────────────────────────────────────────┤
│  Service Layer                                                       │
│  MusicLibrary          (Singleton — in-memory data store + search)   │
│              │  owns instances of                                    │
├──────────────┼──────────────────────────────────────────────────────┤
│  Model Layer                                                         │
│  Song  ·  Playlist  ·  PlayerState (enum)  ·  RepeatMode (enum)     │
└─────────────────────────────────────────────────────────────────────┘
```

**Key design decisions:**

- **Observer bus** — `PlayerController` fires typed `Consumer<T>` callbacks; UI panels subscribe without coupling to each other
- **Lazy singletons** — both `PlayerController` and `MusicLibrary` use the *Initialization-on-demand holder* idiom for thread-safe, zero-synchronisation singletons
- **Font-free icons** — `IconRenderer` draws every icon shape (play, pause, hearts, shuffle, volume, etc.) as pure `Graphics2D` primitives — no `Segoe UI Symbol`, emoji fonts, or image assets needed

---

## 📁 Project Structure

```
resonance-music-player/
├── pom.xml                                     # Maven build descriptor
├── checkstyle.xml                              # Static analysis rules
├── README.md
└── src/
    ├── main/
    │   ├── java/com/musicapp/
    │   │   ├── App.java                        # Application entry point
    │   │   ├── model/
    │   │   │   ├── Song.java                   # Track domain object
    │   │   │   ├── Playlist.java               # Ordered, named song collection
    │   │   │   ├── PlayerState.java            # Enum: STOPPED | PLAYING | PAUSED
    │   │   │   └── RepeatMode.java             # Enum: NONE | ALL | ONE
    │   │   ├── service/
    │   │   │   └── MusicLibrary.java           # Singleton data store & search
    │   │   ├── controller/
    │   │   │   └── PlayerController.java       # Singleton playback engine
    │   │   ├── ui/
    │   │   │   ├── MainWindow.java             # Root JFrame — composes all panels
    │   │   │   ├── SidebarPanel.java           # Navigation sidebar + playlists
    │   │   │   ├── LibraryPanel.java           # Song table with search
    │   │   │   ├── PlayerBar.java              # Bottom transport bar
    │   │   │   └── UIComponents.java           # Reusable custom components:
    │   │   │                                   #   RoundedPanel, ProgressSlider,
    │   │   │                                   #   IconButton, AlbumArtPanel,
    │   │   │                                   #   VisualizerBar, VectorLabel
    │   │   └── util/
    │   │       ├── Theme.java                  # Colour + font design tokens
    │   │       └── IconRenderer.java           # Pure Java2D vector icon renderer
    │   └── resources/
    │       ├── logback.xml                     # Logback logging configuration
    │       └── application.properties          # App defaults (window size, volume…)
    └── test/
        └── java/com/musicapp/
            ├── SongTest.java                   #  8 unit tests
            ├── PlaylistTest.java               #  7 unit tests
            ├── MusicLibraryTest.java           #  8 unit tests
            └── PlayerControllerTest.java       # 13 unit tests
```

---

## 🧰 Tech Stack

| Concern | Library / Tool | Version |
|---|---|---|
| Language | Java | 11 |
| UI Toolkit | Java Swing (custom-painted) | JDK built-in |
| Build | Apache Maven | 3.8+ |
| Logging API | SLF4J | 2.0.12 |
| Logging Implementation | Logback Classic | 1.5.3 |
| Unit Testing | JUnit Jupiter | 5.10.2 |
| Mocking | Mockito | 5.11.0 |
| Fat JAR packaging | maven-shade-plugin | 3.5.2 |
| Static Analysis | Checkstyle | 3.3.1 |

---

## ⚙️ Prerequisites

| Tool | Minimum Version | Check Command |
|---|---|---|
| Java JDK | 11 | `java -version` |
| Apache Maven | 3.8 | `mvn -version` |

<details>
<summary>Install on common platforms</summary>

**macOS (Homebrew)**
```bash
brew install openjdk@21 maven
```

**Ubuntu / Debian**
```bash
sudo apt-get install openjdk-21-jdk maven
```

**Windows (winget)**
```powershell
winget install Microsoft.OpenJDK.21
winget install Apache.Maven
```

</details>

---

## 🚀 Getting Started

### 1 — Clone

```bash
git clone https://github.com/your-org/resonance-music-player.git
cd resonance-music-player
```

### 2 — Run directly with Maven

```bash
mvn exec:java
```

### 3 — Or build a fat JAR and run it

```bash
mvn package
java -jar target/resonance-music-player-1.0.0-runnable.jar
```

---

## 🛠️ Maven Command Reference

```bash
# Compile source files only
mvn compile

# Run all 36 unit tests
mvn test

# Launch the application
mvn exec:java

# Build runnable fat JAR → target/*-runnable.jar
mvn package

# Full clean → compile → test → package cycle
mvn clean package

# Remove all compiled output
mvn clean

# Generate HTML test report (opens in browser)
mvn surefire-report:report
open target/site/surefire-report.html   # macOS
xdg-open target/site/surefire-report.html  # Linux

# Run static analysis
mvn checkstyle:check
```

---

## 🧪 Testing

**36 unit tests** across 4 test classes, covering the full non-UI stack:

| Class | Tests | Coverage |
|---|---|---|
| `SongTest` | 8 | Constructor, field getters, duration formatting, equality, null guard |
| `PlaylistTest` | 7 | Add/remove/move songs, duplicate guard, unmodifiable list, duration total |
| `MusicLibraryTest` | 8 | Singleton identity, seed data, case-insensitive search, favorites filter, CRUD |
| `PlayerControllerTest` | 13 | Play/pause/resume/stop, seek clamping, volume clamping, shuffle, repeat cycle, favorite toggle, play-count increment |

Run with:

```bash
mvn test
```

Expected output:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Tests run: 36, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

---

## 🎨 Design System

All visual styling lives in two utility classes so the look-and-feel can be changed in one place:

### `Theme.java` — design tokens

```java
// Backgrounds
Theme.BG_DARKEST     // #0A0A12  deepest layer
Theme.BG_DARK        // #12121E  sidebar
Theme.BG_MEDIUM      // #19192A  content area
Theme.BG_SELECTED    // #412D6E  active row/item

// Accents
Theme.ACCENT         // #8A2BE2  violet primary
Theme.ACCENT_LIGHT   // #B464FF  hover / active tint
Theme.PINK           // #FF468C  favourite hearts
Theme.CYAN           // #00D2DC  volume slider

// Text
Theme.TEXT_PRIMARY   // #F0F0FF
Theme.TEXT_SECONDARY // #A0A0BE
Theme.TEXT_MUTED     // #5A5A78
```

### `IconRenderer.java` — vector icons

Every icon is drawn programmatically using `Graphics2D` paths, fills, and strokes — making the UI **100% font-independent** and consistent across operating systems:

| Icon | Method |
|---|---|
| Play / Pause / Stop | `drawPlay()` `drawPause()` `drawStop()` |
| Previous / Next | `drawPrevious()` `drawNext()` |
| Shuffle | `drawShuffle()` |
| Repeat All / One | `drawRepeatAll()` `drawRepeatOne()` |
| Heart (filled / outline) | `drawHeart()` `drawHeartOutline()` |
| Volume High / Mid / Mute | `drawVolumeHigh()` `drawVolumeMid()` `drawVolumeMute()` |
| Grid (library) | `drawGrid()` |
| Lines (queue) | `drawLines()` |
| Music Note (logo) | `drawNote()` |

---

## 📋 Configuration

### `application.properties`

```properties
# Window dimensions
ui.window.width=1100
ui.window.height=700
ui.window.min.width=900
ui.window.min.height=580

# Player defaults
player.default.volume=70
player.shuffle=false
player.repeat=NONE
```

### `logback.xml`

- **Console appender** — `HH:mm:ss.SSS [thread] LEVEL logger - message`
- **Rolling file appender** — daily rotation, 7 days retention, written to `logs/resonance.log`
- Root level: `INFO`; `com.musicapp` package: `DEBUG`

---

## 🔌 Extending the Project

| Goal | Approach |
|---|---|
| **Real audio playback** | Wire `javax.sound.sampled` (WAV) or [JLayer](https://www.javazoom.net/javalayer/javalayer.html) (MP3) into `PlayerController.playCurrent()` |
| **File persistence** | Replace the `LinkedHashMap` stores in `MusicLibrary` with SQLite (via JDBC) or H2 |
| **Import local music** | Add a `JFileChooser` menu action that scans a directory and calls `MusicLibrary.addSong()` |
| **Multiple themes** | Introduce a `ThemeManager` singleton; store colour constants per theme and hot-swap at runtime |
| **Last.fm scrobbling** | Add a `Consumer<Song>` listener in `PlayerController` that fires an HTTP POST via `java.net.http.HttpClient` |
| **System tray mini-player** | Expose a compact `JWindow` via `java.awt.SystemTray` |
| **Keyboard shortcuts** | Register `KeyStroke` actions on `MainWindow`'s root pane via `InputMap` / `ActionMap` |

---

## 🐛 Known Limitations

- **Simulated playback only** — progress advances via a `javax.swing.Timer`; no actual audio decoding in v1.0.0
- **In-memory only** — the library and playlists reset on every restart; no file or database persistence yet
- **Sample data** — ships with 20 hard-coded tracks; local file import is a planned future feature

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. **Fork** the repository
2. Create a feature branch
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Commit with a conventional message
   ```bash
   git commit -m "feat: add local file import support"
   ```
4. Push and open a **Pull Request**

**Before submitting, make sure:**
```bash
mvn clean package   # must produce BUILD SUCCESS with 0 test failures
mvn checkstyle:check  # must report 0 errors
```

---

## 📄 License

This project is licensed under the **MIT License**.  
See the [LICENSE](LICENSE) file for the full text.

---

<div align="center">

Built with ☕ Java and pure `Graphics2D`

</div>


output 
<img width="2166" height="1374" alt="image" src="https://github.com/user-attachments/assets/19d748b4-cc05-4b41-8f9f-bdead527a7ce" />


