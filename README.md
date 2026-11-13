# 2026-2027-BioBuzz

## Building with Gradle

Requirements:
- **JDK 25.** Android Studio's bundled JDK works. Other JDK 25 installs also work.
- **Android SDK.** Installed through Android Studio.

Set `JAVA_HOME` before running `./gradlew`:

macOS (Android Studio's bundled JDK):
```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
```

Windows (PowerShell, Android Studio's bundled JDK):
```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
```

Then build:
```bash
./gradlew :TeamCode:assembleDebug
```

Notes:
- `local.properties` holds your machine's Android SDK path (`sdk.dir=...`). Android Studio creates it automatically. Don't edit it by hand, and don't share your copy.
- Build output folders (`build/`, `.gradle/`) are generated. They change on every build, so don't commit them.
- `libs/ftc.debug.keystore` is the shared debug signing key the build uses. Keep it in the repo.