# AIRO

An Android app (Kotlin + Jetpack Compose) that scans a room with the camera, identifies
the objects in it (including waste), and suggests where each one belongs — asking a
quick clarifying question first when the right storage spot depends on an object's
sub-type (e.g. "what kind of bag is this?").

## How it works

1. Create a **space** (e.g. "Bedroom", "Hallway Closet").
2. Tap **Scan room** and take a photo.
3. The photo is sent to Claude's vision API, which returns every object it sees,
   flags waste, and either gives a direct storage suggestion or asks a short
   clarifying question with a few quick-pick options.
4. Answer any clarifications — each answer triggers a follow-up call that returns a
   concrete suggested location and reason.
5. Save the resolved list to the space's inventory.

## Project structure

```
app/src/main/java/com/airo/app/
  ui/            Compose screens (home, space detail, scan/review) + theme
  data/local/    Room database (spaces, saved inventory items)
  data/remote/   Anthropic Messages API client (vision + placement reasoning)
  data/repository/  Combines network + persistence
  domain/model/  DetectedObject, ItemCategory, PlacementSuggestion
  di/            Minimal hand-rolled DI container (no Hilt, app is small)
```

## Setup

1. Copy `local.properties.example` to `local.properties` and set your
   `ANTHROPIC_API_KEY` (get one at https://console.anthropic.com).
2. Open the project in Android Studio (or run `./gradlew assembleDebug` from the
   command line) — it needs network access to Google's Maven repo and Maven
   Central to download the Android Gradle Plugin and AndroidX/Compose
   dependencies on first sync.
3. Run on a device or emulator with a camera. Minimum SDK is 26.

## Current scope (MVP)

This first slice covers one full path end-to-end: scan → detect → clarify →
suggest → save. Things intentionally left for later iterations:
- Multi-photo/video room sweep instead of a single still photo
- Cleaning schedules / reminders
- Editing or deleting saved inventory items
- Photo thumbnails stored alongside each item
