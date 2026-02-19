package com.musicapp.util;

import java.awt.*;
import java.awt.geom.*;

/**
 * Renders all player icons as pure Java2D vector shapes.
 *
 * <p>This avoids any dependency on platform-specific symbol fonts
 * (Segoe UI Symbol, Apple Symbols, Noto, etc.) — every icon is drawn
 * programmatically and looks identical on Windows, macOS, and Linux.</p>
 *
 * <p>All draw methods accept a {@link Graphics2D} context and a bounding box
 * (x, y, size).  The caller is responsible for creating / disposing the context
 * and setting the desired {@link RenderingHints}.</p>
 */
public final class IconRenderer {

    private IconRenderer() { /* static utility */ }

    // ─────────────────────────────────────────────────────────────────────────
    // Transport controls
    // ─────────────────────────────────────────────────────────────────────────

    /** ▶ Play triangle */
    public static void drawPlay(Graphics2D g, int x, int y, int size) {
        int[] px = { x, x, x + size };
        int[] py = { y, y + size, y + size / 2 };
        g.fillPolygon(px, py, 3);
    }

    /** ⏸ Pause — two vertical bars */
    public static void drawPause(Graphics2D g, int x, int y, int size) {
        int barW = Math.max(2, size / 4);
        int barH = size;
        int gap  = Math.max(2, size / 5);
        g.fillRect(x, y, barW, barH);
        g.fillRect(x + barW + gap, y, barW, barH);
    }

    /** ⏮ Previous — left-pointing triangle + bar */
    public static void drawPrevious(Graphics2D g, int x, int y, int size) {
        int barW = Math.max(2, size / 5);
        // Left bar
        g.fillRect(x, y, barW, size);
        // Left-pointing triangle
        int tx = x + barW + 1;
        int[] px = { tx + size - barW, tx + size - barW, tx };
        int[] py = { y, y + size, y + size / 2 };
        g.fillPolygon(px, py, 3);
    }

    /** ⏭ Next — right-pointing triangle + bar */
    public static void drawNext(Graphics2D g, int x, int y, int size) {
        int barW = Math.max(2, size / 5);
        // Right-pointing triangle
        int[] px = { x, x, x + size - barW - 1 };
        int[] py = { y, y + size, y + size / 2 };
        g.fillPolygon(px, py, 3);
        // Right bar
        g.fillRect(x + size - barW, y, barW, size);
    }

