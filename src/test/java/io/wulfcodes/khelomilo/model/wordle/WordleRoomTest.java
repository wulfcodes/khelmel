package io.wulfcodes.khelomilo.model.wordle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordleRoomTest {

    private WordleRoom room;

    @BeforeEach
    void setUp() {
        room = new WordleRoom("room123");
    }

    @Test
    void testRoomInitialization() {
        assertEquals("room123", room.getRoomId());
        assertEquals(0, room.getPlayerCount());
        assertEquals(WordleGameState.WAITING, room.getState());
        assertFalse(room.isFull());
    }

    @Test
    void testAddPlayer() {
        boolean added1 = room.addPlayer("player1", null);
        assertTrue(added1, "First player should be added");
        assertEquals(1, room.getPlayerCount());
        assertEquals(WordleGameState.WAITING, room.getState());

        boolean added2 = room.addPlayer("player2", null);
        assertTrue(added2, "Second player should be added");
        assertEquals(2, room.getPlayerCount());
        assertTrue(room.isFull(), "Room should be full");
        assertEquals(WordleGameState.LOBBY, room.getState(), "State should transition to LOBBY when full");

        boolean added3 = room.addPlayer("player3", null);
        assertFalse(added3, "Third player should not be added");
    }

    @Test
    void testCannotAddDuplicatePlayer() {
        room.addPlayer("player1", null);
        boolean added2 = room.addPlayer("player1", null);
        assertFalse(added2, "Duplicate username should not be allowed");
        assertEquals(1, room.getPlayerCount());
    }

    @Test
    void testRemovePlayer() {
        room.addPlayer("player1", null);
        room.addPlayer("player2", null);
        assertEquals(WordleGameState.LOBBY, room.getState());

        room.removePlayer("player1");
        assertEquals(1, room.getPlayerCount());
        assertEquals(WordleGameState.WAITING, room.getState(), "State should revert to WAITING");
    }

    @Test
    void testGetOpponent() {
        room.addPlayer("player1", null);
        room.addPlayer("player2", null);

        WordlePlayer opponentFor1 = room.getOpponent("player1");
        assertNotNull(opponentFor1);
        assertEquals("player2", opponentFor1.getUsername());

        WordlePlayer opponentFor2 = room.getOpponent("player2");
        assertNotNull(opponentFor2);
        assertEquals("player1", opponentFor2.getUsername());
    }
}
