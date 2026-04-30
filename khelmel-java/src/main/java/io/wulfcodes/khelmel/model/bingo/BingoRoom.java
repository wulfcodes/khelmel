package io.wulfcodes.khelmel.model.bingo;

import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.List;

public class BingoRoom {

    private final String roomId;
    private final List<BingoPlayer> players = new ArrayList<>(2);
    private GameState state = GameState.WAITING;
    private String currentTurn; // username of player whose turn it is

    public BingoRoom(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() { return roomId; }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public String getCurrentTurn() { return currentTurn; }
    public void setCurrentTurn(String currentTurn) { this.currentTurn = currentTurn; }

    public boolean isFull() { return players.size() >= 2; }
    public int getPlayerCount() { return players.size(); }

    /** Returns true if player was added, false if room is full or username taken. */
    public synchronized boolean addPlayer(String username, WsContext ctx) {
        if (isFull()) return false;
        if (players.stream().anyMatch(p -> p.getUsername().equals(username))) return false;
        players.add(new BingoPlayer(username, ctx));
        if (isFull()) state = GameState.LOBBY;
        return true;
    }

    public synchronized void removePlayer(String username) {
        players.removeIf(p -> p.getUsername().equals(username));
        if (players.size() < 2 && state != GameState.FINISHED) {
            state = GameState.WAITING;
        }
    }

    public BingoPlayer getPlayer(String username) {
        return players.stream()
            .filter(p -> p.getUsername().equals(username))
            .findFirst().orElse(null);
    }

    public BingoPlayer getOpponent(String username) {
        return players.stream()
            .filter(p -> !p.getUsername().equals(username))
            .findFirst().orElse(null);
    }

    public List<BingoPlayer> getPlayers() { return players; }

    public int getReadyCount() {
        return (int) players.stream().filter(BingoPlayer::isReady).count();
    }

    /** Broadcast a JSON string to all players in the room. */
    public void broadcast(String message) {
        for (BingoPlayer p : players) {
            try {
                if (p.getSession().session.isOpen()) {
                    p.getSession().send(message);
                }
            } catch (Exception ignored) {}
        }
    }

    /** Send a JSON string to a specific player. */
    public void sendTo(String username, String message) {
        BingoPlayer player = getPlayer(username);
        if (player != null) {
            try {
                if (player.getSession().session.isOpen()) {
                    player.getSession().send(message);
                }
            } catch (Exception ignored) {}
        }
    }
}
