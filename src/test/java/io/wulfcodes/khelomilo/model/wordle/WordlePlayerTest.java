package io.wulfcodes.khelomilo.model.wordle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WordlePlayerTest {

    @Test
    void testPlayerInitialization() {
        WordlePlayer player = new WordlePlayer("testuser", null);

        assertEquals("testuser", player.getUsername());
        assertFalse(player.isReady(), "Player should not be ready initially");
        assertFalse(player.hasWon(), "Player should not have won initially");
        assertFalse(player.hasLost(), "Player should not have lost initially");
        assertEquals(0, player.getGuessesUsed(), "Player should have 0 guesses initially");
    }

    @Test
    void testGuessesIncrement() {
        WordlePlayer player = new WordlePlayer("testuser", null);
        player.incrementGuesses();
        assertEquals(1, player.getGuessesUsed());

        player.incrementGuesses();
        assertEquals(2, player.getGuessesUsed());
    }

    @Test
    void testWinLossStates() {
        WordlePlayer player = new WordlePlayer("testuser", null);
        player.setWon(true);
        assertTrue(player.hasWon());
        assertFalse(player.hasLost());

        player.setWon(false);
        player.setLost(true);
        assertTrue(player.hasLost());
        assertFalse(player.hasWon());
    }
}
