@param String title
@template.layout.main(title = title, content = @`

<style>
    .wordle-container {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 2rem;
        padding: 2rem 1rem;
        max-width: 1000px;
        margin: 0 auto;
        min-height: calc(100vh - 150px);
    }
    .setup-panel {
        background: var(--bg-card);
        border: 1px solid var(--border);
        border-radius: var(--radius);
        padding: 2rem;
        text-align: center;
        width: 100%;
        max-width: 400px;
    }
    .input-group {
        display: flex;
        flex-direction: column;
        gap: 0.5rem;
        margin-bottom: 1.5rem;
        text-align: left;
    }
    .input-group label {
        font-size: 0.85rem;
        font-weight: 600;
        color: var(--muted);
        text-transform: uppercase;
        letter-spacing: 0.05em;
    }
    .input-group input {
        background: rgba(0, 0, 0, 0.2);
        border: 1px solid var(--border);
        padding: 0.8rem 1rem;
        border-radius: 6px;
        color: var(--text);
        font-size: 1rem;
        outline: none;
        transition: border-color 0.2s;
    }
    .input-group input:focus { border-color: var(--primary); }
    .btn {
        background: var(--primary);
        color: white;
        border: none;
        padding: 0.8rem 1.5rem;
        font-size: 1rem;
        font-weight: 600;
        border-radius: 6px;
        cursor: pointer;
        width: 100%;
        transition: background 0.2s;
    }
    .btn:hover { background: var(--primary-l); }
    
    .game-area {
        display: none;
        width: 100%;
        flex-direction: column;
        align-items: center;
        gap: 2rem;
    }
    .top-bar {
        display: flex;
        justify-content: space-between;
        width: 100%;
        background: var(--bg-card);
        padding: 1rem 2rem;
        border-radius: var(--radius);
        border: 1px solid var(--border);
    }
    .player-info {
        display: flex;
        align-items: center;
        gap: 1rem;
        font-weight: 600;
    }
    .status-badge {
        font-size: 0.75rem;
        padding: 0.25rem 0.75rem;
        border-radius: 99px;
        text-transform: uppercase;
        letter-spacing: 0.05em;
    }
    .status-waiting { background: rgba(251, 191, 36, 0.2); color: #fbbf24; }
    .status-playing { background: rgba(52, 211, 153, 0.2); color: #34d399; }
    .status-disconnected { background: rgba(239, 68, 68, 0.2); color: #ef4444; }

    .grids-wrapper {
        display: flex;
        gap: 4rem;
        justify-content: center;
        flex-wrap: wrap;
    }
    .grid-section {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
    }
    .grid-title {
        font-weight: 600;
        color: var(--muted);
        text-transform: uppercase;
        letter-spacing: 0.1em;
        font-size: 0.85rem;
    }
    
    .wordle-grid {
        display: grid;
        grid-template-rows: repeat(6, 1fr);
        gap: 5px;
    }
    .wordle-row {
        display: grid;
        grid-template-columns: repeat(5, 1fr);
        gap: 5px;
    }
    .wordle-cell {
        width: clamp(40px, 12vw, 60px);
        height: clamp(40px, 12vw, 60px);
        border: 2px solid var(--border);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: clamp(1.5rem, 6vw, 2rem);
        font-weight: bold;
        text-transform: uppercase;
        user-select: none;
    }
    .opponent-grid .wordle-cell {
        width: clamp(30px, 8vw, 40px);
        height: clamp(30px, 8vw, 40px);
        font-size: clamp(1rem, 4vw, 1.2rem);
    }
    
    .cell-state-0 { background-color: #3a3a3c; border-color: #3a3a3c; color: white; }
    .cell-state-1 { background-color: #b59f3b; border-color: #b59f3b; color: white; }
    .cell-state-2 { background-color: #538d4e; border-color: #538d4e; color: white; }

    .keyboard {
        display: flex;
        flex-direction: column;
        gap: 6px;
        margin-top: 1rem;
        user-select: none;
        width: 100%;
        max-width: 500px;
    }
    .keyboard-row {
        display: flex;
        justify-content: center;
        gap: 6px;
        width: 100%;
    }
    .key {
        background: #818384;
        color: white;
        border: none;
        border-radius: 4px;
        height: 58px;
        flex: 1;
        padding: 0;
        font-weight: bold;
        cursor: pointer;
        font-size: clamp(0.8rem, 3.5vw, 1.1rem);
        text-transform: uppercase;
    }
    .key:active { transform: scale(0.95); }
    .key-wide { flex: 1.5; font-size: clamp(0.65rem, 3vw, 0.9rem); }
    
    .key-state-0 { background-color: #3a3a3c; }
    .key-state-1 { background-color: #b59f3b; }
    .key-state-2 { background-color: #538d4e; }

    #toast {
        position: fixed;
        top: 20px;
        left: 50%;
        transform: translateX(-50%);
        background: rgba(255, 255, 255, 0.9);
        color: black;
        padding: 1rem 2rem;
        border-radius: 8px;
        font-weight: bold;
        box-shadow: 0 4px 12px rgba(0,0,0,0.5);
        opacity: 0;
        pointer-events: none;
        transition: opacity 0.3s;
        z-index: 1000;
    }
    #toast.show { opacity: 1; }

    .game-over-overlay {
        position: fixed;
        inset: 0;
        background: rgba(0, 0, 0, 0.8);
        display: none;
        align-items: center;
        justify-content: center;
        z-index: 2000;
        flex-direction: column;
        gap: 1rem;
        text-align: center;
    }
    .game-over-overlay.show { display: flex; }
    .game-over-title { font-size: 3rem; font-weight: 900; }
    .game-over-word { font-size: 1.5rem; color: var(--primary); margin-bottom: 1rem; }
    .game-over-title.win { color: #538d4e; }
    .game-over-title.lose { color: #ef4444; }
</style>

<div class="wordle-container">
    <div id="toast"></div>

    <div class="setup-panel" id="setupPanel">
        <h2 style="margin-bottom: 1.5rem;">Join Wordle Room</h2>
        <div class="input-group">
            <label for="usernameInput">Your Username</label>
            <input type="text" id="usernameInput" placeholder="Enter username..." maxlength="15">
        </div>
        <div class="input-group">
            <label for="roomIdInput">Room Code</label>
            <input type="text" id="roomIdInput" placeholder="Enter room code..." maxlength="10">
        </div>
        <button class="btn" id="joinBtn">Join Room</button>
    </div>

    <div class="game-area" id="gameArea">
        <div class="top-bar">
            <div class="player-info">
                <span>You: <span id="myName" style="color: var(--primary)"></span></span>
            </div>
            <button id="readyBtn" class="btn" style="width: auto; padding: 0.5rem 2rem;">I'm Ready</button>
            <div class="player-info">
                <span>Opponent: <span id="opponentName" style="color: var(--primary)">Waiting...</span></span>
                <span id="opponentStatus" class="status-badge status-waiting">Waiting</span>
            </div>
        </div>

        <div class="grids-wrapper">
            <!-- Player Grid -->
            <div class="grid-section">
                <div class="grid-title">Your Board</div>
                <div class="wordle-grid" id="playerGrid">
                    <!-- JS will generate 6 rows of 5 cells -->
                </div>
            </div>

            <!-- Opponent Grid -->
            <div class="grid-section">
                <div class="grid-title">Opponent's Board</div>
                <div class="wordle-grid opponent-grid" id="opponentGrid">
                    <!-- JS will generate 6 rows of 5 cells -->
                </div>
            </div>
        </div>

        <div class="keyboard" id="keyboard">
            <div class="keyboard-row">
                <button class="key" data-key="Q">Q</button>
                <button class="key" data-key="W">W</button>
                <button class="key" data-key="E">E</button>
                <button class="key" data-key="R">R</button>
                <button class="key" data-key="T">T</button>
                <button class="key" data-key="Y">Y</button>
                <button class="key" data-key="U">U</button>
                <button class="key" data-key="I">I</button>
                <button class="key" data-key="O">O</button>
                <button class="key" data-key="P">P</button>
            </div>
            <div class="keyboard-row">
                <button class="key" data-key="A">A</button>
                <button class="key" data-key="S">S</button>
                <button class="key" data-key="D">D</button>
                <button class="key" data-key="F">F</button>
                <button class="key" data-key="G">G</button>
                <button class="key" data-key="H">H</button>
                <button class="key" data-key="J">J</button>
                <button class="key" data-key="K">K</button>
                <button class="key" data-key="L">L</button>
            </div>
            <div class="keyboard-row">
                <button class="key key-wide" data-key="ENTER">ENTER</button>
                <button class="key" data-key="Z">Z</button>
                <button class="key" data-key="X">X</button>
                <button class="key" data-key="C">C</button>
                <button class="key" data-key="V">V</button>
                <button class="key" data-key="B">B</button>
                <button class="key" data-key="N">N</button>
                <button class="key" data-key="M">M</button>
                <button class="key key-wide" data-key="BACKSPACE">⌫</button>
            </div>
        </div>
    </div>
</div>

<div class="game-over-overlay" id="gameOverOverlay">
    <div class="game-over-title" id="gameOverTitle">YOU WIN!</div>
    <div class="game-over-word" id="gameOverWord">Target word was: APPLE</div>
    <button class="btn" onclick="location.reload()" style="max-width: 200px;">Play Again</button>
</div>

<script src="/js/wordle.js"></script>

`)
