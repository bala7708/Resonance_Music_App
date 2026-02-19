package com.musicapp.ui;

import com.musicapp.model.Playlist;
import com.musicapp.service.MusicLibrary;
import com.musicapp.util.Theme;
import com.musicapp.ui.UIComponents.PlayerIcon;
import com.musicapp.ui.UIComponents.VectorLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Left-hand navigation sidebar containing main nav items and the playlist list.
 *
 * <p>All sidebar icons are drawn as pure Java2D vector shapes — no Unicode
 * symbol fonts required.</p>
 */
public class SidebarPanel extends JPanel {

    /** Top-level navigation destinations. */
    public enum NavItem { LIBRARY, FAVORITES, QUEUE }

    private NavItem  selectedNav      = NavItem.LIBRARY;
    private Playlist selectedPlaylist = null;

    private Consumer<NavItem>  navListener;
    private Consumer<Playlist> playlistListener;

    private JPanel playlistsContainer;

    public SidebarPanel() {
        setBackground(Theme.BG_DARK);
        setPreferredSize(new Dimension(220, 0));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));
        buildUI();
    }

    // ── Construction ──────────────────────────────────────────────────────────

    private void buildUI() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Theme.BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Logo — vector note icon + text (no font symbols needed)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        logoPanel.setBackground(Theme.BG_DARK);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 22, 0));
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        VectorLabel logoIcon = new VectorLabel(PlayerIcon.NOTE, 22, Theme.ACCENT_LIGHT);
        JLabel logoText = new JLabel("Resonance");
        logoText.setFont(Theme.bold(20));
        logoText.setForeground(Theme.ACCENT_LIGHT);
        logoPanel.add(logoIcon);
        logoPanel.add(logoText);
        content.add(logoPanel);

        // Main nav — each row uses a VectorLabel icon
        content.add(sectionHeader("MENU"));
        content.add(navRow(PlayerIcon.GRID,         "Library",   NavItem.LIBRARY));
        content.add(navRow(PlayerIcon.HEART_FILLED,  "Favorites", NavItem.FAVORITES));
        content.add(navRow(PlayerIcon.LINES,         "Queue",     NavItem.QUEUE));

        content.add(Box.createVerticalStrut(16));
        content.add(sectionHeader("PLAYLISTS"));

        playlistsContainer = new JPanel();
        playlistsContainer.setLayout(new BoxLayout(playlistsContainer, BoxLayout.Y_AXIS));
        playlistsContainer.setBackground(Theme.BG_DARK);
        content.add(playlistsContainer);

        refreshPlaylists();

        JScrollPane scroll = new JScrollPane(content,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.BG_DARK);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(4, 0));
        add(scroll, BorderLayout.CENTER);
    }

    private JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.bold(10));
        lbl.setForeground(Theme.TEXT_MUTED);
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 20, 4, 0));
        return lbl;
    }

    /**
     * Build a nav row that shows a vector icon + text label.
     * The active-state indicator is a coloured left bar painted in paintComponent.
     */
    private JPanel navRow(PlayerIcon iconType, String label, NavItem nav) {
        JPanel row = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                if (selectedNav == nav && selectedPlaylist == null) {
                    g.setColor(Theme.BG_SELECTED);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Theme.ACCENT);
                    g.fillRect(0, 0, 3, getHeight());
                }
                super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 12));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        boolean isSelected = selectedNav == nav && selectedPlaylist == null;
        Color iconCol = isSelected ? Theme.ACCENT_LIGHT : Theme.TEXT_SECONDARY;

        // Vector icon — platform-independent
        VectorLabel ico = new VectorLabel(iconType, 16, iconCol);

        JLabel lbl = new JLabel(label);
        lbl.setFont(Theme.plain(14));
        lbl.setForeground(iconCol);

        JPanel inner = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        inner.setOpaque(false);
        inner.add(ico);
        inner.add(lbl);
        row.add(inner, BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (selectedNav != nav || selectedPlaylist != null) {
                    row.setOpaque(true);
                    row.setBackground(Theme.BG_HOVER);
                }
            }
            public void mouseExited(MouseEvent e) { row.setOpaque(false); row.repaint(); }
            public void mouseClicked(MouseEvent e) {
                selectedNav = nav;
                selectedPlaylist = null;
                if (navListener != null) navListener.accept(nav);
                refreshUI();
            }
        });
        return row;
    }

    // ── Playlist list ─────────────────────────────────────────────────────────

    public void refreshPlaylists() {
        playlistsContainer.removeAll();
        for (Playlist pl : MusicLibrary.getInstance().getAllPlaylists()) {
            playlistsContainer.add(playlistRow(pl));
        }

        JButton addBtn = new JButton("+ New Playlist");
        addBtn.setFont(Theme.plain(12));
        addBtn.setForeground(Theme.TEXT_MUTED);
        addBtn.setContentAreaFilled(false);
        addBtn.setBorderPainted(false);
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addBtn.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 12));
        addBtn.setHorizontalAlignment(SwingConstants.LEFT);
        addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        addBtn.addActionListener(e -> createPlaylist());
        playlistsContainer.add(addBtn);
        playlistsContainer.revalidate();
        playlistsContainer.repaint();
    }

    private JPanel playlistRow(Playlist pl) {
        Color dot = parseHex(pl.getCoverHexColor());

        JPanel row = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                if (pl.equals(selectedPlaylist)) {
                    g.setColor(Theme.BG_SELECTED);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Theme.ACCENT);
                    g.fillRect(0, 0, 3, getHeight());
                }
                super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row.setBorder(BorderFactory.createEmptyBorder(5, 16, 5, 12));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Colour dot — painted via Graphics2D (no emoji needed)
        JPanel colorDot = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIComponents.aa(g);
                g2.setColor(dot);
                g2.fillRoundRect(0, 2, 12, 12, 4, 4);
                g2.dispose();
            }
        };
        colorDot.setOpaque(false);
        colorDot.setPreferredSize(new Dimension(12, 16));

        JLabel lbl = new JLabel(pl.getName());
        lbl.setFont(Theme.plain(13));
        lbl.setForeground(Theme.TEXT_SECONDARY);

        row.add(colorDot, BorderLayout.WEST);
        row.add(lbl,      BorderLayout.CENTER);

        row.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!pl.equals(selectedPlaylist)) { row.setOpaque(true); row.setBackground(Theme.BG_HOVER); }
            }
            public void mouseExited (MouseEvent e) { row.setOpaque(false); row.repaint(); }
            public void mouseClicked(MouseEvent e) {
                selectedPlaylist = pl;
                if (playlistListener != null) playlistListener.accept(pl);
                refreshUI();
            }
        });
        return row;
    }

    private void createPlaylist() {
        String name = JOptionPane.showInputDialog(this, "Playlist name:", "New Playlist", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.isBlank()) {
            Color[] cols = Theme.PLAYLIST_COLORS;
            Color c = cols[(int)(Math.random() * cols.length)];
            String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            MusicLibrary.getInstance().addPlaylist(
                new Playlist("p" + System.currentTimeMillis(), name.trim(), "", hex));
            refreshPlaylists();
        }
    }

    private void refreshUI() { repaint(); revalidate(); }

    private static Color parseHex(String hex) {
        try { return Color.decode(hex.startsWith("#") ? hex : "#" + hex); }
        catch (NumberFormatException e) { return Theme.ACCENT; }
    }

    // ── Listener setters ──────────────────────────────────────────────────────
    public void setNavListener(Consumer<NavItem> l)       { navListener = l; }
    public void setPlaylistListener(Consumer<Playlist> l) { playlistListener = l; }
}
