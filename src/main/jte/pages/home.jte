@param String title
@template.layout.main(title = title, content = @`

<style>
    /* ── Hero ── */
    .hero {
        text-align: center;
        padding: 6rem 2rem 4rem;
        position: relative;
        overflow: hidden;
    }
    .hero::before {
        content: '';
        position: absolute;
        inset: 0;
        background: radial-gradient(ellipse 60% 50% at 50% -10%, rgba(124, 58, 237, 0.25), transparent);
        pointer-events: none;
    }
    .hero-eyebrow {
        display: inline-block;
        font-size: 0.8rem;
        font-weight: 600;
        letter-spacing: 0.12em;
        text-transform: uppercase;
        color: var(--accent);
        background: rgba(251, 191, 36, 0.1);
        border: 1px solid rgba(251, 191, 36, 0.2);
        padding: 0.35rem 1rem;
        border-radius: 9999px;
        margin-bottom: 1.75rem;
    }
    .hero h1 {
        font-size: clamp(2.5rem, 6vw, 4.5rem);
        font-weight: 900;
        line-height: 1.1;
        letter-spacing: -0.03em;
        margin-bottom: 1rem;
    }
    .hero h1 .grad {
        background: linear-gradient(135deg, #a78bfa, #fbbf24 70%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
    }
    .hero-sub {
        font-size: 1.15rem;
        color: var(--muted);
        max-width: 480px;
        margin: 0 auto 3rem;
        line-height: 1.7;
        font-weight: 400;
    }

    /* ── Games grid ── */
    .games-section {
        padding: 2rem 2.5rem 6rem;
        max-width: 1100px;
        margin: 0 auto;
        width: 100%;
    }
    .section-label {
        font-size: 0.75rem;
        font-weight: 600;
        letter-spacing: 0.1em;
        text-transform: uppercase;
        color: var(--muted);
        margin-bottom: 1.5rem;
    }
    .games-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 1.5rem;
    }

    /* ── Game card ── */
    .game-card {
        position: relative;
        background: var(--bg-card);
        border: 1px solid var(--border);
        border-radius: var(--radius);
        padding: 2rem;
        text-decoration: none;
        color: var(--text);
        display: flex;
        flex-direction: column;
        gap: 1rem;
        overflow: hidden;
        transition: transform 0.2s, border-color 0.2s, box-shadow 0.2s;
        cursor: pointer;
    }
    .game-card::before {
        content: '';
        position: absolute;
        inset: 0;
        background: linear-gradient(135deg, rgba(124,58,237,0.12), rgba(251,191,36,0.06));
        opacity: 0;
        transition: opacity 0.2s;
    }
    .game-card:hover {
        transform: translateY(-4px);
        border-color: rgba(124, 58, 237, 0.4);
        box-shadow: 0 20px 40px -10px rgba(124, 58, 237, 0.2);
    }
    .game-card:hover::before { opacity: 1; }

    .game-icon {
        font-size: 2.5rem;
        line-height: 1;
    }
    .game-name {
        font-size: 1.4rem;
        font-weight: 700;
        letter-spacing: -0.02em;
    }
    .game-desc {
        font-size: 0.875rem;
        color: var(--muted);
        line-height: 1.6;
        flex: 1;
    }
    .game-cta {
        display: inline-flex;
        align-items: center;
        gap: 0.4rem;
        font-size: 0.875rem;
        font-weight: 600;
        color: var(--primary-l);
        margin-top: 0.5rem;
    }
    .game-cta::after { content: '→'; transition: transform 0.2s; }
    .game-card:hover .game-cta::after { transform: translateX(4px); }

    .game-badge {
        position: absolute;
        top: 1rem;
        right: 1rem;
        font-size: 0.65rem;
        font-weight: 700;
        letter-spacing: 0.1em;
        text-transform: uppercase;
        padding: 0.2rem 0.6rem;
        border-radius: 9999px;
        background: rgba(124, 58, 237, 0.2);
        color: var(--primary-l);
        border: 1px solid rgba(124, 58, 237, 0.3);
    }
</style>

<section class="hero">
    <div class="hero-eyebrow">🎮 Mini Games Collection</div>
    <h1>Play Together,<br><span class="grad">Win Together</span></h1>
    <p class="hero-sub">Fast, fun mini games you can enjoy with friends. No downloads, no installs — just open and play.</p>
</section>

<section class="games-section">
    <p class="section-label">Available Games</p>
    <div class="games-grid">
        <a href="/bingo" class="game-card">
            <span class="game-badge">Live</span>
            <div class="game-icon">🎱</div>
            <div class="game-name">Bingo</div>
            <div class="game-desc">Classic bingo with a twist — play with friends in real-time. Mark numbers, shout Bingo, and win!</div>
            <span class="game-cta">Play now</span>
        </a>
    </div>
</section>

`)
