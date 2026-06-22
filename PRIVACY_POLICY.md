# AIRO Privacy Policy

_Last updated: 2026-06-22_

This policy covers what AIRO ("the app") collects and how it's used. Replace the
contact email below with a real address before publishing, and host this file at a
public URL (e.g. GitHub Pages) — Play Console requires a live privacy policy link.

## What the app collects

**Account information.** AIRO requires sign-in via Firebase Authentication, using
either Google Sign-In or an email address and password. We (via Firebase) store your
email address, display name, and profile photo URL if provided. Passwords are never
seen or stored by us directly — they're handled entirely by Firebase Authentication.

**Room photos.** When you scan a room, the photo is sent to Anthropic's Claude API
for object detection and is not stored by us beyond the request needed to analyze it.
See Anthropic's own privacy policy for how they handle API inputs.

**Rooms and inventory items.** Names of spaces ("Bedroom", "Hallway Closet") and the
items you scan and save (names, categories, suggested storage locations) are stored
**only on your device**, in a local database tied to your signed-in account. This data
is **not synced to any server** and is **deleted if you uninstall the app**.

## What we don't do

- No advertising or ad tracking.
- No sale or sharing of personal data with third parties beyond the processors
  named above (Firebase for authentication, Anthropic for photo analysis).
- No analytics SDKs.

## Permissions

- **Camera**: required to scan rooms. Photos are only used for the scan you take.
- **Internet**: required to reach Firebase (sign-in) and Anthropic (object detection).

## Data deletion

You can delete your account's local data by uninstalling the app, or by deleting
individual spaces from within the app. To delete your Firebase account itself, contact
us at the address below.

## Contact

Questions about this policy: **[your-email@example.com]**
