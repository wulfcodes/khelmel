// ── State ─────────────────────────────────────────────────────────────────────
const S = {
  cells: Array(25).fill(null),
  counter: 1,
  phase: 'SETUP',
  ws: null,
  heartbeat: null,
  me: '',
  crossed: new Set(),
  myBingo: 0,
  oppBingo: 0,
  myTurn: false,
  bothConnected: false,
};

// ── Grid init ─────────────────────────────────────────────────────────────────
const grid = document.getElementById('grid');
for (let i = 0; i < 25; i++) {
  const cell = document.createElement('div');
  cell.className = 'cell';
  cell.dataset.idx = i;
  cell.addEventListener('click', () => handleCellClick(i));
  grid.appendChild(cell);
}

function cellEl(i) { return grid.children[i]; }

function renderGrid() {
  for (let i = 0; i < 25; i++) {
    const el = cellEl(i);
    const num = S.cells[i];
    if (num !== null && S.crossed.has(num)) {
      el.className = 'cell crossed filled';
      el.innerHTML = '<span>' + num + '</span><span class="x-mark">✕</span>';
    } else if (num !== null) {
      el.className = 'cell filled';
      el.textContent = num;
    } else {
      el.className = 'cell';
      el.textContent = '';
    }
  }
}

// ── Setup ─────────────────────────────────────────────────────────────────────
function handleCellClick(i) {
  if (S.phase === 'SETUP') {
    if (S.cells[i] !== null || S.counter > 25) return;
    S.cells[i] = S.counter++;
    renderGrid();
    checkEnterVisibility();
  } else if (S.phase === 'PLAYING' && S.myTurn) {
    const num = S.cells[i];
    if (num === null || S.crossed.has(num)) return;
    makeMove(num);
  }
}

document.getElementById('btn-reset').addEventListener('click', () => {
  S.cells = Array(25).fill(null);
  S.counter = 1;
  renderGrid();
  checkEnterVisibility();
});

const inpUser = document.getElementById('inp-user');
const inpRoom = document.getElementById('inp-room');
const btnEnter = document.getElementById('btn-enter');

function checkEnterVisibility() {
  const full = S.cells.every(c => c !== null);
  btnEnter.disabled = !(full && inpRoom.value.trim() && inpUser.value.trim());
}
inpUser.addEventListener('input', checkEnterVisibility);
inpRoom.addEventListener('input', checkEnterVisibility);

// ── Enter room ────────────────────────────────────────────────────────────────
function enterRoom() {
  S.me = inpUser.value.trim();
  const roomId = inpRoom.value.trim();
  S.ws = new WebSocket('ws://' + location.host + '/ws/bingo/' + encodeURIComponent(roomId) + '/' + encodeURIComponent(S.me));
  S.ws.onopen = function() {
    console.log('WS connected');
    S.heartbeat = setInterval(function() {
      if (S.ws && S.ws.readyState === WebSocket.OPEN) {
        S.ws.send(JSON.stringify({ type: 'PING' }));
      }
    }, 20000);
  };
  S.ws.onmessage = function(e) { handleMessage(JSON.parse(e.data)); };
  S.ws.onclose = function() {
    if (S.heartbeat) { clearInterval(S.heartbeat); S.heartbeat = null; }
    if (S.phase !== 'DONE') showDisconnect();
  };
}

// ── WebSocket messages ────────────────────────────────────────────────────────
function handleMessage(msg) {
  const d = msg.data || {};
  switch (msg.type) {
    case 'ROOM_FULL':     alert('Room is full! Try a different room code.'); break;
    case 'USERNAME_TAKEN': alert('That username is already taken in this room.'); break;

    case 'JOINED':
      switchToGameMode();
      document.getElementById('me-name').textContent = d.username + ' (You)';
      document.getElementById('opp-name').textContent = d.opponentUsername;
      document.getElementById('opp-status').textContent = d.opponentStatus === 'waiting' ? 'Waiting\u2026' : 'Not connected';
      if (d.opponentStatus === 'waiting') { S.bothConnected = true; showReadyBtn(); }
      break;

    case 'PLAYER_JOINED':
      document.getElementById('opp-name').textContent = d.opponentUsername;
      document.getElementById('opp-status').textContent = 'Waiting\u2026';
      S.bothConnected = true;
      showReadyBtn();
      break;

    case 'PLAYER_READY': {
      const isMe = d.username === S.me;
      const card = document.getElementById(isMe ? 'me-card' : 'opp-card');
      const stat = document.getElementById(isMe ? 'me-status' : 'opp-status');
      card.classList.add('ready');
      stat.textContent = 'Ready \u2713';
      break;
    }

    case 'GAME_START': startCountdown(d.firstPlayer); break;

    case 'MOVE_ACK':
      applyCross(d.number, d.moverBingo);
      S.myTurn = false;
      updateTurnBanner(d.nextTurn);
      break;

    case 'OPPONENT_MOVE':
      S.crossed.add(d.number);
      S.oppBingo = d.moverBingo;
      S.myBingo = countBingo(S.crossed);
      updateBingoPanel(S.myBingo);
      renderGrid();
      S.myTurn = d.nextTurn === S.me;
      updateTurnBanner(d.nextTurn);
      break;

    case 'GAME_OVER': showResult(d.winner); break;
    case 'PLAYER_DISCONNECTED': showDisconnect(d.username); break;
  }
}

