package io.wulfcodes.khelmel.model.bingo;

public enum GameState {
    WAITING,    // < 2 players connected
    LOBBY,      // 2 players connected, not both ready
    COUNTDOWN,  // both ready, countdown in progress
    PLAYING,    // game in progress
    FINISHED    // game over
}
