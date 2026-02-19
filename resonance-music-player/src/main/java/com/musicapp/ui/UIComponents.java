package com.musicapp.ui;

import com.musicapp.util.IconRenderer;
import com.musicapp.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.function.Consumer;

/**
 * Collection of reusable custom Swing components that implement the Resonance
 * dark-theme design language.
 *
 * <p><strong>Icon rendering:</strong> All icons are drawn as pure Java2D vector
 * shapes via {@link IconRenderer}.  No symbol/emoji fonts are required, so the
 * UI looks identical on Windows, macOS, and Linux.</p>
 */
public final class UIComponents {

    private UIComponents() { /* utility */ }

    // ── Icon type enum ────────────────────────────────────────────────────────

    /** All renderable icon types. */
    public enum PlayerIcon {
        PLAY, PAUSE, PREV, NEXT, STOP,
        SHUFFLE, REPEAT_ALL, REPEAT_ONE,
        HEART_FILLED, HEART_OUTLINE,
        VOLUME_HIGH, VOLUME_MID, VOLUME_MUTE,
        GRID, LINES, NOTE
    }

    // ── RoundedPanel ──────────────────────────────────────────────────────────

    public static class RoundedPanel extends JPanel {
        private final int   arc;
        private final Color bg;
        private final Color borderColor;
        private final boolean showBorder;

        public RoundedPanel(int arc, Color bg) { this(arc, bg, false, null); }

        public RoundedPanel(int arc, Color bg, boolean showBorder, Color borderColor) {
            this.arc = arc; this.bg = bg;
            this.showBorder = showBorder; this.borderColor = borderColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            g2.setColor(bg);
            g2.fill(rr(0, 0, getWidth(), getHeight(), arc));
            if (showBorder && borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(rr(1, 1, getWidth() - 2, getHeight() - 2, arc));
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ── ProgressSlider ────────────────────────────────────────────────────────

    public static class ProgressSlider extends JComponent {
        private double value = 0.0;
        private boolean dragging = false;
        private Consumer<Double> seekConsumer;
        private final Color trackColor, fillColor, thumbColor;

        public ProgressSlider(Color track, Color fill, Color thumb) {
            this.trackColor = track; this.fillColor = fill; this.thumbColor = thumb;
            setPreferredSize(new Dimension(200, 20));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e)  { dragging = true;  commit(e.getX()); }
                public void mouseReleased(MouseEvent e) { dragging = false; commit(e.getX()); }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                public void mouseDragged(MouseEvent e) { if (dragging) commit(e.getX()); }
            });
        }

        private void commit(int x) {
            value = Math.max(0, Math.min(1.0, (double) x / getWidth()));
            repaint();
            if (seekConsumer != null) seekConsumer.accept(value);
        }

        public void setValue(double v) { if (!dragging) { value = Math.max(0, Math.min(1, v)); repaint(); } }
        public void setSeekConsumer(Consumer<Double> c) { seekConsumer = c; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            int h = 4, y = (getHeight() - h) / 2, w = getWidth(), fx = (int)(w * value), r = 7;
            g2.setColor(trackColor); g2.fillRoundRect(0, y, w, h, h, h);
            g2.setColor(fillColor);  g2.fillRoundRect(0, y, fx, h, h, h);
            g2.setColor(thumbColor); g2.fillOval(fx - r, getHeight() / 2 - r, r * 2, r * 2);
            g2.dispose();
        }
    }

    // ── IconButton ────────────────────────────────────────────────────────────

    /**
     * A borderless button that renders its icon as a pure Java2D vector shape.
     * No dependency on any symbol/emoji font — works on all platforms.
     */
    public static class IconButton extends JButton {
        private PlayerIcon  iconType;
        private boolean hovered = false;
        private boolean active  = false;
        private Color activeFg  = Theme.ACCENT_LIGHT;
        private final int iconSize;

