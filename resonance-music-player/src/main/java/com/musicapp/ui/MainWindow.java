package com.musicapp.ui;

import com.musicapp.util.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Root application window. Composes the sidebar, content area, and player bar.
 */
public class MainWindow extends JFrame {

    private static final Logger log = LoggerFactory.getLogger(MainWindow.class);

    private final SidebarPanel sidebar;
    private final LibraryPanel library;
    private final PlayerBar    playerBar;

    public MainWindow() {
        super("Resonance — Music Player");

        sidebar   = new SidebarPanel();
        library   = new LibraryPanel();
        playerBar = new PlayerBar();

        configureFrame();
        buildLayout();
        wireNavigation();
        setVisible(true);

        log.debug("MainWindow ready");
    }

    // ── Frame config ──────────────────────────────────────────────────────────

    private void configureFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new java.awt.Dimension(900, 580));
        setLocationRelativeTo(null);
        setBackground(Theme.BG_DARKEST);

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { /* best-effort */ }

        // Override UIManager tokens to align with dark theme
        UIManager.put("ScrollBar.background",   Theme.BG_DARK);
        UIManager.put("ScrollBar.thumb",        Theme.BG_HOVER);
        UIManager.put("ScrollBar.track",        Theme.BG_DARK);
        UIManager.put("PopupMenu.background",   Theme.BG_CARD);
        UIManager.put("MenuItem.background",    Theme.BG_CARD);
        UIManager.put("MenuItem.foreground",    Theme.TEXT_PRIMARY);
        UIManager.put("OptionPane.background",  Theme.BG_MEDIUM);
        UIManager.put("Panel.background",       Theme.BG_MEDIUM);
    }

    // ── Layout ────────────────────────────────────────────────────────────────

    private void buildLayout() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, library);
        split.setDividerLocation(220);
        split.setDividerSize(1);
        split.setEnabled(false);
        split.setBorder(null);

        JPanel root = new JPanel(new java.awt.BorderLayout());
        root.setBackground(Theme.BG_DARKEST);
        root.add(split,     java.awt.BorderLayout.CENTER);
        root.add(playerBar, java.awt.BorderLayout.SOUTH);
        setContentPane(root);
    }

    // ── Navigation wiring ─────────────────────────────────────────────────────

    private void wireNavigation() {
        sidebar.setNavListener(nav -> {
            switch (nav) {
                case LIBRARY:   library.loadAllSongs();   break;
                case FAVORITES: library.loadFavorites();  break;
                case QUEUE:     library.loadQueue();      break;
            }
        });
        sidebar.setPlaylistListener(library::loadPlaylist);
    }
}
