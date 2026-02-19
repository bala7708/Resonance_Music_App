package com.musicapp.ui;

import com.musicapp.controller.PlayerController;
import com.musicapp.model.PlayerState;
import com.musicapp.model.RepeatMode;
import com.musicapp.model.Song;
import com.musicapp.util.Theme;
import com.musicapp.ui.UIComponents.*;

import javax.swing.*;
import java.awt.*;

/**
 * Bottom player bar — album art, song info, transport controls, seek bar,
 * shuffle/repeat toggles, and volume slider.
 *
 * <p>All icons are rendered as pure Java2D vector shapes via
 * {@link com.musicapp.util.IconRenderer} — no symbol/emoji fonts required.</p>
 */
public class PlayerBar extends JPanel {

    private final PlayerController player = PlayerController.getInstance();

    // Left zone
    private AlbumArtPanel  albumArt;
    private JLabel         titleLabel;
    private JLabel         artistLabel;
    private VectorLabel    favIcon;

    // Centre zone
    private IconButton     shuffleBtn, prevBtn, playBtn, nextBtn, repeatBtn;
    private ProgressSlider seekBar;
    private JLabel         timeElapsed, timeTotal;
    private VisualizerBar  visualizer;

    // Right zone
    private ProgressSlider volumeSlider;
    private VectorLabel    volIcon;

