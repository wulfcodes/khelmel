package io.wulfcodes.khelomilo.model.wordle;

public enum WordleGameState {
    WAITING,    // < 2 players connected
    LOBBY,      // 2 players connected, not both ready
    PLAYING,    // game in progress
    FINISHED    // game over
}
