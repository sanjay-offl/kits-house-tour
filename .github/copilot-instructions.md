# Project rules: Kit's House Tour

This is a Kotlin Multiplatform project for the Kotlin Multiplatform Contest 2026.

## Non negotiable rules
- Targets: Android and Desktop (JVM). iOS is optional and must never break the Android or Desktop builds.
- All business logic, state, lessons, and UI live in commonMain. Platform code only handles text to speech, the progress file path, and window or activity setup. Keep platform code under 10 percent of lines.
- Use expect/actual for every platform API.
- Never use a JetBrains or Kotlin Foundation mascot, name, or logo. The character is Kit, an original design.
- No template or "Hello World" leftovers. Delete any generated template text.
- All text in English.
- Do not add libraries that are not needed for the current stage. Before using any library or API, confirm it exists in the official docs or in libs.versions.toml. If unsure, say so instead of guessing.
- Do not change the Kotlin, Compose Multiplatform, or Gradle versions unless the current stage asks for it.

## Working style
- Work on one stage at a time. Stop at the end of each stage and wait for confirmation.
- Make the smallest change that completes the stage. Do not refactor unrelated files.
- When a build fails, fix the specific error. Do not rewrite working files.
- After each stage, list every file created or changed, with a one line reason for each.
- Keep comments only where the logic is not obvious.
- Never claim something works unless you ran it. Say what you verified and what you could not verify.

## Architecture
Packages under composeApp/src/commonMain/kotlin:
- domain: Room, Interaction, Lesson, QuizQuestion, Badge
- engine: KitMood (sealed interface: Happy, Curious, Tired, Proud), KitStateMachine (pure functions), HouseGame (room progress, unlock rules, scoring)
- content: lesson text, code snippets, Kit's lines, and quiz questions as plain Kotlin objects. No hardcoded text in UI files.
- data: ProgressRepository interface and a JSON implementation using kotlinx.serialization
- ui: HouseScreen, RoomScreen, LessonCard, QuizScreen, BadgeScreen, SettingsScreen, KitCharacter, SpeechBubble, Theme
- platform: expect declarations for SpeechSynthesizer and ProgressFileLocation, with actual implementations in androidMain and jvmMain

## Testing rules
- Engine, mood transitions, lessons, quiz scoring, and persistence must have unit tests in commonTest.
- Run ./gradlew :composeApp:jvmTest before reporting a stage as done.
- Never delete or weaken a test to make the build pass.Read 
Completed with input: {
  "path": "/home/sanjay/kits-house-tour/.github/copilot-instructions.md"
}

I’ve read the instructions. I’ll treat this as the approved Stage 3 refinement: keep the Stage 2 engine/domain intact, add palette and animation behavior in `commonMain`, and test the pure theme/content helpers. I’m checking the current screen implementation and the pinned Compose version before editing.

Read 
Completed with input: {
  "path": "/home/sanjay/kits-house-tour/composeApp/src/commonMain/kotlin/com/kitshousetour/App.kt"
}

Read 
Completed with input: {
  "path": "/home/sanjay/kits-house-tour/composeApp/src/commonMain/kotlin/com/kitshousetour/ui/Theme.kt"
}

Read 
Completed with input: {
  "path": "/home/sanjay/kits-house-tour/composeApp/src/commonMain/kotlin/com/kitshousetour/ui/HouseScreen.kt"
}

Read 
Completed with input: {
  "path": "/home/sanjay/kits-house-tour/composeApp/src/commonMain/kotlin/com/kitshousetour/ui/KitCharacter.kt"
}

Read 
Completed with input: {
  "path": "/home/sanjay/kits-house-tour/composeApp/src/commonMain/kotlin/com/kitshousetour/ui/SpeechBubble.kt"
}

Read 
Completed with input: {
  "path": "/home/sanjay/kits-house-tour/composeApp/src/commonMain/kotlin/com/kitshousetour/ui/RoomScreen.kt"
}

Read 
Completed with input: {
  "path": "/home/sanjay/kits-house-tour/composeApp/src/commonMain/kotlin/com/kitshousetour/content/RoomText.kt"
}

