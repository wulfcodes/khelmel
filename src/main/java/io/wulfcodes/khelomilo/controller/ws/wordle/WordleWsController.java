package io.wulfcodes.khelomilo.controller.ws.wordle;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import io.wulfcodes.khelomilo.model.wordle.WordleGameState;
import io.wulfcodes.khelomilo.model.wordle.WordlePlayer;
import io.wulfcodes.khelomilo.model.wordle.WordleRoom;
import io.wulfcodes.khelomilo.service.wordle.WordDictionaryService;
import io.wulfcodes.khelomilo.service.wordle.WordleRoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordleWsController {

    private static final Logger log = LoggerFactory.getLogger(WordleWsController.class);
    private static final Gson GSON = new Gson();

    public static void onConnect(WsConnectContext ctx, WordleRoomManager manager) {
        String roomId = ctx.pathParam("roomId");
        String username = ctx.pathParam("username");

        log.info("Connection attempt — room='{}' username='{}'", roomId, username);

        WordleRoom room = manager.getOrCreate(roomId);

        if (room.isFull()) {
            ctx.send(msg("ROOM_FULL", null));
            ctx.session.close();
            return;
        }

        boolean added = room.addPlayer(username, ctx);
        if (!added) {
            ctx.send(msg("USERNAME_TAKEN", null));
            ctx.session.close();
            return;
        }

        ctx.attribute("roomId", roomId);
        ctx.attribute("username", username);

        if (room.getPlayerCount() == 1) {
            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("opponentUsername", "Unknown");
            data.addProperty("opponentStatus", "not_connected");
            ctx.send(msg("JOINED", data));
        } else {
            WordlePlayer opponent = room.getOpponent(username);
            
            JsonObject newPlayerData = new JsonObject();
            newPlayerData.addProperty("username", username);
            newPlayerData.addProperty("opponentUsername", opponent.getUsername());
            newPlayerData.addProperty("opponentStatus", "waiting");
            ctx.send(msg("JOINED", newPlayerData));

            JsonObject existingData = new JsonObject();
            existingData.addProperty("opponentUsername", username);
            existingData.addProperty("opponentStatus", "waiting");
            room.sendTo(opponent.getUsername(), msg("PLAYER_JOINED", existingData));
        }
    }

    public static void onMessage(WsMessageContext ctx, WordleRoomManager manager, WordDictionaryService dictionary) {
        String roomId = ctx.attribute("roomId");
        String username = ctx.attribute("username");

        if (roomId == null || username == null) return;
        WordleRoom room = manager.get(roomId);
        if (room == null) return;

        try {
            JsonObject msg = GSON.fromJson(ctx.message(), JsonObject.class);
            String type = msg.get("type").getAsString();

            switch (type) {
                case "PING" -> ctx.send(msg("PONG", null));
                case "READY" -> handleReady(room, username, dictionary);
                case "GUESS" -> handleGuess(room, username, msg, dictionary);
            }
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }

    public static void onClose(WsCloseContext ctx, WordleRoomManager manager) {
        String roomId = ctx.attribute("roomId");
        String username = ctx.attribute("username");

        if (roomId == null || username == null) return;

        WordleRoom room = manager.get(roomId);
        if (room == null) return;

        room.removePlayer(username);

        JsonObject data = new JsonObject();
        data.addProperty("username", username);
        room.broadcast(msg("PLAYER_DISCONNECTED", data));

        manager.removeIfEmpty(roomId);
    }

    private static void handleReady(WordleRoom room, String username, WordDictionaryService dictionary) {
        WordlePlayer player = room.getPlayer(username);
        if (player == null || (room.getState() != WordleGameState.LOBBY && room.getState() != WordleGameState.WAITING)) return;

        player.setReady(true);

        JsonObject data = new JsonObject();
        data.addProperty("username", username);
        room.broadcast(msg("PLAYER_READY", data));

        if (room.getReadyCount() == 2) {
            room.setState(WordleGameState.PLAYING);
            room.setTargetWord(dictionary.getRandomWord().toUpperCase());
            
            log.info("Game starting in room '{}'. Word is '{}'", room.getRoomId(), room.getTargetWord());
            room.broadcast(msg("GAME_START", null));
        }
    }

    private static void handleGuess(WordleRoom room, String username, JsonObject msg, WordDictionaryService dictionary) {
        if (room.getState() != WordleGameState.PLAYING) return;
        
        WordlePlayer player = room.getPlayer(username);
        if (player.hasWon() || player.hasLost()) return;

        String guess = msg.getAsJsonObject("data").get("word").getAsString().toUpperCase();
        if (!dictionary.isValidWord(guess)) {
            room.sendTo(username, msg("INVALID_WORD", null));
            return;
        }

        player.incrementGuesses();
        String target = room.getTargetWord();
        int[] colors = evaluateGuess(guess, target);

        JsonArray colorArray = new JsonArray();
        boolean allCorrect = true;
        for (int c : colors) {
            colorArray.add(c);
            if (c != 2) allCorrect = false;
        }

        // Send to player
        JsonObject pData = new JsonObject();
        pData.addProperty("word", guess);
        pData.add("colors", colorArray);
        room.sendTo(username, msg("GUESS_RESULT", pData));

        // Send to opponent
        JsonObject oData = new JsonObject();
        oData.add("colors", colorArray);
        oData.addProperty("guessNumber", player.getGuessesUsed());
        WordlePlayer opponent = room.getOpponent(username);
        if (opponent != null) {
            room.sendTo(opponent.getUsername(), msg("OPPONENT_GUESS", oData));
        }

        // Check win/loss
        if (allCorrect) {
            player.setWon(true);
            room.setState(WordleGameState.FINISHED);
            JsonObject winData = new JsonObject();
            winData.addProperty("winner", username);
            winData.addProperty("word", target);
            room.broadcast(msg("GAME_OVER", winData));
        } else if (player.getGuessesUsed() >= 6) {
            player.setLost(true);
            
            if (opponent != null && opponent.hasLost()) {
                room.setState(WordleGameState.FINISHED);
                JsonObject drawData = new JsonObject();
                drawData.addProperty("draw", true);
                drawData.addProperty("word", target);
                room.broadcast(msg("GAME_OVER", drawData));
            }
        }
    }

    private static int[] evaluateGuess(String guess, String target) {
        int[] result = new int[5]; // 0: absent, 1: present, 2: correct
        boolean[] targetUsed = new boolean[5];
        boolean[] guessUsed = new boolean[5];

        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == target.charAt(i)) {
                result[i] = 2;
                targetUsed[i] = true;
                guessUsed[i] = true;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (!guessUsed[i]) {
                for (int j = 0; j < 5; j++) {
                    if (!targetUsed[j] && guess.charAt(i) == target.charAt(j)) {
                        result[i] = 1;
                        targetUsed[j] = true;
                        guessUsed[i] = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private static String msg(String type, JsonObject data) {
        JsonObject root = new JsonObject();
        root.addProperty("type", type);
        if (data != null) root.add("data", data);
        return GSON.toJson(root);
    }
}