    public PlayerBar() {
        setBackground(Theme.BG_DARKEST);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER_ACCENT));
        setPreferredSize(new Dimension(0, 92));
        setLayout(new GridBagLayout());
        buildUI();
        wireListeners();
    }

    // ── Construction ──────────────────────────────────────────────────────────

    private void buildUI() {
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH;

        g.gridx = 0; g.weightx = 0.28; add(buildLeft(),   g);
        g.gridx = 1; g.weightx = 0.44; add(buildCenter(), g);
        g.gridx = 2; g.weightx = 0.28; add(buildRight(),  g);
    }

    private JPanel buildLeft() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        p.setBackground(Theme.BG_DARKEST);
        p.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        albumArt = new AlbumArtPanel(Theme.ACCENT, Theme.PINK);
        albumArt.setPreferredSize(new Dimension(56, 56));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Theme.BG_DARKEST);

        titleLabel = new JLabel("Not Playing");
        titleLabel.setFont(Theme.bold(14));
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        artistLabel = new JLabel("—");
        artistLabel.setFont(Theme.plain(12));
        artistLabel.setForeground(Theme.TEXT_SECONDARY);

        info.add(titleLabel);
        info.add(Box.createVerticalStrut(3));
        info.add(artistLabel);

        // Vector heart icon — no font dependency
        favIcon = new VectorLabel(PlayerIcon.HEART_OUTLINE, 18, Theme.TEXT_MUTED);
        favIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        favIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { player.toggleFavorite(); }
        });

        p.add(albumArt);
        p.add(info);
        p.add(Box.createHorizontalStrut(8));
        p.add(favIcon);
        return p;
    }

    private JPanel buildCenter() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Theme.BG_DARKEST);
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // Transport row
        JPanel transport = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        transport.setBackground(Theme.BG_DARKEST);

        shuffleBtn = new IconButton(PlayerIcon.SHUFFLE,     32);
        prevBtn    = new IconButton(PlayerIcon.PREV,        36);
        playBtn    = new IconButton(PlayerIcon.PLAY,        42);
        nextBtn    = new IconButton(PlayerIcon.NEXT,        36);
        repeatBtn  = new IconButton(PlayerIcon.REPEAT_ALL,  32);

        playBtn.addActionListener(e -> player.togglePlayPause());
        prevBtn.addActionListener(e -> player.previous());
        nextBtn.addActionListener(e -> player.next());
        shuffleBtn.addActionListener(e -> {
            player.toggleShuffle();
            shuffleBtn.setActive(player.isShuffle());
        });
        repeatBtn.addActionListener(e -> {
            player.cycleRepeat();
            updateRepeatBtn();
        });

        visualizer = new VisualizerBar(10, Theme.ACCENT_LIGHT);
        visualizer.setPreferredSize(new Dimension(50, 24));

        transport.add(shuffleBtn);
        transport.add(prevBtn);
        transport.add(playBtn);
        transport.add(nextBtn);
        transport.add(repeatBtn);
        transport.add(Box.createHorizontalStrut(8));
        transport.add(visualizer);

        // Seek bar row
        JPanel seekRow = new JPanel(new BorderLayout(8, 0));
        seekRow.setBackground(Theme.BG_DARKEST);
        seekRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));

        timeElapsed = timeLabel("0:00");
        timeTotal   = timeLabel("0:00");
        seekBar = new ProgressSlider(Theme.PROGRESS_BG, Theme.PROGRESS_FG, Color.WHITE);
        seekBar.setSeekConsumer(player::seek);

        seekRow.add(timeElapsed, BorderLayout.WEST);
        seekRow.add(seekBar,     BorderLayout.CENTER);
        seekRow.add(timeTotal,   BorderLayout.EAST);

        p.add(transport);
        p.add(Box.createVerticalStrut(6));
        p.add(seekRow);
        return p;
    }

    private JPanel buildRight() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        p.setBackground(Theme.BG_DARKEST);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 24));

        // Vector volume icon — no emoji font needed
        volIcon = new VectorLabel(PlayerIcon.VOLUME_HIGH, 18, Theme.TEXT_SECONDARY);

        volumeSlider = new ProgressSlider(Theme.PROGRESS_BG, Theme.VOLUME_FG, Theme.CYAN);
        volumeSlider.setPreferredSize(new Dimension(100, 20));
        volumeSlider.setValue(player.getVolume() / 100.0);
        volumeSlider.setSeekConsumer(v -> player.setVolume((int)(v * 100)));

        p.add(volIcon);
        p.add(volumeSlider);
        return p;
    }

    // ── Listeners ─────────────────────────────────────────────────────────────

    private void wireListeners() {
        player.addStateListener(state -> SwingUtilities.invokeLater(() -> {
            boolean playing = state == PlayerState.PLAYING;
            playBtn.setIconType(playing ? PlayerIcon.PAUSE : PlayerIcon.PLAY);
            albumArt.setPlaying(playing);
            visualizer.setActive(playing);
        }));

        player.addSongListener(song -> SwingUtilities.invokeLater(() -> {
            if (song == null) return;
            titleLabel.setText(song.getTitle());
            artistLabel.setText(song.getArtist());
            timeTotal.setText(song.getFormattedDuration());

            // Swap heart icon based on favourite state
            favIcon.setIconType(song.isFavorite() ? PlayerIcon.HEART_FILLED : PlayerIcon.HEART_OUTLINE);
            favIcon.setColor(song.isFavorite() ? Theme.PINK : Theme.TEXT_MUTED);

            // Assign random disc colours
            Color[] cols = Theme.PLAYLIST_COLORS;
            albumArt.setColors(
                cols[(int)(Math.random() * cols.length)],
                cols[(int)(Math.random() * cols.length)],
                song.getTitle().substring(0, Math.min(2, song.getTitle().length())).toUpperCase());
        }));

        player.addProgressListener(progress -> SwingUtilities.invokeLater(() -> {
            seekBar.setValue(progress);
            Song s = player.getCurrentSong();
            if (s != null) {
                long sec = (long)(progress * s.getDuration().getSeconds());
                timeElapsed.setText(String.format("%d:%02d", sec / 60, sec % 60));
            }
        }));

        player.addVolumeListener(vol -> SwingUtilities.invokeLater(() -> {
            volumeSlider.setValue(vol / 100.0);
            // Swap volume icon based on level
            if (vol == 0) {
                volIcon.setIconType(PlayerIcon.VOLUME_MUTE);
            } else if (vol < 50) {
                volIcon.setIconType(PlayerIcon.VOLUME_MID);
            } else {
                volIcon.setIconType(PlayerIcon.VOLUME_HIGH);
            }
        }));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void updateRepeatBtn() {
        RepeatMode m = player.getRepeatMode();
        repeatBtn.setActive(m != RepeatMode.NONE);
        repeatBtn.setIconType(m == RepeatMode.ONE ? PlayerIcon.REPEAT_ONE : PlayerIcon.REPEAT_ALL);
    }

    private static JLabel timeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.mono(11));
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }
}
