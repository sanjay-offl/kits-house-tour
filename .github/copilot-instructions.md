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
- Never delete or weaken a test to make the build pass.