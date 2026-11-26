package com.kitshousetour.content

import com.kitshousetour.domain.RoomId
import com.kitshousetour.engine.KitMood
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class RoomTextTest {
    @Test
    fun providesTitlesAndPromptsForAllFiveRooms() {
        assertEquals(
            listOf(
                RoomId.LIVING_ROOM,
                RoomId.STUDY,
                RoomId.DRESSING,
                RoomId.KITCHEN,
                RoomId.BEDROOM,
            ),
            RoomText.rooms.map { it.id },
        )
        assertFalse(RoomText.rooms.any { it.title.isBlank() || it.interactionPrompt.isBlank() })
    }

    @Test
    fun everyRoomHasAnIconAndAccessibleLabel() {
        RoomText.rooms.forEach { room ->
            assertFalse(RoomText.iconFor(room.id).isBlank())
            assertEquals(
                "${room.title}. ${room.interactionPrompt}",
                RoomText.accessibilityLabel(room),
            )
        }
    }

    @Test
    fun progressLineReportsVisitedAndTotalRooms() {
        assertEquals("Rooms visited: 2 of 5", RoomText.progressLine(2, 5))
    }

    @Test
    fun kitHasDistinctSpeechLinesForEveryMood() {
        val moods = listOf(KitMood.Happy, KitMood.Curious, KitMood.Tired, KitMood.Proud)
        val lines = moods.map(KitText::lineForMood)

        assertEquals(moods.size, lines.toSet().size)
        assertFalse(lines.any(String::isBlank))
        assertNotEquals(KitText.ACCESSIBILITY_DESCRIPTION, lines.first())
    }
}
