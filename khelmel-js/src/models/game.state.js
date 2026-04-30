const GameState = {
    WAITING: 'WAITING',     // < 2 players connected
    LOBBY: 'LOBBY',       // 2 players connected, not both ready
    COUNTDOWN: 'COUNTDOWN',   // both ready, countdown in progress
    PLAYING: 'PLAYING',     // game in progress
    FINISHED: 'FINISHED'     // game over
};

module.exports = GameState;
