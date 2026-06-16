package io.wulfcodes.khelomilo.service.wordle;

import com.google.inject.Singleton;
import io.wulfcodes.khelomilo.model.wordle.WordleRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class WordleRoomManager {

    private static final Logger log = LoggerFactory.getLogger(WordleRoomManager.class);

    private final Map<String, WordleRoom> rooms = new ConcurrentHashMap<>();

    public WordleRoom getOrCreate(String roomId) {
        boolean existed = rooms.containsKey(roomId);
        WordleRoom room = rooms.computeIfAbsent(roomId, WordleRoom::new);
        if (!existed) {
            log.info("Created new Wordle room '{}'", roomId);
        } else {
            log.debug("Fetched existing Wordle room '{}'", roomId);
        }
        return room;
    }

    public WordleRoom get(String roomId) {
        return rooms.get(roomId);
    }

    public void removeIfEmpty(String roomId) {
        WordleRoom room = rooms.get(roomId);
        if (room != null && room.getPlayerCount() == 0) {
            rooms.remove(roomId);
            log.info("Wordle room '{}' removed (no players remaining)", roomId);
        }
    }
}
