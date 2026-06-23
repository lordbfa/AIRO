# AIRO

An Android app (Kotlin + Jetpack Compose) that scans a room with the camera, identifies
the objects in it (including waste), and suggests where each one belongs — asking a
quick clarifying question first when the right storage spot depends on an object's
sub-type (e.g. "what kind of bag is this?"). Sign-in (Google or email) is required;
each account has its own rooms.

## How it works

1. Sign in with Google or an email/password account (required — see `FIREBASE_SETUP.md`
   for the one-time console setup this needs).
2. Create a **space** (e.g. "Bedroom", "Hallway Closet"). Rename or delete spaces any
   time from the home screen's overflow menu.
3. Tap **Scan room** and take a photo.
4. The photo is sent to Google's Gemini API, which returns every object it sees,
   flags waste, and either gives a direct storage suggestion or asks a short
   clarifying question with a few quick-pick options.
5. Answer any clarifications — each answer triggers a follow-up call that returns a
   concrete suggested location and reason.
6. Save the resolved list to the space's inventory.
7. Edit your display name or sign out from the profile screen (tap the person icon).

## Project structure

```
app/src/main/java/com/airo/app/
  ui/            Compose screens (auth, profile, home, space detail, scan/review) + theme
  data/auth/     Firebase Authentication wrapper (email/password + Google)
  data/local/    Room database (spaces, saved inventory items), scoped per signed-in user
  data/remote/   Google Gemini API client (vision + placement reasoning)
  data/repository/  Combines network + persistence
  domain/model/  DetectedObject, ItemCategory
  di/            Minimal hand-rolled DI container (no Hilt, app is small)
```

## Setup

1. Copy `local.properties.example` to `local.properties` and fill in:
   - `GEMINI_API_KEY` (get a free one at https://aistudio.google.com/app/apikey)
   - `GOOGLE_WEB_CLIENT_ID` (from Firebase — see `FIREBASE_SETUP.md`)
2. Follow `FIREBASE_SETUP.md` to create a Firebase project, enable Email/Password and
   Google sign-in, and drop `google-services.json` into `app/`. The app won't compile
   sign-in correctly without this (it still builds, but auth calls fail at runtime).
3. Open the project in Android Studio (or run `./gradlew assembleDebug` from the
   command line) — it needs network access to Google's Maven repo and Maven
   Central to download the Android Gradle Plugin and AndroidX/Compose/Firebase
   dependencies on first sync.
4. Run on a device or emulator with a camera. Minimum SDK is 26.

For a release build (signed `.aab` for Play Store upload), see
`keystore.properties.example` and `PLAY_STORE_CHECKLIST.md`.

## Current scope (MVP)

This slice covers one full path end-to-end: sign in → scan → detect → clarify →
suggest → save, plus basic account/room management. Things intentionally left for
later iterations:
- Multi-photo/video room sweep instead of a single still photo
- Cleaning schedules / reminders
- Editing or deleting individual saved inventory items (only whole spaces, for now)
- Photo thumbnails stored alongside each item
- Cross-device sync (rooms/items are local-only, tied to the device + account)
