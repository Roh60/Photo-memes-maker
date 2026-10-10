<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your app locally.

View your app in AI Studio: https://ai.studio/apps/1bd102fa-5cc0-4fe8-a5fe-6e973cd671f9

## Run Locally

**Prerequisites:**  [Android Studio](https://developer.android.com/studio)


1. Open Android Studio
2. Select **Open** and choose the directory containing this project
3. Allow Android Studio to fix any incompatibilities as it imports the project.
4. Create a file named `.env` in the project directory and set `GEMINI_API_KEY` in that file to your Gemini API key (see `.env.example` for an example)
5. Remove this line from the app's `build.gradle.kts` file: `signingConfig = signingConfigs.getByName("debugConfig")`
6. Run the app on an emulator or physical device
7. If you have already published your app in AI Studio, please [request upload key reset](https://support.google.com/googleplay/android-developer/answer/9842756#zippy=%2Crequest-an-upload-key-reset) in Google Play Console.

## Reliability improvements in this revision

- Large gallery images are decoded with a power-of-two sample size based on both dimensions, reducing avoidable memory pressure on tall or wide photos.
- Rotated photos now use expanded bitmap bounds so non-square images are less likely to be clipped.
- Gallery export verifies that the output stream and image encoding succeeded; failed partial exports are removed instead of being left behind.
- Export filenames are sanitized, local creation writes verify encoding, and stale share-cache images older than 24 hours are cleaned up when sharing.
- Photo, meme, and template save actions now reset their progress state after errors and report whether Gallery export actually succeeded.
- Photo template cards now preview a layered mini-poster (heading, framed art, and greeting), and the meme catalog has keyword search plus a no-results state.
- Unit checks were added for image downsampling and photo-template catalog consistency.

## Verification note

The source archive does not include the Gradle wrapper scripts/JAR, and this environment has no Android SDK installed. The Android Gradle build and device-level camera/gallery/export checks therefore could not be run here. Open the project in Android Studio, allow Gradle sync, run the unit tests, then test camera, rotation, gallery save, and sharing on a real Android device before release.


## Added feature step: Daily Greetings Studio

This additive update keeps the existing photo, collage, meme, template, camera, and creations flows in place and adds a Home entry for **Daily Greetings & Quotes**. The new screen includes category filters (Daily, Quotes, Festivals, Celebrations), original greeting designs, a live preview, optional name personalization, optional photo selection, and image sharing through Android's share sheet. The greeting image is generated locally and shared from the app cache; no new media-hosting service or third-party API is required.

Build/device verification is still required in Android Studio because this environment does not have Gradle or an Android SDK available. Please test opening the new screen, selecting a photo, and sharing to WhatsApp or another target app.

---

# Photo-memes-maker
