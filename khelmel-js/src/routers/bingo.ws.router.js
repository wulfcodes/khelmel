const BingoWsController = require('../sockets/bingo.socket');

module.exports = function(app) {
    app.ws('/ws/bingo/:roomId/:username', (ws, req) => {
        const manager = req.app.locals.roomManager;
        BingoWsController.onConnect(ws, req, manager);

        ws.on('message', (msg) => {
            BingoWsController.onMessage(ws, msg, manager);
        });

        ws.on('close', () => {
            BingoWsController.onClose(ws, manager);
        });
    });
};
