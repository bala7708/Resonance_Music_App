# 🎵 Resonance Music Player

A feature-rich desktop music player built with **Java 11 + Swing**, packaged as a proper **Maven** project.

---

## Project layout

```
resonance-music-player/
├── pom.xml                              # Maven build descriptor
├── checkstyle.xml                       # Checkstyle rules
├── README.md
└── src/
    ├── main/
    │   ├── java/com/musicapp/
    │   │   ├── App.java                 # Entry point
    │   │   ├── model/
    │   │   │   ├── Song.java            # Track domain object
    │   │   │   ├── Playlist.java        # Ordered song collection
    │   │   │   ├── PlayerState.java     # STOPPED | PLAYING | PAUSED
    │   │   │   └── RepeatMode.java      # NONE | ALL | ONE
    │   │   ├── service/
    │   │   │   └── MusicLibrary.java    # Singleton data store (20 songs, 4 playlists)
    │   │   ├── controller/
    │   │   │   └── PlayerController.java # Singleton playback engine + observer bus
    │   │   ├── ui/
    │   │   │   ├── MainWindow.java      # Root JFrame
    │   │   │   ├── SidebarPanel.java    # Navigation sidebar
    │   │   │   ├── LibraryPanel.java    # Song table with search
    │   │   │   ├── PlayerBar.java       # Bottom transport bar
    │   │   │   └── UIComponents.java    # RoundedPanel · ProgressSlider · IconButton
    │   │   │                            # AlbumArtPanel · VisualizerBar
    │   │   └── util/
    │   │       └── Theme.java           # Colour + font design tokens
    │   └── resources/
    │       ├── logback.xml              # SLF4J / Logback configuration
    │       └── application.properties  # App-level configuration
    └── test/
        └── java/com/musicapp/
            ├── SongTest.java            # 8 unit tests
            ├── PlaylistTest.java        # 7 unit tests
            ├── MusicLibraryTest.java    # 8 unit tests
            └── PlayerControllerTest.java# 13 unit tests
```

---

## Tech stack

| Concern              | Choice                              |
|----------------------|-------------------------------------|
| Language             | Java 11                             |
| UI toolkit           | Java Swing (custom-painted)         |
| Build tool           | Apache Maven 3.8+                   |
| Logging API          | SLF4J 2.0                           |
| Logging impl         | Logback 1.5                         |
| Test framework       | JUnit Jupiter 5.10                  |
| Mocking              | Mockito 5.11                        |
| Fat-JAR packaging    | maven-shade-plugin 3.5              |
| Static analysis      | Checkstyle 3.3                      |

---

## Prerequisites

- **Java JDK 11+** — `java -version` should show 11 or higher
- **Maven 3.8+** — `mvn -version`

---

## Common Maven commands

```bash
# Compile
mvn compile

# Run all unit tests
mvn test

# Run the application
mvn exec:java

# Package (produces regular JAR + fat/runnable JAR)
mvn package

# Run the fat JAR directly
java -jar target/resonance-music-player-1.0.0-runnable.jar

# Clean build artefacts
mvn clean

# Full cycle: clean → compile → test → package
mvn clean package

# Generate test report (target/site/surefire-report.html)
mvn surefire-report:report

# Checkstyle analysis
mvn checkstyle:check
```

---

## Features

| Feature                      | Details                                                          |
|------------------------------|------------------------------------------------------------------|
| **Library view**             | All 20 sample songs in a sortable table                         |
| **Favorites view**           | Songs marked with ♥                                             |
| **Queue view**               | Active playback queue                                           |
| **Playlists**                | 4 pre-built · create new from sidebar                           |
| **Real-time search**         | Filters by title, artist, album, genre                          |
| **Transport controls**       | Play · Pause · Next · Previous · Seek                           |
| **Shuffle**                  | Random queue traversal                                          |
| **Repeat modes**             | None / All / One                                                |
| **Volume control**           | Draggable slider with icon feedback                             |
| **Animated album art**       | Rotating vinyl disc (gradient, per-song colours)                |
| **Audio visualizer**         | Animated equalizer bars while playing                           |
| **Logging**                  | SLF4J + Logback (console + rolling file under `logs/`)          |
| **Simulated playback**       | Timer-driven progress (no actual audio file needed)             |

---

## Architecture overview

```
┌──────────────────────────────────────────────────────────────┐
│  UI Layer (Swing / EDT)                                       │
│  MainWindow → SidebarPanel + LibraryPanel + PlayerBar         │
│                    ↑                   ↑                      │
│           navListener          addSongListener etc.           │
│                    │                   │ (Consumer callbacks)  │
├────────────────────┼───────────────────┼──────────────────────┤
│  Controller Layer  │                   │                       │
│  PlayerController ─┴───────────────────┘                      │
│  (Singleton, owns playback state, fires observers)            │
│                         │                                     │
├─────────────────────────┼─────────────────────────────────────┤
│  Service Layer          │                                     │
│  MusicLibrary ──────────┘  (Singleton, owns all data)         │
├──────────────────────────────────────────────────────────────┤
│  Model Layer                                                  │
│  Song · Playlist · PlayerState · RepeatMode                  │
└──────────────────────────────────────────────────────────────┘
```

---

## Running the tests

```bash
mvn test
```

Expected output:
```
[INFO] Tests run: 36, Failures: 0, Errors: 0, Skipped: 0
```

---

## Extending the project

- **Real audio playback** — inject `javax.sound.sampled` or [JLayer](https://www.javazoom.net/javalayer/javalayer.html) into `PlayerController`
- **Persistence** — replace in-memory maps in `MusicLibrary` with SQLite via JDBC
- **File import** — add a menu action that scans a directory and populates the library
- **Themes** — add a `ThemeManager` bean and swap `Theme` colour constants at runtime
