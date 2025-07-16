# Kir-Bits: The Dragon's Crystal – Application README

This document explains the structure and purpose of the main files and components in the Android Studio project for the Kir-Bits choose-your-own-adventure app.

---

## 📁 Project Structure Overview

```
app/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── kirbits/
│       │           └── thedragonscrystal/
│       │               ├── activities/
│       │               │   ├── MainMenuActivity.java
│       │               │   ├── StoryActivity.java
│       │               │   ├── SettingsActivity.java
│       │               │   ├── EndingsActivity.java
│       │               │   └── LoadGameActivity.java
│       │               ├── models/
│       │               │   ├── Page.java
│       │               │   ├── Choice.java
│       │               │   ├── Story.java
│       │               │   └── SaveFile.java
│       │               ├── managers/
│       │               │   └── SaveManager.java
│       │               └── utils/
│       │                   └── LanguageUtils.java
│       ├── res/
│       │   ├── layout/
│       │   │   ├── activity_main_menu.xml
│       │   │   ├── activity_story.xml
│       │   │   ├── activity_settings.xml
│       │   │   ├── activity_endings.xml
│       │   │   └── activity_load_game.xml
│       │   ├── drawable/
│       │   ├── values/
│       │   │   ├── strings.xml
│       │   │   ├── colors.xml
│       │   │   └── styles.xml
│       │   └── mipmap/
│       └── AndroidManifest.xml
```

---

## 📂 Key Java Packages

### `activities/`
Contains all Android UI screens:
- `MainMenuActivity.java`: Launch screen with navigation options.
- `StoryActivity.java`: Displays story pages and user choices.
- `SettingsActivity.java`: Lets users change text size and language.
- `EndingsActivity.java`: Shows unlocked endings.
- `LoadGameActivity.java`: Displays and loads saved game states.

### `models/`
Holds the game data classes:
- `Page.java`: A story page containing text, image, and choices.
- `Choice.java`: A user's decision and the target page it leads to.
- `Story.java`: A collection of all pages and logic.
- `SaveFile.java`: Represents a saved game slot.

### `managers/`
Logic and helper classes:
- `SaveManager.java`: Handles saving/loading progress and completed endings.

### `utils/` *(optional)*
- `LanguageUtils.java`: Helper for managing localization and language settings.

---

## 📂 XML Layouts (`res/layout/`)

- `activity_main_menu.xml`: Layout for the main menu.
- `activity_story.xml`: Layout for the story reading screen.
- `activity_settings.xml`: Settings screen with language and font size options.
- `activity_endings.xml`: Shows achieved endings.
- `activity_load_game.xml`: UI to choose and load saved games.

---

## 📂 Resources (`res/`)

- `drawable/`: Contains image files used in the UI.
- `values/strings.xml`: Text strings for localization.
- `values/colors.xml`: Color definitions used throughout the app.
- `values/styles.xml`: App theme and text styling.
- `mipmap/`: Launcher icons in various resolutions.

---

## 📄 AndroidManifest.xml

Declares the application components, permissions, and launcher activity.

---

## 🚀 How It Works

1. **User opens the app** → `MainMenuActivity` loads.
2. **User selects "Start Game"** → `StoryActivity` begins at page 0.
3. **Story is displayed** using data from `Story.java` and `Page.java`.
4. **User chooses a path** → `StoryActivity` loads the next page.
5. **User can save** at any time → `SaveManager` stores progress.
6. **If an ending is reached**, it's recorded for display in `EndingsActivity`.

---

## 🗜️ Submission Notes

All resources, layouts, and code files are contained within the app module. No external assets are required at runtime aside from the included drawable and layout resources.