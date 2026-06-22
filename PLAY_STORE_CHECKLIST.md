# Play Store Submission Checklist

Manual steps in the Play Console / Firebase Console that can't be done from this repo.
Code-side prerequisites (signing config, target SDK, etc.) are already in place — see
`README.md` and `FIREBASE_SETUP.md` for what to provide before building a release.

## 1. Accounts
- [ ] Google Play Developer account (one-time $25 fee) at play.google.com/console.
- [ ] Firebase project set up per `FIREBASE_SETUP.md` (required for sign-in to work).

## 2. Release build artifacts
- [ ] Generate an upload keystore (`keytool -genkeypair ...`, command in
      `keystore.properties.example`) and **back it up somewhere safe** — losing it
      means you can never update the app under the same listing again.
- [ ] Fill in `keystore.properties` and `local.properties` (not committed to git).
- [ ] Build the release bundle: `./gradlew bundleRelease` → produces
      `app/build/outputs/bundle/release/app-release.aab`.

## 3. Store listing
- [ ] App name, short description (80 chars), full description (4000 chars).
- [ ] App icon (512x512 PNG) — can be exported from `ic_launcher_foreground.xml` +
      the green background, or redrawn at higher fidelity.
- [ ] Feature graphic (1024x500 PNG).
- [ ] At least 2 phone screenshots (recommend capturing the scan/review flow).
- [ ] Privacy policy URL — host `PRIVACY_POLICY.md` publicly (e.g. GitHub Pages) and
      fill in the real contact email first.

## 4. Play Console forms
- [ ] **App content > Privacy policy**: paste the hosted URL.
- [ ] **App content > Data safety**: declare camera photos (processed, not stored on
      our servers), account email (collected, used for authentication, not shared).
- [ ] **App content > Content rating questionnaire**: complete (this app has no
      mature content; should come back rated for all ages).
- [ ] **App content > Target audience**: select age groups (not designed for children).
- [ ] **App content > Government apps / Ads / News apps**: answer "No" as applicable.
- [ ] **Store settings > App category**: Lifestyle or Productivity.

## 5. Release
- [ ] Create an Internal testing release first, upload the `.aab`, add testers.
- [ ] Verify sign-in (Google + email) and the scan flow work on a real installed build.
- [ ] Promote to Production when ready.

## 6. Compliance notes
- `compileSdk`/`targetSdk` are set to 36 (Android 16), meeting Google Play's
  requirement that all app updates target API 36 by **August 31, 2026**.
- Release builds are minified (R8) with `proguard-rules.pro` — re-test the release
  build (not just debug) before shipping, since R8 can occasionally strip something
  reflection-based that debug builds don't exercise.
