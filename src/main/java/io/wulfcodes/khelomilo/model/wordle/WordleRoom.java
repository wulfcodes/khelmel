package io.wulfcodes.khelomilo.model.wordle;

import io.javalin.websocket.WsContext;

import java.util.ArrayList;
import java.util.List;

public class WordleRoom {

    private final String roomId;
    private final List<WordlePlayer> players = new ArrayList<>(2);
    private WordleGameState state = WordleGameState.WAITING;
    private String targetWord;

    public WordleRoom(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() { return roomId; }
    public WordleGameState getState() { return state; }
    public void setState(WordleGameState state) { this.state = state; }
    
    public String getTargetWord() { return targetWord; }
    public void setTargetWord(String targetWord) { this.targetWord = targetWord; }

    public boolean isFull() { return players.size() >= 2; }
    public int getPlayerCount() { return players.size(); }

    public synchronized boolean addPlayer(String username, WsContext ctx) {
        if (isFull()) return false;
        if (players.stream().anyMatch(p -> p.getUsername().equals(username))) return false;
        players.add(new WordlePlayer(username, ctx));
        if (isFull()) state = WordleGameState.LOBBY;
        return true;
    }

    public synchronized void removePlayer(String username) {
        players.removeIf(p -> p.getUsername().equals(username));
        if (players.size() < 2 && state != WordleGameState.FINISHED) {
            state = WordleGameState.WAITING;
        }
    }

    public WordlePlayer getPlayer(String username) {
        return players.stream()
            .filter(p -> p.getUsername().equals(username))
            .findFirst().orElse(null);
    }

    public WordlePlayer getOpponent(String username) {
        return players.stream()
            .filter(p -> !p.getUsername().equals(username))
            .findFirst().orElse(null);
    }

    public List<WordlePlayer> getPlayers() { return players; }

    public int getReadyCount() {
        return (int) players.stream().filter(WordlePlayer::isReady).count();
    }

    public void broadcast(String message) {
        for (WordlePlayer p : players) {
            try {
                if (p.getSession().session.isOpen()) {
                    p.getSession().send(message);
                }
            } catch (Exception ignored) {}
        }
    }

    public void sendTo(String username, String message) {
        WordlePlayer player = getPlayer(username);
        if (player != null) {
            try {
                if (player.getSession().session.isOpen()) {
                    player.getSession().send(message);
                }
            } catch (Exception ignored) {}
        }
    }
}
