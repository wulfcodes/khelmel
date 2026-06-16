package io.wulfcodes.khelomilo.model.wordle;

import io.javalin.websocket.WsContext;

public class WordlePlayer {
    private final String username;
    private final WsContext session;
    private boolean ready;
    private int guessesUsed;
    private boolean hasWon;
    private boolean hasLost;

    public WordlePlayer(String username, WsContext session) {
        this.username = username;
        this.session = session;
        this.ready = false;
        this.guessesUsed = 0;
        this.hasWon = false;
        this.hasLost = false;
    }

    public String getUsername() { return username; }
    public WsContext getSession() { return session; }
    
    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }
    
    public int getGuessesUsed() { return guessesUsed; }
    public void incrementGuesses() { this.guessesUsed++; }
    
    public boolean hasWon() { return hasWon; }
    public void setWon(boolean won) { this.hasWon = won; }
    
    public boolean hasLost() { return hasLost; }
    public void setLost(boolean lost) { this.hasLost = lost; }
}
