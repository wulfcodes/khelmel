const express = require('express');
const app = express();
const expressWs = require('express-ws')(app);
const path = require('path');

console.log("Starting Khelmel application...");

// Models & Services
const BingoRoomManager = require('./src/services/bingo.room.manager');
app.locals.roomManager = new BingoRoomManager();

// Setup EJS
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Static files
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.json());

// Routers
const homeRouter = require('./src/routers/home.router');
const bingoWebRouter = require('./src/routers/bingo.web.router');
const bingoWsRouter = require('./src/routers/bingo.ws.router');

app.use('/', homeRouter);
app.use('/bingo', bingoWebRouter);
bingoWsRouter(app);

const PORT = process.env.PORT;
if (!PORT) {
    console.error("Environment variable PORT is not set. Aborting startup.");
    process.exit(1);
}

app.listen(PORT, () => {
    console.log(`Khelmel started successfully on port ${PORT}`);
});
