# SkillTok - Android App

## Cursor Cloud specific instructions

### Project overview

SkillTok is a native Android app (Kotlin + Jetpack Compose) with a Firebase backend (Auth + Data Connect / Cloud SQL PostgreSQL). It's a TikTok-style educational platform combining short-form video reels with structured courses.

### Prerequisites

- **JDK 21** (OpenJDK) — required by `compileOptions` in `app/build.gradle`
- **Android SDK** at `/opt/android-sdk` with platform 36, build-tools 36.0.0, and platform-tools
- `local.properties` must contain `sdk.dir=/opt/android-sdk` (this file is gitignored)
- Environment variables: `ANDROID_HOME=/opt/android-sdk`, `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64`

### Build and test commands

| Action | Command |
|---|---|
| Debug build | `./gradlew assembleDebug` |
| Unit tests | `./gradlew test` |
| Lint | `./gradlew lint` |
| Clean | `./gradlew clean` |
| Release build | `./gradlew assembleRelease` |

### Known issues

- **Lint error (pre-existing):** `activity_main.xml` references `CoordinatorLayout` but the dependency (`androidx.coordinatorlayout`) is not declared in `app/build.gradle`. `./gradlew lint` exits with error code 1 due to this. This is not caused by environment setup.
- **No unit/integration test sources** currently exist in the project (`src/test/` and `src/androidTest/` are empty).

### Running the app

This is a native Android app — it cannot be run in a headless cloud VM. To run it:
- Use an Android emulator (requires KVM / hardware acceleration, not available in cloud VMs)
- Or install the debug APK (`app/build/outputs/apk/debug/app-debug.apk`) on a physical device

### Architecture notes

- Firebase config is in `app/google-services.json` (committed to repo)
- Firebase Data Connect schema: `dataconnect/schema/schema.gql`
- Auto-generated Data Connect SDK: `app/src/main/java/com/skilltok/app/dataconnect/`
- Local encrypted Room/SQLCipher cache with offline-first sync pattern
- Mock data seeding from `MockData.kt` when remote DB is empty
