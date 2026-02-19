package com.musicapp.util;

import java.awt.*;

/**
 * Centralised design tokens (colours, fonts) for the Resonance dark theme.
 * All values are {@code static final} so callers can import-static them.
 */
public final class Theme {

    private Theme() { /* utility class */ }

    // ── Background palette ─────────────────────────────────────────────────────
    public static final Color BG_DARKEST  = new Color(10,  10,  18);
    public static final Color BG_DARK     = new Color(18,  18,  30);
    public static final Color BG_MEDIUM   = new Color(25,  25,  42);
    public static final Color BG_PANEL    = new Color(32,  32,  52);
    public static final Color BG_CARD     = new Color(40,  40,  65);
    public static final Color BG_HOVER    = new Color(55,  55,  85);
    public static final Color BG_SELECTED = new Color(65,  45, 110);

    // ── Accent ─────────────────────────────────────────────────────────────────
    public static final Color ACCENT       = new Color(138,  43, 226);
    public static final Color ACCENT_LIGHT = new Color(180, 100, 255);
    public static final Color ACCENT_GLOW  = new Color(138,  43, 226, 60);
    public static final Color PINK         = new Color(255,  70, 140);
    public static final Color CYAN         = new Color(  0, 210, 220);

    // ── Text ───────────────────────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY   = new Color(240, 240, 255);
    public static final Color TEXT_SECONDARY = new Color(160, 160, 190);
    public static final Color TEXT_MUTED     = new Color( 90,  90, 120);

    // ── Borders ────────────────────────────────────────────────────────────────
    public static final Color BORDER        = new Color( 60,  60,  90);
    public static final Color BORDER_ACCENT = new Color(100,  60, 160);

    // ── Controls ───────────────────────────────────────────────────────────────
    public static final Color PROGRESS_BG = new Color(50, 50, 80);
    public static final Color PROGRESS_FG = ACCENT_LIGHT;
    public static final Color VOLUME_FG   = CYAN;

    // ── Playlist colour wheel ──────────────────────────────────────────────────
    public static final Color[] PLAYLIST_COLORS = {
        new Color(233,  30,  99),
        new Color(255,  87,  34),
        new Color(156,  39, 176),
        new Color(  0, 188, 212),
        new Color( 76, 175,  80),
        new Color(255, 193,   7),
        new Color( 63,  81, 181),
        new Color(  0, 150, 136),
    };

    // ── Font helpers ───────────────────────────────────────────────────────────
    public static Font bold(int size)  { return new Font("Segoe UI", Font.BOLD,  size); }
    public static Font plain(int size) { return new Font("Segoe UI", Font.PLAIN, size); }
    public static Font mono(int size)  { return new Font("Consolas", Font.PLAIN, size); }
}