    /** ⏹ Stop — solid square */
    public static void drawStop(Graphics2D g, int x, int y, int size) {
        g.fillRect(x, y, size, size);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Shuffle & Repeat
    // ─────────────────────────────────────────────────────────────────────────

    /** Shuffle icon — two crossing curved arrows */
    public static void drawShuffle(Graphics2D g, int x, int y, int size) {
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(Math.max(1.5f, size / 9f),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // Top path: left→right curving up
        Path2D top = new Path2D.Float();
        top.moveTo(x, y + size * 0.7f);
        top.curveTo(x + size * 0.3f, y + size * 0.7f,
                    x + size * 0.6f, y + size * 0.15f,
                    x + size * 0.75f, y + size * 0.15f);
        g.draw(top);
        arrowHead(g, x + size * 0.75f, y + size * 0.15f, 1, 0, size / 5);

        // Bottom path: left→right curving down
        Path2D bot = new Path2D.Float();
        bot.moveTo(x, y + size * 0.3f);
        bot.curveTo(x + size * 0.3f, y + size * 0.3f,
                    x + size * 0.6f, y + size * 0.85f,
                    x + size * 0.75f, y + size * 0.85f);
        g.draw(bot);
        arrowHead(g, x + size * 0.75f, y + size * 0.85f, 1, 0, size / 5);

        g.setStroke(old);
    }

    /** Repeat-all icon — circular arrow loop */
    public static void drawRepeatAll(Graphics2D g, int x, int y, int size) {
        Stroke old = g.getStroke();
        float sw = Math.max(1.5f, size / 8f);
        g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int pad = (int)(size * 0.12f);
        Arc2D arc = new Arc2D.Float(x + pad, y + pad,
                size - pad * 2, size - pad * 2, 30, 300, Arc2D.OPEN);
        g.draw(arc);
        // Arrow tip at end of arc
        double endAngle = Math.toRadians(30 - 10);
        double cx2 = x + pad + (size - pad * 2) / 2.0;
        double cy2 = y + pad + (size - pad * 2) / 2.0;
        double r   = (size - pad * 2) / 2.0;
        arrowHead(g,
                cx2 + r * Math.cos(endAngle),
                cy2 - r * Math.sin(endAngle),
                Math.cos(endAngle - Math.PI / 2),
                -Math.sin(endAngle - Math.PI / 2),
                size / 5);
        g.setStroke(old);
    }

    /** Repeat-one icon — repeat-all with a "1" label drawn on top */
    public static void drawRepeatOne(Graphics2D g, int x, int y, int size) {
        drawRepeatAll(g, x, y, size);
        g.setFont(new Font("Segoe UI", Font.BOLD, Math.max(8, size / 2)));
        FontMetrics fm = g.getFontMetrics();
        String lbl = "1";
        g.drawString(lbl,
                x + (size - fm.stringWidth(lbl)) / 2,
                y + (size + fm.getAscent() - fm.getDescent()) / 2);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Favourite / heart
    // ─────────────────────────────────────────────────────────────────────────

    /** ♥ Filled heart */
    public static void drawHeart(Graphics2D g, int x, int y, int size) {
        g.fill(heartShape(x, y, size));
    }

    /** ♡ Heart outline */
    public static void drawHeartOutline(Graphics2D g, int x, int y, int size) {
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(Math.max(1f, size / 10f),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(heartShape(x, y, size));
        g.setStroke(old);
    }

    private static Shape heartShape(int x, int y, int size) {
        double w = size, h = size;
        Path2D p = new Path2D.Double();
        p.moveTo(x + w / 2, y + h * 0.3);
        p.curveTo(x + w / 2, y + h * 0.05, x + w * 0.05, y + h * 0.05, x + w * 0.05, y + h * 0.3);
        p.curveTo(x + w * 0.05, y + h * 0.55, x + w / 2, y + h * 0.75, x + w / 2, y + h * 0.92);
        p.curveTo(x + w / 2, y + h * 0.75, x + w * 0.95, y + h * 0.55, x + w * 0.95, y + h * 0.3);
        p.curveTo(x + w * 0.95, y + h * 0.05, x + w / 2, y + h * 0.05, x + w / 2, y + h * 0.3);
        p.closePath();
        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Volume
    // ─────────────────────────────────────────────────────────────────────────

    /** Speaker cone (base shape used for all volume icons) */
    public static void drawVolumeSpeaker(Graphics2D g, int x, int y, int size) {
        // Speaker body
        int bx = x, by = y + size / 3, bw = size / 3, bh = size / 3;
        g.fillRect(bx, by, bw, bh);
        // Cone
        int[] px = { bx + bw, bx + bw, x + size * 2 / 3 };
        int[] py = { by, by + bh, y + size };
        int[] pxt = { bx + bw, bx + bw, x + size * 2 / 3 };
        int[] pyt = { by, by + bh, y };
        int[][] cone = { { bx + bw, x + size * 2 / 3, x + size * 2 / 3, bx + bw },
                         { by, y, y + size, by + bh } };
        g.fillPolygon(cone[0], cone[1], 4);
    }

    /** 🔊 High volume — speaker + 3 arcs */
    public static void drawVolumeHigh(Graphics2D g, int x, int y, int size) {
        drawVolumeSpeaker(g, x, y, size);
        Stroke old = g.getStroke();
        float sw = Math.max(1.5f, size / 9f);
        g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = x + size * 2 / 3;
        int cy = y + size / 2;
        for (int r = size / 5; r <= size * 2 / 5; r += size / 5) {
            g.drawArc(cx - r, cy - r, r * 2, r * 2, -45, 90);
        }
        g.setStroke(old);
    }

    /** 🔉 Medium volume — speaker + 1 arc */
    public static void drawVolumeMid(Graphics2D g, int x, int y, int size) {
        drawVolumeSpeaker(g, x, y, size);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(Math.max(1.5f, size / 9f),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int cx = x + size * 2 / 3, cy = y + size / 2, r = size / 4;
        g.drawArc(cx - r, cy - r, r * 2, r * 2, -45, 90);
        g.setStroke(old);
    }

    /** 🔇 Muted — speaker + X cross */
    public static void drawVolumeMute(Graphics2D g, int x, int y, int size) {
        drawVolumeSpeaker(g, x, y, size);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(Math.max(1.5f, size / 8f),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int ox = x + size * 3 / 4, oy = y + size / 4, len = size / 4;
        g.drawLine(ox, oy, ox + len, oy + len);
        g.drawLine(ox + len, oy, ox, oy + len);
        g.setStroke(old);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sidebar nav
    // ─────────────────────────────────────────────────────────────────────────

    /** Grid / library icon — 2×2 squares */
    public static void drawGrid(Graphics2D g, int x, int y, int size) {
        int cell = size / 2 - 1;
        int gap  = 2;
        g.fillRoundRect(x, y, cell, cell, 3, 3);
        g.fillRoundRect(x + cell + gap, y, cell, cell, 3, 3);
        g.fillRoundRect(x, y + cell + gap, cell, cell, 3, 3);
        g.fillRoundRect(x + cell + gap, y + cell + gap, cell, cell, 3, 3);
    }

    /** Three horizontal lines — queue / list icon */
    public static void drawLines(Graphics2D g, int x, int y, int size) {
        Stroke old = g.getStroke();
        float sw = Math.max(1.5f, size / 7f);
        g.setStroke(new BasicStroke(sw, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int spacing = size / 4;
        for (int i = 0; i < 3; i++) {
            int ly = y + spacing + i * spacing;
            g.drawLine(x, ly, x + size, ly);
        }
        g.setStroke(old);
    }

    /** Music note — for the logo / album default */
    public static void drawNote(Graphics2D g, int x, int y, int size) {
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(Math.max(1.5f, size / 8f),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Stem
        int stemX = x + size * 2 / 3;
        g.drawLine(stemX, y + size / 5, stemX, y + size * 4 / 5);
        // Flag
        Path2D flag = new Path2D.Float();
        flag.moveTo(stemX, y + size / 5);
        flag.curveTo(stemX + size / 3, y + size / 4,
                     stemX + size / 3, y + size * 2 / 5,
                     stemX,            y + size * 2 / 5);
        g.draw(flag);
        // Note head (filled oval)
        g.setStroke(old);
        g.fillOval(x + size / 6, y + size * 3 / 5, size / 3, size / 4);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper
    // ─────────────────────────────────────────────────────────────────────────

    private static void arrowHead(Graphics2D g, double tipX, double tipY,
                                   double dx, double dy, int size) {
        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return;
        dx /= len; dy /= len;
        int hs = Math.max(3, size / 2);
        int[] px = {
            (int) tipX,
            (int)(tipX - hs * dx + hs / 2.0 * dy),
            (int)(tipX - hs * dx - hs / 2.0 * dy)
        };
        int[] py = {
            (int) tipY,
            (int)(tipY - hs * dy - hs / 2.0 * dx),
            (int)(tipY - hs * dy + hs / 2.0 * dx)
        };
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(1f));
        g.fillPolygon(px, py, 3);
        g.setStroke(old);
    }
}
