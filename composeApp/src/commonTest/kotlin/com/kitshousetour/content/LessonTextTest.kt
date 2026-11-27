package com.kitshousetour.content

import com.kitshousetour.domain.RoomId
import com.kitshousetour.domain.validationErrors
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LessonTextTest {
    @Test
    fun everyRoomHasOneValidLesson() {
        assertEquals(RoomId.entries.toSet(), LessonText.lessons.map { it.roomId }.toSet())
        assertEquals(LessonText.lessons.size, LessonText.lessons.map { it.id }.toSet().size)
        assertTrue(LessonText.lessons.all { it.validationErrors().isEmpty() })
    }

    @Test
    fun kitLinesRepeatTheRoomConcept() {
        val requiredTerms = mapOf(
            RoomId.LIVING_ROOM to "val",
            RoomId.STUDY to "var",
            RoomId.DRESSING to "copy()",
            RoomId.KITCHEN to "sealed",
            RoomId.BEDROOM to "delay()",
        )

        LessonText.lessons.forEach { lesson ->
            assertTrue(lesson.kitSays.contains(requiredTerms.getValue(lesson.roomId), ignoreCase = true))
        }
    }
}
