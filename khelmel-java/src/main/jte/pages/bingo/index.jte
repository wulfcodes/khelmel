@param String title
@template.layout.main(title = title, content = @`
<style>
:root{--primary:#7c3aed;--primary-l:#9d5af5;--accent:#fbbf24;--bg:#07080f;--bg-card:#0f1120;--border:rgba(255,255,255,0.08);--muted:#64748b;--text:#f1f5f9;--radius:14px;}
*,*::before,*::after{box-sizing:border-box;margin:0;padding:0;}
.bingo-page{display:flex;flex-direction:column;align-items:center;justify-content:center;padding:2rem 1rem 4rem;gap:1.5rem;min-height:calc(100vh - 64px);}
#turn-banner{font-size:1rem;font-weight:700;letter-spacing:.05em;text-transform:uppercase;padding:.6rem 1.8rem;border-radius:9999px;background:rgba(124,58,237,.15);border:1px solid rgba(124,58,237,.3);color:var(--primary-l);display:none;}
.game-layout{display:flex;flex-direction:column;align-items:center;gap:2rem;width:100%;max-width:900px;}
.game-mode .game-layout{display:grid;grid-template-columns:220px 1fr 80px;align-items:start;}
.players-panel{background:var(--bg-card);border:1px solid var(--border);border-radius:var(--radius);padding:1.25rem;display:flex;flex-direction:column;gap:.75rem;}
.players-panel h3{font-size:.7rem;font-weight:700;letter-spacing:.1em;text-transform:uppercase;color:var(--muted);margin-bottom:.25rem;}
.player-card{display:flex;flex-direction:column;gap:.2rem;padding:.75rem;border-radius:10px;background:rgba(255,255,255,.03);border:1px solid var(--border);}
.player-card .pname{font-weight:700;font-size:.95rem;}
.player-card .pstatus{font-size:.75rem;color:var(--muted);}
.player-card.ready .pstatus{color:#4ade80;}
.player-card.me{border-color:rgba(124,58,237,.35);}
.board-col{display:flex;flex-direction:column;align-items:center;gap:1rem;}
.grid{display:grid;grid-template-columns:repeat(5,1fr);gap:.5rem;width:100%;max-width:550px;}
.cell{aspect-ratio:1;border-radius:12px;border:1px solid var(--border);background:var(--bg-card);display:flex;align-items:center;justify-content:center;font-size:1.35rem;font-weight:700;cursor:pointer;position:relative;overflow:hidden;transition:transform .15s,border-color .15s,box-shadow .15s;}
.cell:not(.filled):not(.crossed):hover{transform:scale(1.05);border-color:rgba(124,58,237,.5);box-shadow:0 0 16px rgba(124,58,237,.25);}
.cell:not(.filled):not(.crossed){animation:cellPulse 3s ease-in-out infinite;}
.cell.filled{cursor:default;border-color:rgba(124,58,237,.3);color:var(--text);}
.cell.crossed{cursor:default;color:rgba(255,255,255,.25);position:relative;}
.cell.crossed .x-mark{position:absolute;font-size:1.4rem;color:rgba(239,68,68,.8);font-weight:900;z-index:1;}
@keyframes cellPulse{0%,100%{box-shadow:0 0 0 0 rgba(124,58,237,0)}50%{box-shadow:0 0 8px 1px rgba(124,58,237,.15)}}
.board-controls{display:flex;gap:.75rem;flex-wrap:wrap;justify-content:center;}
.btn{padding:.65rem 1.5rem;border-radius:9999px;font-family:inherit;font-size:.875rem;font-weight:700;cursor:pointer;border:none;transition:all .2s;letter-spacing:.03em;}
.btn-primary{background:linear-gradient(135deg,var(--primary),#5b21b6);color:#fff;box-shadow:0 4px 15px rgba(124,58,237,.35);}
.btn-primary:hover:not(:disabled){transform:translateY(-2px);box-shadow:0 8px 20px rgba(124,58,237,.45);}
.btn-primary:disabled{opacity:.35;cursor:not-allowed;filter:grayscale(.4);}
.btn-danger{background:transparent;border:1px solid rgba(239,68,68,.3);color:rgba(239,68,68,.7);}
.btn-danger:hover{background:rgba(239,68,68,.1);border-color:rgba(239,68,68,.6);color:#f87171;}
.input-row{display:flex;gap:.75rem;flex-wrap:wrap;justify-content:center;width:100%;}
.input-wrap{display:flex;flex-direction:column;gap:.35rem;}
.input-wrap label{font-size:.7rem;font-weight:600;letter-spacing:.08em;text-transform:uppercase;color:var(--muted);}
.inp{background:var(--bg-card);border:1px solid var(--border);border-radius:10px;padding:.6rem 1rem;color:var(--text);font-family:inherit;font-size:.9rem;outline:none;transition:border-color .2s;width:170px;}
.inp:focus{border-color:var(--primary);}
.bingo-panel{display:flex;flex-direction:column;gap:.5rem;align-items:center;padding-top:.5rem;}
.bingo-letter{width:52px;height:52px;border-radius:10px;border:2px solid var(--border);display:flex;align-items:center;justify-content:center;font-size:1.4rem;font-weight:900;color:var(--muted);background:var(--bg-card);transition:all .4s;}
.bingo-letter.lit{border-color:var(--accent);color:var(--accent);background:rgba(251,191,36,.1);box-shadow:0 0 16px rgba(251,191,36,.3);text-decoration:line-through;}
#countdown-overlay{position:fixed;inset:0;background:rgba(7,8,15,.85);backdrop-filter:blur(8px);display:none;align-items:center;justify-content:center;z-index:200;flex-direction:column;gap:1rem;}
#countdown-overlay.show{display:flex;}
.countdown-num{font-size:10rem;font-weight:900;line-height:1;background:linear-gradient(135deg,var(--primary-l),var(--accent));-webkit-background-clip:text;-webkit-text-fill-color:transparent;background-clip:text;}
#result-overlay{position:fixed;inset:0;background:rgba(7,8,15,.9);backdrop-filter:blur(12px);display:none;align-items:center;justify-content:center;z-index:200;flex-direction:column;gap:1.5rem;}
#result-overlay.show{display:flex;}
.result-box{text-align:center;display:flex;flex-direction:column;align-items:center;gap:1rem;}
.result-title{font-size:3rem;font-weight:900;background:linear-gradient(135deg,var(--primary-l),var(--accent));-webkit-background-clip:text;-webkit-text-fill-color:transparent;background-clip:text;}
.result-sub{color:var(--muted);font-size:1rem;}
.boards-row{display:flex;gap:2rem;flex-wrap:wrap;justify-content:center;}
.mini-board-wrap{display:flex;flex-direction:column;align-items:center;gap:.5rem;}
.mini-board-wrap span{font-size:.8rem;font-weight:700;color:var(--muted);}
.mini-grid{display:grid;grid-template-columns:repeat(5,1fr);gap:3px;width:200px;}
.mini-cell{aspect-ratio:1;border-radius:5px;background:var(--bg-card);border:1px solid var(--border);display:flex;align-items:center;justify-content:center;font-size:.65rem;font-weight:700;color:var(--muted);}
.mini-cell.mini-crossed{background:rgba(239,68,68,.15);border-color:rgba(239,68,68,.3);color:rgba(239,68,68,.8);text-decoration:line-through;}
.setup-only{display:flex;}
.game-only{display:none;}
.game-mode .setup-only{display:none !important;}
.game-mode .game-only{display:flex;}

/* ── Mobile ── */
@media(max-width:768px){
  .bingo-page{padding:1.25rem .75rem 3rem;}
  .game-mode .game-layout{display:flex;flex-direction:column;align-items:center;}
  .players-panel{flex-direction:row;gap:.5rem;width:100%;max-width:550px;}
  .player-card{flex:1;}
  .grid{max-width:92vw;gap:.35rem;}
  .cell{font-size:1.1rem;border-radius:8px;}
  .bingo-panel{flex-direction:row;gap:.4rem;padding-top:0;}
  .bingo-letter{width:42px;height:42px;font-size:1.1rem;}
  .input-row{flex-direction:column;align-items:center;}
  .inp{width:100%;max-width:280px;}
  #turn-banner{font-size:.8rem;padding:.5rem 1.2rem;}
  .result-title{font-size:2rem;}
  .countdown-num{font-size:6rem;}
  .mini-grid{width:160px;}
}
@media(max-width:400px){
  .grid{gap:.25rem;}
  .cell{font-size:.95rem;border-radius:6px;}
  .bingo-letter{width:36px;height:36px;font-size:.95rem;}
  .btn{padding:.55rem 1.2rem;font-size:.8rem;}
}
</style>

<div class="bingo-page" id="bingo-page">
  <div id="turn-banner" class="game-only"></div>
  <div class="game-layout">
    <div class="players-panel game-only" id="players-panel">
      <h3>Players</h3>
      <div class="player-card me" id="me-card">
        <span class="pname" id="me-name">You</span>
        <span class="pstatus" id="me-status">Waiting...</span>
      </div>
      <div class="player-card" id="opp-card">
        <span class="pname" id="opp-name">Unknown</span>
        <span class="pstatus" id="opp-status">Not connected</span>
      </div>
    </div>
    <div class="board-col">
      <div class="grid" id="grid"></div>
      <div class="board-controls setup-only" id="setup-controls">
        <div class="input-row">
          <div class="input-wrap">
            <label for="inp-user">Username</label>
            <input class="inp" id="inp-user" placeholder="your name" maxlength="16" autocomplete="off">
          </div>
          <div class="input-wrap">
            <label for="inp-room">Room Code</label>
            <input class="inp" id="inp-room" placeholder="e.g. sunshine42" maxlength="20" autocomplete="off">
          </div>
        </div>
        <button class="btn btn-danger" id="btn-reset">Reset</button>
        <button class="btn btn-primary" id="btn-enter" disabled onclick="enterRoom()">Enter Room</button>
      </div>
      <div class="board-controls game-only" id="game-controls">
        <button class="btn btn-primary" id="btn-ready" style="display:none" onclick="sendReady()">Ready</button>
      </div>
    </div>
    <div class="bingo-panel game-only" id="bingo-panel">
      <div class="bingo-letter" id="bl-0">B</div>
      <div class="bingo-letter" id="bl-1">I</div>
      <div class="bingo-letter" id="bl-2">N</div>
      <div class="bingo-letter" id="bl-3">G</div>
      <div class="bingo-letter" id="bl-4">O</div>
    </div>
  </div>
</div>

<div id="countdown-overlay">
  <div class="countdown-num" id="countdown-num">3</div>
</div>

<div id="result-overlay">
  <div class="result-box">
    <div class="result-title" id="result-title"></div>
    <div class="result-sub" id="result-sub"></div>
    <div class="boards-row" id="boards-row"></div>
    <a href="/bingo" class="btn btn-primary" style="margin-top:1rem;text-decoration:none;">Play Again</a>
  </div>
</div>

<script src="/js/bingo.js?v=2"></script>
`)
