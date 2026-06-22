# Firebase Setup (required for sign-in)

AIRO requires sign-in to use the app at all. Sign-in is handled by Firebase
Authentication. None of this can be done from the repo — it's one-time manual setup
in the Firebase console.

## 1. Create the Firebase project
1. Go to https://console.firebase.google.com and create a project (or reuse one).
2. Add an Android app to the project with package name **`com.airo.app`**.
3. Generate the SHA-1 signing fingerprints and add both to the Firebase Android app:
   - Debug: `./gradlew signingReport` (look for the `debug` variant's SHA1), or
     `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android`.
   - Release: same command against your upload keystore, once you've created one
     (see `keystore.properties.example`). Re-add this fingerprint if you ever
     regenerate the keystore.
4. Download `google-services.json` and place it at `app/google-services.json`
   (this path is gitignored — it's per-developer/per-project config, not a secret
   to commit, but also not something to share publicly).

## 2. Enable sign-in providers
In the Firebase console: **Authentication > Sign-in method**, enable:
- **Email/Password**
- **Google** — Firebase will generate a "Web client ID" (a `....apps.googleusercontent.com`
  value). Copy it.

## 3. Wire up the Web client ID
Add the Web client ID from step 2 to your local `local.properties`:
```
GOOGLE_WEB_CLIENT_ID=your-id.apps.googleusercontent.com
```
This is read into `BuildConfig.GOOGLE_WEB_CLIENT_ID` and passed to Credential
Manager's `GetGoogleIdOption` when the app requests a Google sign-in credential.

## 4. Build
Once `app/google-services.json` exists, the `com.google.gms.google-services` Gradle
plugin is applied automatically (see `app/build.gradle.kts`) and the build will wire
Firebase config into the app. Without that file present, the project still configures
and builds — Firebase Auth calls will simply fail at runtime until it's added.

## Notes
- This app is **local-only, scoped to account**: Firebase Auth handles identity, but
  rooms/items stay in the on-device Room database (tagged with the signed-in user's
  ID). There's no Firestore/cloud sync — data doesn't follow you across devices and
  is lost on uninstall. That's an intentional MVP tradeoff, not a bug.
- Sign-in is required; there's no guest mode.
