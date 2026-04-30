package io.wulfcodes.khelmel.model.bingo;

import io.javalin.websocket.WsContext;

public class BingoPlayer {
    private final String username;
    private final WsContext session;
    private boolean ready;

    public BingoPlayer(String username, WsContext session) {
        this.username = username;
        this.session = session;
        this.ready = false;
    }

    public String getUsername() { return username; }
    public WsContext getSession() { return session; }
    public boolean isReady() { return ready; }
    public void setReady(boolean ready) { this.ready = ready; }
}
