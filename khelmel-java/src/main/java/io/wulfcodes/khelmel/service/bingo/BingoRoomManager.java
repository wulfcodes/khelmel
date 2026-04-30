package io.wulfcodes.khelmel.service.bingo;

import com.google.inject.Singleton;
import io.wulfcodes.khelmel.model.bingo.BingoRoom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class BingoRoomManager {

    private static final Logger log = LoggerFactory.getLogger(BingoRoomManager.class);

    private final Map<String, BingoRoom> rooms = new ConcurrentHashMap<>();

    public BingoRoom getOrCreate(String roomId) {
        boolean existed = rooms.containsKey(roomId);
        BingoRoom room = rooms.computeIfAbsent(roomId, BingoRoom::new);
        if (!existed) {
            log.info("Created new room '{}'", roomId);
        } else {
            log.debug("Fetched existing room '{}'", roomId);
        }
        return room;
    }

    public BingoRoom get(String roomId) {
        BingoRoom room = rooms.get(roomId);
        if (room == null) {
            log.debug("Room '{}' not found", roomId);
        }
        return room;
    }

    public void removeIfEmpty(String roomId) {
        BingoRoom room = rooms.get(roomId);
        if (room != null && room.getPlayerCount() == 0) {
            rooms.remove(roomId);
            log.info("Room '{}' removed (no players remaining)", roomId);
        }
    }
}
