package com.musicapp.ui;

import com.musicapp.controller.PlayerController;
import com.musicapp.util.IconRenderer;
import com.musicapp.model.Playlist;
import com.musicapp.model.Song;
import com.musicapp.service.MusicLibrary;
import com.musicapp.util.Theme;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Central panel that renders the song list as a styled table.
 * Supports switching between Library / Favorites / Queue / Playlist views.
 */
public class LibraryPanel extends JPanel {

    private static final String[] COLUMNS = {"", "#", "Title", "Artist", "Album", "Genre", "Year", "Time"};
    private static final int[]    COL_W   = { 30, 40,  220,    160,      160,      100,     55,     65 };

    private List<Song>         currentSongs;
    private JLabel             headerLabel;
    private JTable             table;
    private DefaultTableModel  model;

    public LibraryPanel() {
        setBackground(Theme.BG_MEDIUM);
        setLayout(new BorderLayout());
        buildHeader();
        buildTable();
        loadAllSongs();
    }

    // ── UI construction ───────────────────────────────────────────────────────

    private void buildHeader() {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setBackground(Theme.BG_MEDIUM);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 12, 24));

        headerLabel = new JLabel("Library");
        headerLabel.setFont(Theme.bold(24));
        headerLabel.setForeground(Theme.TEXT_PRIMARY);

        JTextField search = new JTextField(20);
        search.setBackground(Theme.BG_CARD);
        search.setForeground(Theme.TEXT_PRIMARY);
        search.setCaretColor(Theme.TEXT_PRIMARY);
        search.setFont(Theme.plain(13));
        search.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1, true),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        search.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String q = search.getText().trim();
                populate(q.isEmpty() ? MusicLibrary.getInstance().getAllSongs()
                                     : MusicLibrary.getInstance().search(q));
            }
        });

        header.add(headerLabel, BorderLayout.WEST);
        header.add(search,      BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    private void buildTable() {
        model = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                Song s = row < currentSongs.size() ? currentSongs.get(row) : null;
                boolean sel     = isRowSelected(row);
                boolean playing = s != null && s.equals(PlayerController.getInstance().getCurrentSong());

                Color bg = playing ? Theme.BG_SELECTED
                         : sel     ? Theme.BG_HOVER
                         : row % 2 == 0 ? Theme.BG_MEDIUM : new Color(28,28,46);
                Color fg = playing ? Theme.ACCENT_LIGHT
                         : sel     ? Theme.TEXT_PRIMARY
                         : col == 1 ? Theme.TEXT_MUTED : Theme.TEXT_SECONDARY;

                c.setBackground(bg);
                c.setForeground(fg);
                if (c instanceof JLabel) ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0,8,0,8));
                return c;
            }
        };
        styleTable();
        wireTableInteractions();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Theme.BG_MEDIUM);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(6, 0));
        add(scroll, BorderLayout.CENTER);

        // Repaint when current song changes
        PlayerController.getInstance().addSongListener(s -> SwingUtilities.invokeLater(table::repaint));
    }

    private void styleTable() {
        table.setBackground(Theme.BG_MEDIUM);
        table.setForeground(Theme.TEXT_SECONDARY);
        table.setGridColor(new Color(40, 40, 60));
        table.setRowHeight(42);
        table.setFont(Theme.plain(13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionBackground(Theme.BG_HOVER);
        table.setSelectionForeground(Theme.TEXT_PRIMARY);
        table.setFocusable(false);

        for (int i = 0; i < COL_W.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(COL_W[i]);
            if (i == 0 || i == 1 || i == 6 || i == 7) {
                col.setMinWidth(COL_W[i]); col.setMaxWidth(COL_W[i]);
            }
        }

        // Heart renderer for column 0 — pure Java2D vector, no font dependency
        table.getColumnModel().getColumn(0).setCellRenderer(new HeartCellRenderer());

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(Theme.BG_PANEL);
        header.setForeground(Theme.TEXT_MUTED);
        header.setFont(Theme.bold(11));
        header.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Theme.BORDER));
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);
    }

    private void wireTableInteractions() {
        // Double-click → play
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || row >= currentSongs.size()) return;
                Song song = currentSongs.get(row);

                if (e.getClickCount() == 2) {
                    PlayerController.getInstance().play(song, currentSongs);
                    table.repaint();
                } else if (col == 0) {
                    song.setFavorite(!song.isFavorite());
                    table.repaint();
                }
            }
        });

        // Right-click context menu
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(Theme.BG_CARD);
        JMenuItem playItem = styledItem("Play Now");
        JMenuItem favItem  = styledItem("Toggle Favorite");
        menu.add(playItem); menu.addSeparator(); menu.add(favItem);

        playItem.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0 && r < currentSongs.size())
                PlayerController.getInstance().play(currentSongs.get(r), currentSongs);
        });
        favItem.addActionListener(e -> {
            int r = table.getSelectedRow();
            if (r >= 0 && r < currentSongs.size()) {
                Song s = currentSongs.get(r);
                s.setFavorite(!s.isFavorite());
                table.repaint();
            }
        });
        table.setComponentPopupMenu(menu);
    }

    private JMenuItem styledItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setBackground(Theme.BG_CARD);
        item.setForeground(Theme.TEXT_PRIMARY);
        item.setFont(Theme.plain(13));
        item.setBorder(BorderFactory.createEmptyBorder(6,16,6,16));
        return item;
    }

    // ── Public loaders ────────────────────────────────────────────────────────

    public void loadAllSongs() {
        List<Song> all = MusicLibrary.getInstance().getAllSongs();
        setHeader("Library — " + all.size() + " songs");
        populate(all);
    }

    public void loadFavorites() {
        List<Song> favs = MusicLibrary.getInstance().getFavorites();
        setHeader("Favorites — " + favs.size() + " songs");
        populate(favs);
    }

    public void loadPlaylist(Playlist pl) {
        setHeader(pl.getName() + "  ·  " + pl.size() + " songs  ·  " + pl.getTotalDurationFormatted());
        populate(pl.getSongs());
    }

    public void loadQueue() {
        List<Song> q = PlayerController.getInstance().getQueue();
        setHeader("Queue — " + q.size() + " songs");
        populate(q);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void setHeader(String text) { headerLabel.setText(text); }

    private void populate(List<Song> songs) {
        currentSongs = songs;
        model.setRowCount(0);
        int i = 1;
        for (Song s : songs) {
            model.addRow(new Object[]{
                s.isFavorite(), String.valueOf(i++),
                s.getTitle(), s.getArtist(), s.getAlbum(),
                s.getGenre(), String.valueOf(s.getYear()), s.getFormattedDuration()
            });
        }
    }
    // ── HeartCellRenderer ────────────────────────────────────────────────────
    /**
     * Table cell renderer that draws a heart icon using pure Java2D — no font
     * symbols, works identically on all platforms.
     */
    private static class HeartCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {

            final boolean filled = Boolean.TRUE.equals(value);
            final Color bg = row % 2 == 0 ? Theme.BG_MEDIUM : new java.awt.Color(28, 28, 46);

            return new JComponent() {
                {
                    setBackground(bg);
                    setOpaque(true);
                }
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = UIComponents.aa(g);
                    g2.setColor(bg);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    int s  = 14;
                    int ox = (getWidth()  - s) / 2;
                    int oy = (getHeight() - s) / 2;
                    if (filled) {
                        g2.setColor(Theme.PINK);
                        IconRenderer.drawHeart(g2, ox, oy, s);
                    } else {
                        g2.setColor(Theme.TEXT_MUTED);
                        IconRenderer.drawHeartOutline(g2, ox, oy, s);
                    }
                    g2.dispose();
                }
            };
        }
    }

}
