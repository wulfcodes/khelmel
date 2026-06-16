package io.wulfcodes.khelomilo.service.wordle;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Singleton
public class WordDictionaryService {

    private static final Logger log = LoggerFactory.getLogger(WordDictionaryService.class);
    private final List<String> words = new ArrayList<>();
    private final Random random = new Random();

    public WordDictionaryService() {
        loadWords();
    }

    private void loadWords() {
        try (InputStream is = getClass().getResourceAsStream("/words.txt")) {
            if (is == null) {
                log.error("Could not find words.txt in resources!");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().length() == 5) {
                        words.add(line.trim().toUpperCase());
                    }
                }
            }
            log.info("Loaded {} words into the Wordle dictionary.", words.size());
        } catch (Exception e) {
            log.error("Failed to load word dictionary", e);
        }
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            log.error("Dictionary is empty! Returning fallback word.");
            return "ERROR";
        }
        return words.get(random.nextInt(words.size()));
    }

    public boolean isValidWord(String word) {
        if (word == null || word.length() != 5) return false;
        return words.contains(word.toUpperCase());
    }
}