Read 
Completed with input: {
  "path": "/home/sanjay/kits-house-tour/gradle/libs.versions.toml"
}

Read 
Completed with input: {
  "path": "/home/sanjay/kits-house-tour/composeApp/build.gradle.kts"
}

Edit , 

Edit 

Edit 

Edit 

Edit 

Edit 

Edit 

Edit , 

Ran terminal command: ./gradlew :composeApp:jvmTest

Edit 

Ran terminal command: ./gradlew :composeApp:jvmTest

Ran terminal command: ANDROID_HOME="$HOME/Android/Sdk" ANDROID_SDK_ROOT="$HOME/Android/Sdk" ./gradlew :composeApp:assembleDebug

Search tools
Completed with input: {
  "query": "Capture screenshot of the currently running native desktop app window or screen in VS Code to inspect its rendered UI."
}

The theme models, four palettes, selector, mood animation, bubble timing, and screen transition are implemented. The JVM suite now passes after correcting a Compose graphics import, and the Android assembly succeeds; I’m checking the final Desktop launch and test totals.

Ran terminal command: ANDROID_HOME="$HOME/Android/Sdk" ANDROID_SDK_ROOT="$HOME/Android/Sdk" ./gradlew :composeApp:run

Ran terminal command: xwininfo -root -tree | grep -A1 -B1 "Kit's House Tour"; command -v ffmpeg || true; command -v scrot || true; command -v gnome-screenshot || true

Ran terminal command: python3 - <<'PY'
from pathlib import Path
import xml.etree.ElementTree as ET
reports = sorted(Path('composeApp/build/test-results/jvmTest').glob('TEST-*.xml'))
total = 0
for report in reports:
    root = ET.parse(report).getroot()
    count = int(root.attrib.get('tests', 0))
    errors = int(root.attrib.get('errors', 0)) + int(root.attrib.get('failures', 0))
    skipped = int(root.attrib.get('skipped', 0))
    total += count
    print(f"{root.attrib.get('name')}: {count} tests, {errors} failures, {skipped} skipped")
print(f"TOTAL: {total}")
PY

Ran terminal command: scrot -u /tmp/kits-house-stage3.png && file /tmp/kits-house-stage3.png && identify /tmp/kits-house-stage3.png 2>/dev/null || true

Read 
Completed with input: {
  "path": "/tmp/kits-house-stage3.png"
}

Ran terminal command: scrot /tmp/kits-house-screen.png && identify /tmp/kits-house-screen.png

Read 
Completed with input: {
  "path": "/tmp/kits-house-screen.png"
}

Edit 

Edit 

Ran terminal command: ANDROID_HOME="$HOME/Android/Sdk" ANDROID_SDK_ROOT="$HOME/Android/Sdk" ./gradlew :composeApp:jvmTest :composeApp:assembleDebug

Read Terminal
Completed with input: {
  "shellId": "62",
  "delay": 0
}

Ran terminal command: python3 - <<'PY'
import ctypes
class D(ctypes.Union): _fields_ = [("l", ctypes.c_long * 5), ("b", ctypes.c_char * 20), ("s", ctypes.c_short * 10)]
class C(ctypes.Structure): _fields_ = [("type", ctypes.c_int), ("serial", ctypes.c_ulong), ("send_event", ctypes.c_int), ("display", ctypes.c_void_p), ("window", ctypes.c_ulong), ("message_type", ctypes.c_ulong), ("format", ctypes.c_int), ("data", D)]
class E(ctypes.Union): _fields_ = [("type", ctypes.c_int), ("xclient", C), ("pad", ctypes.c_long * 24)]
x = ctypes.CDLL("libX11.so.6"); x.XOpenDisplay.restype = ctypes.c_void_p; d = x.XOpenDisplay(None)
x.XInternAtom.argtypes = [ctypes.c_void_p, ctypes.c_char_p, ctypes.c_int]; x.XInternAtom.restype = ctypes.c_ulong
p = x.XInternAtom(d, b"WM_PROTOCOLS", 0); a = x.XInternAtom(d, b"WM_DELETE_WINDOW", 0)
e = E(); e.xclient.type = 33; e.xclient.send_event = 1; e.xclient.display = d; e.xclient.window = 0x1800007; e.xclient.message_type = p; e.xclient.format = 32; e.xclient.data.l[0] = a
x.XSendEvent.argtypes = [ctypes.c_void_p, ctypes.c_ulong, ctypes.c_int, ctypes.c_long, ctypes.POINTER(E)]; x.XSendEvent(d, 0x1800007, 0, 0, ctypes.byref(e)); x.XFlush(d); x.XCloseDisplay(d)
PY
rm -f /tmp/kits-house-stage3.png /tmp/kits-house-screen.png

