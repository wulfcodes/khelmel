class BingoPlayer {
    constructor(username, session) {
        this.username = username;
        this.session = session;
        this.ready = false;
    }

    getUsername() { return this.username; }
    getSession() { return this.session; }
    isReady() { return this.ready; }
    setReady(ready) { this.ready = ready; }
}

module.exports = BingoPlayer;
