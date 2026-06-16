package io.wulfcodes.khelomilo.service.wordle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordDictionaryServiceTest {

    private WordDictionaryService dictionaryService;

    @BeforeEach
    void setUp() {
        // Will load the words.txt from resources if available
        dictionaryService = new WordDictionaryService();
    }

    @Test
    void testGetRandomWord() {
        String randomWord = dictionaryService.getRandomWord();
        assertNotNull(randomWord, "Random word should not be null");
        assertEquals(5, randomWord.length(), "Random word must be exactly 5 letters long");
    }

    @Test
    void testIsValidWord() {
        // Since we loaded the SGB list, "APPLE" is generally valid
        assertTrue(dictionaryService.isValidWord("APPLE"), "'APPLE' should be a valid word");
        assertTrue(dictionaryService.isValidWord("apple"), "Method should be case-insensitive");
        
        assertFalse(dictionaryService.isValidWord("ZZZZZ"), "'ZZZZZ' should not be in the dictionary");
        assertFalse(dictionaryService.isValidWord(""), "Empty string should be invalid");
        assertFalse(dictionaryService.isValidWord(null), "Null should be invalid");
        assertFalse(dictionaryService.isValidWord("LONGWORD"), "Words longer than 5 letters should be invalid");
        assertFalse(dictionaryService.isValidWord("CAT"), "Words shorter than 5 letters should be invalid");
    }
}
