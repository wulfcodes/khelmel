const GameState = require('../models/game.state');

class BingoWsController {
    static msg(type, data) {
        return { type, data };
    }

    static onConnect(ws, req, manager) {
        const roomId = req.params.roomId;
        const username = req.params.username;

        console.log(`Connection attempt — room='${roomId}' username='${username}'`);

        const room = manager.getOrCreate(roomId);

        if (room.isFull()) {
            console.warn(`Room '${roomId}' is full — rejecting '${username}'`);
            ws.send(JSON.stringify(this.msg('ROOM_FULL', null)));
            ws.close();
            return;
        }

        const added = room.addPlayer(username, ws);
        if (!added) {
            console.warn(`Username '${username}' already taken in room '${roomId}'`);
            ws.send(JSON.stringify(this.msg('USERNAME_TAKEN', null)));
            ws.close();
            return;
        }

        // Attach properties directly to ws for easy access later
        ws.roomId = roomId;
        ws.username = username;

        if (room.getPlayerCount() === 1) {
            console.debug(`'${username}' is first to join room '${roomId}' — waiting for opponent`);
            ws.send(JSON.stringify(this.msg('JOINED', {
                username: username,
                opponentUsername: "Unknown",
                opponentStatus: "not_connected"
            })));
        } else {
            const opponent = room.getOpponent(username);
            console.log(`'${username}' joined room '${roomId}' — both players present: '${username}' vs '${opponent.getUsername()}'`);

            ws.send(JSON.stringify(this.msg('JOINED', {
                username: username,
                opponentUsername: opponent.getUsername(),
                opponentStatus: "waiting"
            })));

            room.sendTo(opponent.getUsername(), this.msg('PLAYER_JOINED', {
                opponentUsername: username,
                opponentStatus: "waiting"
            }));
        }
    }

    static onMessage(ws, messageStr, manager) {
        const roomId = ws.roomId;
        const username = ws.username;

        if (!roomId || !username) {
            console.warn("Received message from unauthenticated session — ignoring");
            return;
        }

        const room = manager.get(roomId);
        if (!room) {
            console.warn(`Message from '${username}' for unknown room '${roomId}' — ignoring`);
            return;
        }

        try {
            const msg = JSON.parse(messageStr);
            const type = msg.type;

            console.debug(`Message received — room='${roomId}' username='${username}' type='${type}'`);

            switch (type) {
                case "PING":
                    break; // heartbeat — keep alive, no logging needed
                case "READY":
                    this.handleReady(room, username);
                    break;
                case "MOVE":
                    this.handleMove(room, username, msg);
                    break;
                default:
                    console.warn(`Unknown message type '${type}' from '${username}' in room '${roomId}'`);
            }
        } catch (e) {
            console.error(`Error processing message from '${username}' in room '${roomId}'`, e);
        }
    }

    static onClose(ws, manager) {
        const roomId = ws.roomId;
        const username = ws.username;

        if (!roomId || !username) {
            console.debug("Connection closed before authentication — nothing to clean up");
            return;
        }

        // ws.close() in express-ws passes a code, or we can just default if missing
        console.log(`'${username}' disconnected from room '${roomId}'`);

        const room = manager.get(roomId);
        if (!room) return;

        room.removePlayer(username);

        room.broadcast(this.msg("PLAYER_DISCONNECTED", { username: username }));

        manager.removeIfEmpty(roomId);
    }

    static handleReady(room, username) {
        const player = room.getPlayer(username);
        if (!player || room.getState() !== GameState.LOBBY) {
            console.warn(`Ignoring READY from '${username}' — player null or room not in LOBBY state (state=${room.getState()})`);
            return;
        }

        player.setReady(true);
        console.log(`'${username}' is ready in room '${room.getRoomId()}' (${room.getReadyCount()}/2 ready)`);

        room.broadcast(this.msg("PLAYER_READY", { username: username }));

        if (room.getReadyCount() === 2) {
            room.setState(GameState.PLAYING);

            const players = room.getPlayers();
            const first = players[Math.floor(Math.random() * 2)].getUsername();
            room.setCurrentTurn(first);

            console.log(`Game starting in room '${room.getRoomId()}' — first turn: '${first}'`);

            room.broadcast(this.msg("GAME_START", { firstPlayer: first }));
        }
    }

    static handleMove(room, username, msg) {
        if (room.getState() !== GameState.PLAYING) {
            console.warn(`Ignoring MOVE from '${username}' — game not in PLAYING state (state=${room.getState()})`);
            return;
        }
        if (username !== room.getCurrentTurn()) {
            console.warn(`Ignoring MOVE from '${username}' — not their turn (current='${room.getCurrentTurn()}')`);
            return;
        }

        const number = msg.number;
        const myBingo = msg.myBingo;

        console.debug(`MOVE — room='${room.getRoomId()}' player='${username}' number=${number} bingo=${myBingo}`);

        const opponent = room.getOpponent(username);
        if (!opponent) return;
        room.setCurrentTurn(opponent.getUsername());

        room.sendTo(username, this.msg("MOVE_ACK", {
            number: number,
            nextTurn: opponent.getUsername(),
            moverBingo: myBingo
        }));

        room.sendTo(opponent.getUsername(), this.msg("OPPONENT_MOVE", {
            number: number,
            nextTurn: opponent.getUsername(),
            moverBingo: myBingo
        }));

        if (myBingo >= 5) {
            room.setState(GameState.FINISHED);
            console.log(`Game over in room '${room.getRoomId()}' — winner: '${username}'`);
            room.broadcast(this.msg("GAME_OVER", { winner: username }));
        }
    }
}

module.exports = BingoWsController;
