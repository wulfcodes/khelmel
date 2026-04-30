const BingoRoom = require('../models/bingo.room');

class BingoRoomManager {
    constructor() {
        this.rooms = new Map();
    }

    getOrCreate(roomId) {
        const existed = this.rooms.has(roomId);
        if (!existed) {
            this.rooms.set(roomId, new BingoRoom(roomId));
            console.log(`Created new room '${roomId}'`);
        } else {
            console.debug(`Fetched existing room '${roomId}'`);
        }
        return this.rooms.get(roomId);
    }

    get(roomId) {
        const room = this.rooms.get(roomId);
        if (!room) {
            console.debug(`Room '${roomId}' not found`);
        }
        return room;
    }

    removeIfEmpty(roomId) {
        const room = this.rooms.get(roomId);
        if (room && room.getPlayerCount() === 0) {
            this.rooms.delete(roomId);
            console.log(`Room '${roomId}' removed (no players remaining)`);
        }
    }
}

module.exports = BingoRoomManager;
