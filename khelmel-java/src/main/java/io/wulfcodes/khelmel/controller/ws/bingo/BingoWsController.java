package io.wulfcodes.khelmel.controller.ws.bingo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import io.wulfcodes.khelmel.model.bingo.BingoPlayer;
import io.wulfcodes.khelmel.model.bingo.BingoRoom;
import io.wulfcodes.khelmel.model.bingo.GameState;
import io.wulfcodes.khelmel.service.bingo.BingoRoomManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class BingoWsController {

    private static final Logger log = LoggerFactory.getLogger(BingoWsController.class);
    private static final Gson GSON = new Gson();
    private static final Random RANDOM = new Random();

    // ── Connect ───────────────────────────────────────────────────────────────

    public static void onConnect(WsConnectContext ctx, BingoRoomManager manager) {
        String roomId   = ctx.pathParam("roomId");
        String username = ctx.pathParam("username");

        log.info("Connection attempt — room='{}' username='{}'", roomId, username);

        BingoRoom room = manager.getOrCreate(roomId);

        if (room.isFull()) {
            log.warn("Room '{}' is full — rejecting '{}'", roomId, username);
            ctx.send(msg("ROOM_FULL", null));
            ctx.session.close();
            return;
        }

        boolean added = room.addPlayer(username, ctx);
        if (!added) {
            log.warn("Username '{}' already taken in room '{}'", username, roomId);
            ctx.send(msg("USERNAME_TAKEN", null));
            ctx.session.close();
            return;
        }

        ctx.attribute("roomId", roomId);
        ctx.attribute("username", username);

        if (room.getPlayerCount() == 1) {
            log.debug("'{}' is first to join room '{}' — waiting for opponent", username, roomId);
            JsonObject data = new JsonObject();
            data.addProperty("username", username);
            data.addProperty("opponentUsername", "Unknown");
            data.addProperty("opponentStatus", "not_connected");
            ctx.send(msg("JOINED", data));
        } else {
            BingoPlayer opponent = room.getOpponent(username);
            log.info("'{}' joined room '{}' — both players present: '{}' vs '{}'", username, roomId, username, opponent.getUsername());

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

    // ── Message ───────────────────────────────────────────────────────────────

    public static void onMessage(WsMessageContext ctx, BingoRoomManager manager) {
        String roomId   = ctx.attribute("roomId");
        String username = ctx.attribute("username");

        if (roomId == null || username == null) {
            log.warn("Received message from unauthenticated session — ignoring");
            return;
        }

        BingoRoom room = manager.get(roomId);
        if (room == null) {
            log.warn("Message from '{}' for unknown room '{}' — ignoring", username, roomId);
            return;
        }

        try {
            JsonObject msg = GSON.fromJson(ctx.message(), JsonObject.class);
            String type = msg.get("type").getAsString();

            log.debug("Message received — room='{}' username='{}' type='{}'", roomId, username, type);

            switch (type) {
                case "PING"  -> ctx.send(msg("PONG", null));
                case "READY" -> handleReady(room, username);
                case "MOVE"  -> handleMove(room, username, msg);
                default      -> log.warn("Unknown message type '{}' from '{}' in room '{}'", type, username, roomId);
            }
        } catch (Exception e) {
            log.error("Error processing message from '{}' in room '{}'", username, roomId, e);
        }
    }

    // ── Close ─────────────────────────────────────────────────────────────────

    public static void onClose(WsCloseContext ctx, BingoRoomManager manager) {
        String roomId   = ctx.attribute("roomId");
        String username = ctx.attribute("username");

        if (roomId == null || username == null) {
            log.debug("Connection closed before authentication — nothing to clean up");
            return;
        }

        log.info("'{}' disconnected from room '{}' (code={})", username, roomId, ctx.status());

        BingoRoom room = manager.get(roomId);
        if (room == null) return;

        room.removePlayer(username);

        JsonObject data = new JsonObject();
        data.addProperty("username", username);
        room.broadcast(msg("PLAYER_DISCONNECTED", data));

        manager.removeIfEmpty(roomId);
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    private static void handleReady(BingoRoom room, String username) {
        BingoPlayer player = room.getPlayer(username);
        if (player == null || room.getState() != GameState.LOBBY) {
            log.warn("Ignoring READY from '{}' — player null or room not in LOBBY state (state={})",
                    username, room.getState());
            return;
        }

        player.setReady(true);
        log.info("'{}' is ready in room '{}' ({}/2 ready)", username, room.getRoomId(), room.getReadyCount());

        JsonObject data = new JsonObject();
        data.addProperty("username", username);
        room.broadcast(msg("PLAYER_READY", data));

        if (room.getReadyCount() == 2) {
            room.setState(GameState.PLAYING);

            List<BingoPlayer> players = room.getPlayers();
            String first = players.get(RANDOM.nextInt(2)).getUsername();
            room.setCurrentTurn(first);

            log.info("Game starting in room '{}' — first turn: '{}'", room.getRoomId(), first);

            JsonObject startData = new JsonObject();
            startData.addProperty("firstPlayer", first);
            room.broadcast(msg("GAME_START", startData));
        }
    }

    private static void handleMove(BingoRoom room, String username, JsonObject msg) {
        if (room.getState() != GameState.PLAYING) {
            log.warn("Ignoring MOVE from '{}' — game not in PLAYING state (state={})", username, room.getState());
            return;
        }
        if (!username.equals(room.getCurrentTurn())) {
            log.warn("Ignoring MOVE from '{}' — not their turn (current='{}')", username, room.getCurrentTurn());
            return;
        }

        int number  = msg.get("number").getAsInt();
        int myBingo = msg.get("myBingo").getAsInt();

        log.debug("MOVE — room='{}' player='{}' number={} bingo={}", room.getRoomId(), username, number, myBingo);

        BingoPlayer opponent = room.getOpponent(username);
        if (opponent == null) return;
        room.setCurrentTurn(opponent.getUsername());

        JsonObject ack = new JsonObject();
        ack.addProperty("number", number);
        ack.addProperty("nextTurn", opponent.getUsername());
        ack.addProperty("moverBingo", myBingo);
        room.sendTo(username, msg("MOVE_ACK", ack));

        JsonObject relay = new JsonObject();
        relay.addProperty("number", number);
        relay.addProperty("nextTurn", opponent.getUsername());
        relay.addProperty("moverBingo", myBingo);
        room.sendTo(opponent.getUsername(), msg("OPPONENT_MOVE", relay));

        if (myBingo >= 5) {
            room.setState(GameState.FINISHED);
            log.info("Game over in room '{}' — winner: '{}'", room.getRoomId(), username);
            JsonObject winData = new JsonObject();
            winData.addProperty("winner", username);
            room.broadcast(msg("GAME_OVER", winData));
        }
    }

    // ── Util ──────────────────────────────────────────────────────────────────

    private static String msg(String type, JsonObject data) {
        JsonObject root = new JsonObject();
        root.addProperty("type", type);
        if (data != null) root.add("data", data);
        return GSON.toJson(root);
    }
}
