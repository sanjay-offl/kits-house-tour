package com.kitshousetour.content

import com.kitshousetour.domain.Room
import com.kitshousetour.domain.RoomId
import com.kitshousetour.engine.KitMood

/** House room titles, prompts, icons, and shared screen labels. */
object RoomText {
    val rooms = listOf(
        Room(RoomId.LIVING_ROOM, "Living Room", "Tap the sofa to meet Kit"),
        Room(RoomId.STUDY, "Study Room", "Tap the bookshelf"),
        Room(RoomId.DRESSING, "Dressing Room", "Tap the wardrobe"),
        Room(RoomId.KITCHEN, "Kitchen", "Tap the fridge"),
        Room(RoomId.BEDROOM, "Bedroom", "Tap the bed"),
    )

    private val icons = mapOf(
        RoomId.LIVING_ROOM to "🛋️",
        RoomId.STUDY to "📚",
        RoomId.DRESSING to "👗",
        RoomId.KITCHEN to "🧊",
        RoomId.BEDROOM to "🛏️",
    )

    const val APP_TITLE = "Kit's House Tour"
    const val BACK_BUTTON = "Back"
    const val BACK_BUTTON_DESCRIPTION = "Go back to the house"
    const val PLACEHOLDER_LESSON = "Lesson coming in the next stage."
    const val THEME_CHOOSER_DESCRIPTION = "Choose a color palette"
    const val PURPLE_PALETTE = "Purple"
    const val WHITE_PALETTE = "White"
    const val BLACK_PALETTE = "Black"
    const val GREY_PALETTE = "Grey"
    const val SELECT_PURPLE_PALETTE = "Select Purple palette"
    const val SELECT_WHITE_PALETTE = "Select White palette"
    const val SELECT_BLACK_PALETTE = "Select Black palette"
    const val SELECT_GREY_PALETTE = "Select Grey palette"
    const val DARK_MODE_LABEL = "Dark mode"
    const val DARK_MODE_DESCRIPTION = "Toggle dark mode"

    /** Returns the room's decorative icon. */
    fun iconFor(roomId: RoomId): String = icons.getValue(roomId)

    /** Returns an accessible description containing the room name and its tap prompt. */
    fun accessibilityLabel(room: Room): String = "${room.title}. ${room.interactionPrompt}"

    /** Formats the visited-room count for the house screen. */
    fun progressLine(visitedCount: Int, roomCount: Int): String =
        "Rooms visited: $visitedCount of $roomCount"
}

/** Kit's speech and accessibility text. */
object KitText {
    const val ACCESSIBILITY_DESCRIPTION = "Tap Kit for a greeting"

    private const val HAPPY_LINE = "Hi, friend! I'm Kit. Let's explore!"
    private const val CURIOUS_LINE = "Ooh, what's in this room?"
    private const val TIRED_LINE = "Whew, what a lovely place to rest."
    private const val PROUD_LINE = "We did it! I'm so proud of you!"

    /** Returns Kit's speech line for the current mood. */
    fun lineForMood(mood: KitMood): String = when (mood) {
        KitMood.Happy -> HAPPY_LINE
        KitMood.Curious -> CURIOUS_LINE
        KitMood.Tired -> TIRED_LINE
        KitMood.Proud -> PROUD_LINE
    }
}
