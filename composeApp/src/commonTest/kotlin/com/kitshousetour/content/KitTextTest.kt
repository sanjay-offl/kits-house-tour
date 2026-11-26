package com.kitshousetour.content

import com.kitshousetour.engine.KitMood
import kotlin.test.Test
import kotlin.test.assertEquals

class KitTextTest {
    @Test
    fun happyKitUsesFriendlyGreeting() {
        assertEquals("Hi, friend! I'm Kit. Let's explore!", KitText.lineForMood(KitMood.Happy))
    }

    @Test
    fun curiousKitUsesCuriousLine() {
        assertEquals("Ooh, what's in this room?", KitText.lineForMood(KitMood.Curious))
    }

    @Test
    fun tiredKitUsesRestfulLine() {
        assertEquals("Whew, what a lovely place to rest.", KitText.lineForMood(KitMood.Tired))
    }

    @Test
    fun proudKitUsesEncouragingLine() {
        assertEquals("We did it! I'm so proud of you!", KitText.lineForMood(KitMood.Proud))
    }
}
