package com.kitshousetour.content

import com.kitshousetour.domain.Lesson
import com.kitshousetour.domain.RoomId

/** The five short Kotlin lessons and labels used by the lesson card. */
object LessonText {
    val lessons = listOf(
        Lesson(
            id = "hello-kotlin",
            roomId = RoomId.LIVING_ROOM,
            title = "Hello, Kotlin",
            explanation = "A val stores a read-only value. A Kotlin program can start in fun main.",
            snippet = """
                fun main() {
                    val greeting = "Hello, Kotlin!"
                    println(greeting)
                }
            """.trimIndent(),
            kitSays = "A val keeps its value, and main is where our program begins!",
        ),
        Lesson(
            id = "val-and-var",
            roomId = RoomId.STUDY,
            title = "Values that stay and change",
            explanation = "Use val when a value should stay the same. Use var when you need to reassign it. Kotlin often infers the type.",
            snippet = """
                fun main() {
                    val maxRooms = 5
                    var roomsVisited = 0
                    roomsVisited += 1
                }
            """.trimIndent(),
            kitSays = "Use val for a name that stays the same; var can change.",
        ),
        Lesson(
            id = "data-class-copy",
            roomId = RoomId.DRESSING,
            title = "A fresh look with copy",
            explanation = "A data class stores related values. Its copy function makes a new instance and lets you change selected properties.",
            snippet = """
                data class Outfit(val color: String)
                val original = Outfit("blue")
                val newLook = original.copy(color = "purple")
            """.trimIndent(),
            kitSays = "copy() gives us a new data-class instance with our changes.",
        ),
        Lesson(
            id = "sealed-when",
            roomId = RoomId.KITCHEN,
            title = "Cover every choice",
            explanation = "A sealed type has a known set of subtypes. A when expression over it must handle every subtype or include an else branch.",
            snippet = """
                sealed interface Meal
                data object Soup : Meal
                data object Salad : Meal
                fun label(meal: Meal): String = when (meal) {
                    Soup -> "warm"
                    Salad -> "crunchy"
                }
            """.trimIndent(),
            kitSays = "A sealed when checks every subtype, or adds else.",
        ),
        Lesson(
            id = "suspend-delay",
            roomId = RoomId.BEDROOM,
            title = "Pause without blocking",
            explanation = "A suspend function can pause while waiting. delay suspends the coroutine so its thread can do other work.",
            snippet = """
                import kotlinx.coroutines.delay
                suspend fun wakeUp() {
                    delay(1_000)
                    println("Awake!")
                }
            """.trimIndent(),
            kitSays = "delay() pauses a coroutine without blocking its thread.",
        ),
    )

    private val lessonsByRoom = lessons.associateBy { it.roomId }

    const val KIT_SAYS_LABEL = "Kit says"
    const val READ_ALOUD = "Read Kit's line aloud"
    const val COMPLETE_LESSON = "Mark lesson complete"
    const val LESSON_COMPLETED = "Lesson completed"
    const val INVALID_LESSON = "This lesson is unavailable."

    /** Returns the authored lesson for [roomId], or fails if lesson content is missing. */
    fun lessonFor(roomId: RoomId): Lesson = checkNotNull(lessonsByRoom[roomId]) {
        "No lesson is defined for $roomId."
    }
}
