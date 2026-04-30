const BingoPlayer = require('./bingo.player');
const GameState = require('./game.state');

class BingoRoom {
    constructor(roomId) {
        this.roomId = roomId;
        this.players = [];
        this.state = GameState.WAITING;
        this.currentTurn = null; // username
    }

    getRoomId() { return this.roomId; }
    getState() { return this.state; }
    setState(state) { this.state = state; }
    getCurrentTurn() { return this.currentTurn; }
    setCurrentTurn(currentTurn) { this.currentTurn = currentTurn; }

    isFull() { return this.players.length >= 2; }
    getPlayerCount() { return this.players.length; }

    /** Returns true if player was added, false if room is full or username taken. */
    addPlayer(username, ws) {
        if (this.isFull()) return false;
        if (this.players.some(p => p.getUsername() === username)) return false;
        this.players.push(new BingoPlayer(username, ws));
        if (this.isFull()) this.state = GameState.LOBBY;
        return true;
    }

    removePlayer(username) {
        this.players = this.players.filter(p => p.getUsername() !== username);
        if (this.players.length < 2 && this.state !== GameState.FINISHED) {
            this.state = GameState.WAITING;
        }
    }

    getPlayer(username) {
        return this.players.find(p => p.getUsername() === username) || null;
    }

    getOpponent(username) {
        return this.players.find(p => p.getUsername() !== username) || null;
    }

    getPlayers() { return this.players; }

    getReadyCount() {
        return this.players.filter(p => p.isReady()).length;
    }

    broadcast(message) {
        const payload = JSON.stringify(message);
        for (const p of this.players) {
            try {
                if (p.getSession().readyState === 1) { // OPEN
                    p.getSession().send(payload);
                }
            } catch (e) { }
        }
    }

    sendTo(username, message) {
        const player = this.getPlayer(username);
        if (player) {
            try {
                if (player.getSession().readyState === 1) { // OPEN
                    player.getSession().send(JSON.stringify(message));
                }
            } catch (e) { }
        }
    }
}

module.exports = BingoRoom;