// ── Game mode switch ──────────────────────────────────────────────────────────
function switchToGameMode() {
  S.phase = 'LOBBY';
  document.getElementById('bingo-page').classList.add('game-mode');
  document.getElementById('turn-banner').style.display = 'none';
  renderGrid();
}

function showReadyBtn() {
  document.getElementById('btn-ready').style.display = '';
}

function sendReady() {
  document.getElementById('btn-ready').style.display = 'none';
  document.getElementById('me-status').textContent = 'Ready \u2713';
  document.getElementById('me-card').classList.add('ready');
  S.ws.send(JSON.stringify({ type: 'READY' }));
}

// ── Countdown ─────────────────────────────────────────────────────────────────
function startCountdown(firstPlayer) {
  const overlay = document.getElementById('countdown-overlay');
  const numEl = document.getElementById('countdown-num');
  overlay.classList.add('show');
  let n = 3;
  numEl.textContent = n;
  const iv = setInterval(function() {
    n--;
    if (n <= 0) {
      clearInterval(iv);
      overlay.classList.remove('show');
      beginGame(firstPlayer);
    } else {
      numEl.textContent = n;
      numEl.style.animation = 'none';
      void numEl.offsetWidth;
      numEl.style.animation = '';
    }
  }, 950);
}

function beginGame(firstPlayer) {
  S.phase = 'PLAYING';
  S.myTurn = firstPlayer === S.me;
  document.getElementById('turn-banner').style.display = '';
  updateTurnBanner(firstPlayer);
  document.getElementById('btn-ready').style.display = 'none';
}

function updateTurnBanner(turnPlayer) {
  const banner = document.getElementById('turn-banner');
  if (turnPlayer === S.me) {
    banner.textContent = '\uD83C\uDFAF Your turn \u2014 pick a number!';
    banner.style.borderColor = 'rgba(251,191,36,.5)';
    banner.style.color = '#fbbf24';
  } else {
    banner.textContent = '\u23F3 ' + turnPlayer + '\'s turn\u2026';
    banner.style.borderColor = 'rgba(124,58,237,.3)';
    banner.style.color = 'var(--primary-l)';
  }
}

// ── Move ──────────────────────────────────────────────────────────────────────
function makeMove(number) {
  S.crossed.add(number);
  S.myBingo = countBingo(S.crossed);
  renderGrid();
  updateBingoPanel(S.myBingo);
  S.ws.send(JSON.stringify({ type: 'MOVE', number: number, myBingo: S.myBingo }));
}

function applyCross(number, myBingo) {
  S.crossed.add(number);
  S.myBingo = myBingo;
  updateBingoPanel(S.myBingo);
  renderGrid();
}

// ── BINGO counting ────────────────────────────────────────────────────────────
function countBingo(crossed) {
  let lines = 0;
  for (let r = 0; r < 5; r++) {
    if ([0,1,2,3,4].every(function(c) { return crossed.has(S.cells[r*5+c]); })) lines++;
  }
  for (let c = 0; c < 5; c++) {
    if ([0,1,2,3,4].every(function(r) { return crossed.has(S.cells[r*5+c]); })) lines++;
  }
  if ([0,1,2,3,4].every(function(i) { return crossed.has(S.cells[i*5+i]); })) lines++;
  if ([0,1,2,3,4].every(function(i) { return crossed.has(S.cells[i*5+(4-i)]); })) lines++;
  return Math.min(lines, 5);
}

function updateBingoPanel(count) {
  for (let i = 0; i < 5; i++) {
    document.getElementById('bl-' + i).classList.toggle('lit', i < count);
  }
}

// ── Result ────────────────────────────────────────────────────────────────────
function showResult(winner) {
  S.phase = 'DONE';
  if (S.heartbeat) { clearInterval(S.heartbeat); S.heartbeat = null; }
  if (S.ws) S.ws.close();
  document.getElementById('result-title').textContent = winner === S.me ? '\uD83C\uDF89 You Win!' : '\uD83D\uDE14 ' + winner + ' Wins';
  document.getElementById('result-sub').textContent = winner === S.me ? 'Congratulations!' : 'Better luck next time!';
  const row = document.getElementById('boards-row');
  row.innerHTML = '';
  row.appendChild(makeMiniBoard('Your Board', S.cells, S.crossed));
  document.getElementById('result-overlay').classList.add('show');
}

function makeMiniBoard(label, cells, crossed) {
  const wrap = document.createElement('div');
  wrap.className = 'mini-board-wrap';
  const lbl = document.createElement('span');
  lbl.textContent = label;
  const g = document.createElement('div');
  g.className = 'mini-grid';
  cells.forEach(function(num) {
    const c = document.createElement('div');
    c.className = 'mini-cell' + (crossed.has(num) ? ' mini-crossed' : '');
    c.textContent = num != null ? num : '';
    g.appendChild(c);
  });
  wrap.appendChild(lbl);
  wrap.appendChild(g);
  return wrap;
}

function showDisconnect(username) {
  if (S.phase === 'DONE') return;
  alert((username || 'Opponent') + ' disconnected. Game over.');
  location.href = '/bingo';
}
