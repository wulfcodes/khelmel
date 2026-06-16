let ws = null;
let username = "";
let roomId = "";

let currentRow = 0;
let currentCol = 0;
let currentGuess = "";
let isPlaying = false;

// DOM Elements
const setupPanel = document.getElementById('setupPanel');
const gameArea = document.getElementById('gameArea');
const joinBtn = document.getElementById('joinBtn');
const readyBtn = document.getElementById('readyBtn');

const myNameEl = document.getElementById('myName');
const opponentNameEl = document.getElementById('opponentName');
const opponentStatusEl = document.getElementById('opponentStatus');

const playerGrid = document.getElementById('playerGrid');
const opponentGrid = document.getElementById('opponentGrid');
const keyboard = document.getElementById('keyboard');

const toast = document.getElementById('toast');
const gameOverOverlay = document.getElementById('gameOverOverlay');
const gameOverTitle = document.getElementById('gameOverTitle');
const gameOverWord = document.getElementById('gameOverWord');

// Pre-fill if url has hash
if (window.location.hash) {
    document.getElementById('roomIdInput').value = window.location.hash.substring(1);
}

// Generate grids
function generateGrid(container) {
    container.innerHTML = '';
    for (let i = 0; i < 6; i++) {
        const row = document.createElement('div');
        row.className = 'wordle-row';
        for (let j = 0; j < 5; j++) {
            const cell = document.createElement('div');
            cell.className = 'wordle-cell';
            row.appendChild(cell);
        }
        container.appendChild(row);
    }
}

generateGrid(playerGrid);
generateGrid(opponentGrid);

// Setup
joinBtn.addEventListener('click', () => {
    username = document.getElementById('usernameInput').value.trim();
    roomId = document.getElementById('roomIdInput').value.trim().toUpperCase();

    if (!username || !roomId) {
        showToast("Enter username and room ID");
        return;
    }

    connectWs();
});

function connectWs() {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${protocol}//${window.location.host}/ws/wordle/${roomId}/${username}`;
    
    ws = new WebSocket(wsUrl);

    ws.onopen = () => {
        window.location.hash = roomId;
        setupPanel.style.display = 'none';
        gameArea.style.display = 'flex';
        myNameEl.textContent = username;
    };

    ws.onmessage = (event) => {
        const msg = JSON.parse(event.data);
        handleMessage(msg);
    };

    ws.onclose = () => {
        showToast("Disconnected from server");
        setTimeout(() => location.reload(), 2000);
    };
}

function handleMessage(msg) {
    const { type, data } = msg;

    switch(type) {
        case 'ROOM_FULL':
            showToast("Room is full!");
            location.reload();
            break;
        case 'USERNAME_TAKEN':
            showToast("Username taken in this room!");
            location.reload();
            break;
        case 'JOINED':
            updateOpponent(data.opponentUsername, data.opponentStatus);
            break;
        case 'PLAYER_JOINED':
            updateOpponent(data.opponentUsername, data.opponentStatus);
            showToast(`${data.opponentUsername} joined the room`);
            break;
        case 'PLAYER_READY':
            if (data.username === username) {
                readyBtn.style.display = 'none';
                showToast("Waiting for opponent to ready up...");
            } else {
                updateOpponent(data.username, "ready");
                showToast(`${data.username} is ready!`);
            }
            break;
        case 'GAME_START':
            isPlaying = true;
            updateOpponent(opponentNameEl.textContent, "playing");
            showToast("Game Started! Guess the word!");
            break;
        case 'INVALID_WORD':
            showToast("Not in word list!");
            shakeRow(playerGrid, currentRow);
            break;
        case 'GUESS_RESULT':
            applyGuessResult(data.word, data.colors);
            break;
        case 'OPPONENT_GUESS':
            applyOpponentGuessResult(data.colors, data.guessNumber - 1);
            break;
        case 'GAME_OVER':
            isPlaying = false;
            showGameOver(data);
            break;
        case 'PLAYER_DISCONNECTED':
            updateOpponent("Waiting...", "disconnected");
            showToast(`${data.username} disconnected!`);
            isPlaying = false;
            break;
    }
}

