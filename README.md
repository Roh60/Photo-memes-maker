# Photo & Memes Maker

All-in-one Android app (Kotlin + Jetpack Compose): photo editor, festival/stylish templates,
2-photo frames, collage studio, meme maker with stickers and filters, and a Quotes maker.

## Storage
Everything is stored locally on the device (Room database, app private storage, and
exports to Pictures/PhotoMemesMaker). The app has no internet permission and no cloud backend.

## Build
Open in Android Studio and sync Gradle, or use the GitHub Actions workflow.

## Release (Play Store)
Set env vars `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD` (key alias: `upload`),
then run `./gradlew bundleRelease` to get the signed AAB.
Never commit `.jks` / `.keystore` files.