        public IconButton(PlayerIcon iconType, int buttonSize) {
            this.iconType = iconType;
            this.iconSize = Math.max(8, buttonSize - 12);
            setPreferredSize(new Dimension(buttonSize, buttonSize));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            });
        }

        public void setIconType(PlayerIcon t)        { this.iconType = t; repaint(); }
        public void setActive(boolean a)        { this.active = a;  repaint(); }
        public void setActiveFg(Color c)        { this.activeFg = c; }
        public PlayerIcon getIconType()               { return iconType; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);

            // Hover glow background
            if (hovered) {
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }

            // Choose icon colour
            Color fg = active ? activeFg : hovered ? Theme.TEXT_PRIMARY : Theme.TEXT_SECONDARY;
            g2.setColor(fg);

            // Centre the icon in the button
            int ox = (getWidth()  - iconSize) / 2;
            int oy = (getHeight() - iconSize) / 2;
            renderIcon(g2, iconType, ox, oy, iconSize);

            g2.dispose();
        }

        /** Dispatch to the correct IconRenderer method. */
        private static void renderIcon(Graphics2D g2, PlayerIcon type, int x, int y, int size) {
            switch (type) {
                case PLAY:         IconRenderer.drawPlay(g2, x, y, size);         break;
                case PAUSE:        IconRenderer.drawPause(g2, x, y, size);        break;
                case PREV:         IconRenderer.drawPrevious(g2, x, y, size);     break;
                case NEXT:         IconRenderer.drawNext(g2, x, y, size);         break;
                case STOP:         IconRenderer.drawStop(g2, x, y, size);         break;
                case SHUFFLE:      IconRenderer.drawShuffle(g2, x, y, size);      break;
                case REPEAT_ALL:   IconRenderer.drawRepeatAll(g2, x, y, size);    break;
                case REPEAT_ONE:   IconRenderer.drawRepeatOne(g2, x, y, size);    break;
                case HEART_FILLED: IconRenderer.drawHeart(g2, x, y, size);        break;
                case HEART_OUTLINE:IconRenderer.drawHeartOutline(g2, x, y, size); break;
                case VOLUME_HIGH:  IconRenderer.drawVolumeHigh(g2, x, y, size);   break;
                case VOLUME_MID:   IconRenderer.drawVolumeMid(g2, x, y, size);    break;
                case VOLUME_MUTE:  IconRenderer.drawVolumeMute(g2, x, y, size);   break;
                case GRID:         IconRenderer.drawGrid(g2, x, y, size);         break;
                case LINES:        IconRenderer.drawLines(g2, x, y, size);        break;
                case NOTE:         IconRenderer.drawNote(g2, x, y, size);         break;
                default: break;
            }
        }
    }

    // ── VectorLabel ───────────────────────────────────────────────────────────

    /**
     * A JComponent that renders a single vector icon inline (no button behaviour).
     * Used for the favourite heart label and volume icon in the player bar.
     */
    public static class VectorLabel extends JComponent {
        private PlayerIcon    iconType;
        private Color   color;
        private final int size;

        public VectorLabel(PlayerIcon iconType, int size, Color color) {
            this.iconType = iconType; this.size = size; this.color = color;
            setPreferredSize(new Dimension(size + 4, size + 4));
            setOpaque(false);
        }

        public void setIconType(PlayerIcon t) { this.iconType = t; repaint(); }
        public void setColor(Color c)   { this.color = c;    repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            g2.setColor(color);
            int ox = (getWidth()  - size) / 2;
            int oy = (getHeight() - size) / 2;
            IconButton.renderIcon(g2, iconType, ox, oy, size);
            g2.dispose();
        }
    }

    // ── NavIconLabel ──────────────────────────────────────────────────────────

    /**
     * Sidebar nav item that shows a vector icon + text label side-by-side.
     * This replaces the previous approach of using Unicode characters in JLabel text.
     */
    public static class NavIconLabel extends JPanel {
        public NavIconLabel(PlayerIcon iconType, String text, int iconSize) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
            setOpaque(false);

            VectorLabel ico = new VectorLabel(iconType, iconSize, Theme.TEXT_SECONDARY);
            JLabel      lbl = new JLabel(text);
            lbl.setFont(Theme.plain(14));
            lbl.setForeground(Theme.TEXT_SECONDARY);

            add(ico); add(lbl);
        }
    }

    // ── AlbumArtPanel ─────────────────────────────────────────────────────────

    public static class AlbumArtPanel extends JPanel {
        private Color  c1, c2;
        private boolean showNote  = true;   // show music-note instead of text initials at start
        private String initials   = "";
        private boolean playing   = false;
        private float   angle     = 0f;

        public AlbumArtPanel(Color c1, Color c2) {
            this.c1 = c1; this.c2 = c2;
            setOpaque(false);
            new Timer(30, e -> { if (playing) { angle = (angle + 0.5f) % 360f; repaint(); } }).start();
        }

        public void setPlaying(boolean p) { playing = p; }

        public void setColors(Color c1, Color c2, String initials) {
            this.c1 = c1; this.c2 = c2;
            this.initials = initials;
            this.showNote = (initials == null || initials.isBlank());
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            int s  = Math.min(getWidth(), getHeight());
            int ox = (getWidth()  - s) / 2;
            int oy = (getHeight() - s) / 2;
            double cx = getWidth()  / 2.0;
            double cy = getHeight() / 2.0;

            // Glow ring
            g2.setColor(new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 28));
            g2.fillOval(ox - 8, oy - 8, s + 16, s + 16);

            // Disc (rotates)
            g2.rotate(Math.toRadians(angle), cx, cy);
            g2.setPaint(new GradientPaint(ox, oy, c1, ox + s, oy + s, c2));
            g2.fillOval(ox, oy, s, s);

            // Centre hole
            int hole = s / 3;
            g2.setColor(Theme.BG_DARK);
            g2.fillOval((int)(cx - hole / 2.0), (int)(cy - hole / 2.0), hole, hole);

            // Reset rotation before drawing label
            g2.rotate(-Math.toRadians(angle), cx, cy);
            g2.setColor(Color.WHITE);

            if (showNote || initials.isBlank()) {
                // Draw vector music note centred
                int ns = Math.max(8, s / 3);
                IconRenderer.drawNote(g2, (int)(cx - ns / 2.0), (int)(cy - ns / 2.0), ns);
            } else {
                // Draw text initials
                g2.setFont(Theme.bold(Math.max(8, s / 5)));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials,
                    (int)(cx - fm.stringWidth(initials) / 2.0),
                    (int)(cy + fm.getAscent() / 2.0 - 2));
            }
            g2.dispose();
        }
    }

    // ── VisualizerBar ─────────────────────────────────────────────────────────

    public static class VisualizerBar extends JPanel {
        private final float[] heights;
        private final Color   barColor;
        private boolean active = false;

        public VisualizerBar(int bars, Color color) {
            heights  = new float[bars];
            barColor = color;
            setOpaque(false);
            setPreferredSize(new Dimension(bars * 5, 30));
            Arrays.fill(heights, 0.1f);
            new Timer(80, e -> {
                if (active) {
                    for (int i = 0; i < heights.length; i++)
                        heights[i] = (float)(0.15 + Math.random() * 0.85);
                    repaint();
                }
            }).start();
        }

        public void setActive(boolean a) {
            active = a;
            if (!a) { Arrays.fill(heights, 0.1f); }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = aa(g);
            int bw = Math.max(1, getWidth() / heights.length - 2);
            for (int i = 0; i < heights.length; i++) {
                int bh = (int)(heights[i] * getHeight());
                g2.setColor(new Color(barColor.getRed(), barColor.getGreen(), barColor.getBlue(), 200));
                g2.fillRoundRect(i * (bw + 2), getHeight() - bh, bw, bh, 3, 3);
            }
            g2.dispose();
        }
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    static Graphics2D aa(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,    RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    private static RoundRectangle2D.Float rr(int x, int y, int w, int h, int arc) {
        return new RoundRectangle2D.Float(x, y, w, h, arc, arc);
    }
}
