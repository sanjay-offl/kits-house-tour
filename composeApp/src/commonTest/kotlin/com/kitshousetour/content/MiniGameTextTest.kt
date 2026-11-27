package com.kitshousetour.content

import com.kitshousetour.domain.RoomId
import com.kitshousetour.engine.validateMiniGame
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MiniGameTextTest {
    @Test
    fun everyRoomHasOneValidMiniGame() {
        assertEquals(RoomId.entries.toSet(), MiniGameText.games.map { it.roomId }.toSet())
        assertEquals(MiniGameText.games.size, MiniGameText.games.map { it.id }.toSet().size)
        assertTrue(MiniGameText.games.all { validateMiniGame(it).isEmpty() })
    }

    @Test
    fun gameLookupReturnsTheRequestedRoom() {
        RoomId.entries.forEach { roomId ->
            assertEquals(roomId, MiniGameText.gameFor(roomId).roomId)
        }
    }
}