function updateOpponent(name, status) {
    opponentNameEl.textContent = name;
    opponentStatusEl.textContent = status.toUpperCase();
    opponentStatusEl.className = 'status-badge';
    if (status === 'ready' || status === 'playing') opponentStatusEl.classList.add('status-playing');
    else if (status === 'disconnected') opponentStatusEl.classList.add('status-disconnected');
    else opponentStatusEl.classList.add('status-waiting');
}

readyBtn.addEventListener('click', () => {
    if (ws && ws.readyState === WebSocket.OPEN) {
        ws.send(JSON.stringify({ type: 'READY' }));
    }
});

// Game Logic
document.addEventListener('keydown', (e) => {
    if (!isPlaying) return;
    
    if (e.key === 'Enter') {
        submitGuess();
    } else if (e.key === 'Backspace') {
        removeLetter();
    } else if (/^[a-zA-Z]$/.test(e.key)) {
        addLetter(e.key.toUpperCase());
    }
});

// On-screen keyboard
document.querySelectorAll('.key').forEach(button => {
    button.addEventListener('click', (e) => {
        if (!isPlaying) return;
        const key = e.target.getAttribute('data-key');
        if (key === 'ENTER') submitGuess();
        else if (key === 'BACKSPACE') removeLetter();
        else addLetter(key);
    });
});

function addLetter(letter) {
    if (currentCol < 5 && currentRow < 6) {
        const row = playerGrid.children[currentRow];
        const cell = row.children[currentCol];
        cell.textContent = letter;
        currentGuess += letter;
        currentCol++;
    }
}

function removeLetter() {
    if (currentCol > 0) {
        currentCol--;
        const row = playerGrid.children[currentRow];
        const cell = row.children[currentCol];
        cell.textContent = '';
        currentGuess = currentGuess.slice(0, -1);
    }
}

function submitGuess() {
    if (currentGuess.length !== 5) {
        showToast("Not enough letters");
        shakeRow(playerGrid, currentRow);
        return;
    }
    ws.send(JSON.stringify({ type: 'GUESS', data: { word: currentGuess } }));
}

function applyGuessResult(word, colors) {
    const row = playerGrid.children[currentRow];
    for (let i = 0; i < 5; i++) {
        const cell = row.children[i];
        const colorClass = `cell-state-${colors[i]}`;
        cell.classList.add(colorClass);
        updateKeyboard(word[i], colors[i]);
    }
    currentRow++;
    currentCol = 0;
    currentGuess = "";
}

function applyOpponentGuessResult(colors, r) {
    const row = opponentGrid.children[r];
    for (let i = 0; i < 5; i++) {
        const cell = row.children[i];
        cell.classList.add(`cell-state-${colors[i]}`);
    }
}

function updateKeyboard(letter, colorState) {
    const keyBtn = document.querySelector(`.key[data-key="${letter}"]`);
    if (!keyBtn) return;
    
    // state 2 (green) > state 1 (yellow) > state 0 (grey)
    const currentClass = keyBtn.className;
    if (currentClass.includes('key-state-2')) return; // Already green
    if (currentClass.includes('key-state-1') && colorState === 0) return; // Don't downgrade yellow to grey

    keyBtn.classList.remove('key-state-0', 'key-state-1', 'key-state-2');
    keyBtn.classList.add(`key-state-${colorState}`);
}

function shakeRow(grid, r) {
    const row = grid.children[r];
    row.style.transform = 'translateX(-5px)';
    setTimeout(() => row.style.transform = 'translateX(5px)', 50);
    setTimeout(() => row.style.transform = 'translateX(-5px)', 100);
    setTimeout(() => row.style.transform = 'translateX(5px)', 150);
    setTimeout(() => row.style.transform = 'translateX(0)', 200);
}

function showToast(msg) {
    toast.textContent = msg;
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 3000);
}

function showGameOver(data) {
    gameOverOverlay.classList.add('show');
    gameOverWord.textContent = `Target word was: ${data.word}`;
    
    if (data.draw) {
        gameOverTitle.textContent = "IT'S A DRAW!";
        gameOverTitle.className = 'game-over-title';
    } else if (data.winner === username) {
        gameOverTitle.textContent = "YOU WIN!";
        gameOverTitle.className = 'game-over-title win';
    } else {
        gameOverTitle.textContent = "YOU LOSE!";
        gameOverTitle.className = 'game-over-title lose';
    }
}