Read Terminal
Completed with input: {
  "shellId": "62",
  "delay": 15
}

Ran terminal command: xwininfo -root -tree | grep -m1 -E 'Visual Studio Code|Code -'; scrot -u /tmp/kits-house-foreground-check.png; file /tmp/kits-house-foreground-check.png

Read 
Completed with input: {
  "path": "/tmp/kits-house-foreground-check.png"
}

Ran terminal command: rm -f /tmp/kits-house-foreground-check.png

The Desktop run remained alive and opened the app window. The available X11 screenshot utility returns black for both the app and VS Code, so it cannot validate rendered pixels here; I’m doing a final source and asset check rather than treating that screenshot as proof of a blank screen.

Search for `Color\s*\(`

Ran terminal command: find composeApp/src -type f \( -iname '*.png' -o -iname '*.jpg' -o -iname '*.jpeg' -o -iname '*.gif' -o -iname '*.svg' -o -iname '*.webp' \) -print

Ran terminal command: python3 - <<'PY'
from pathlib import Path
import xml.etree.ElementTree as ET
reports = sorted(Path('composeApp/build/test-results/jvmTest').glob('TEST-*.xml'))
total = 0
for report in reports:
    root = ET.parse(report).getroot()
    count = int(root.attrib.get('tests', 0))
    errors = int(root.attrib.get('errors', 0)) + int(root.attrib.get('failures', 0))
    skipped = int(root.attrib.get('skipped', 0))
    total += count
    print(f"{root.attrib.get('name')}: {count} tests, {errors} failures, {skipped} skipped")
print(f"TOTAL: {total}")
PY

## Stage 3 implementation complete

**Validation**
- `:composeApp:jvmTest`: passed — **51 tests**, 0 failures.
  - `KitTextTest`: 4 passed
  - `RoomTextTest`: 4 passed
  - `LessonValidationTest`: 4 passed
  - `QuizQuestionTest`: 6 passed
  - `HouseGameTest`: 18 passed
  - `KitStateMachineTest`: 12 passed
  - `ThemeSettingsTest`: 3 passed
- `:composeApp:assembleDebug`: passed.
- `:composeApp:run`: launched successfully and opened a Desktop window titled “Kit’s House Tour.”
- No dependencies or image assets were added; Kotlin and Compose versions are unchanged.

**Files created or changed**
- `App.kt` — holds in-memory progress, mood, palette, and navigation state; adds timed expiry and screen transitions.
- `Theme.kt` — defines four light/dark palettes, `ThemeSettings`, and shared Material 3 theme logic.
- `ThemeChooser.kt` — adds accessible palette and dark-mode controls.
- `HouseScreen.kt` — adds the room grid/column, progress, chooser, and Kit.
- `KitCharacter.kt` — draws Kit with Compose shapes and mood, blink, idle, hop, tilt, and tap animations.
- `SpeechBubble.kt` — adds the tailed bubble with timed fade/scale visibility.
- `RoomScreen.kt` — applies the selected palette to the room placeholder screen.
- `RoomText.kt` — adds theme control labels while retaining room and Kit content.
- `ThemeSettingsTest.kt` — tests palette variants and dark-mode toggling.
- `KitTextTest.kt` — tests the speech-line selection for each mood.

**Verification limits:** The app opened and stayed running, but I could not confirm the rendered house and Kit visually: the available screenshot capture returned black for both the app and VS Code. I did not manually exercise the room, theme, or animation interactions, or launch on an Android device. The asset check found no image files.