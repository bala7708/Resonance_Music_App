package com.musicapp.controller;

import com.musicapp.model.*;
import com.musicapp.service.MusicLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * Singleton controller that owns all playback logic.
 *
 * <p>Observer lists (stateListeners, songListeners, etc.) allow UI panels to
 * react to state changes without any direct coupling.  All listener callbacks
 * are invoked on the same thread that triggered the change; callers that need
 * Swing-thread safety must wrap with {@code SwingUtilities.invokeLater}.</p>
 */
public class PlayerController {

    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

    // ── Singleton ──────────────────────────────────────────────────────────────

    private static final class Holder {
        static final PlayerController INSTANCE = new PlayerController();
    }

    public static PlayerController getInstance() { return Holder.INSTANCE; }

    // ── State ──────────────────────────────────────────────────────────────────

    private Song              currentSong;
    private final List<Song>  queue      = new ArrayList<>();
    private int               queueIndex = -1;

    private PlayerState playerState = PlayerState.STOPPED;
    private RepeatMode  repeatMode  = RepeatMode.NONE;
    private boolean     shuffle     = false;
    private int         volume      = 70;       // 0–100
    private double      progress    = 0.0;      // 0.0–1.0
    private double      currentSec  = 0.0;      // seconds elapsed

    private Timer progressTimer;

    // ── Observer lists ─────────────────────────────────────────────────────────

    private final List<Consumer<PlayerState>> stateListeners    = new ArrayList<>();
    private final List<Consumer<Song>>        songListeners     = new ArrayList<>();
    private final List<Consumer<Double>>      progressListeners = new ArrayList<>();
    private final List<Consumer<Integer>>     volumeListeners   = new ArrayList<>();

    private PlayerController() { /* private */ }

    // ── Public API ─────────────────────────────────────────────────────────────

    /** Play {@code song} using {@code newQueue} as the surrounding context. */
    public void play(Song song, List<Song> newQueue) {
        Objects.requireNonNull(song, "song must not be null");
        stopTimer();
        queue.clear();
        queue.addAll(newQueue);
        queueIndex = queue.indexOf(song);
        if (queueIndex < 0) {
            queue.add(0, song);
            queueIndex = 0;
        }
        playCurrent();
    }

    /** Play {@code song} using the full library as the queue. */
    public void play(Song song) {
        if (queue.isEmpty()) queue.addAll(MusicLibrary.getInstance().getAllSongs());
        play(song, queue);
    }

    public void pause() {
        if (playerState == PlayerState.PLAYING) {
            playerState = PlayerState.PAUSED;
            stopTimer();
            notifyState();
            log.debug("Paused: {}", currentSong);
        }
    }

    public void resume() {
        if (playerState == PlayerState.PAUSED) {
            playerState = PlayerState.PLAYING;
            startTimer();
            notifyState();
            log.debug("Resumed: {}", currentSong);
        }
    }

    public void togglePlayPause() {
        switch (playerState) {
            case PLAYING: pause(); break;
            case PAUSED:  resume(); break;
            case STOPPED:
                if (currentSong != null) { playerState = PlayerState.PLAYING; startTimer(); notifyState(); }
                break;
        }
    }

    public void stop() {
        stopTimer();
        playerState = PlayerState.STOPPED;
        progress = 0; currentSec = 0;
        notifyState(); notifyProgress();
        log.debug("Stopped");
    }

    public void next() {
        if (queue.isEmpty()) return;
        queueIndex = shuffle
            ? randomIndex()
            : (queueIndex + 1) % queue.size();
        playCurrent();
    }

    public void previous() {
        if (queue.isEmpty()) return;
        if (currentSec > 3.0) { seek(0); return; }
        queueIndex = (queueIndex - 1 + queue.size()) % queue.size();
        playCurrent();
    }

    /** Seek to a fractional position (0.0 – 1.0). */
    public void seek(double fraction) {
        if (currentSong == null) return;
        progress   = Math.max(0, Math.min(1, fraction));
        currentSec = progress * currentSong.getDuration().getSeconds();
        notifyProgress();
    }

    public void setVolume(int vol) {
        volume = Math.max(0, Math.min(100, vol));
        notifyVolume();
    }

    public void toggleShuffle() { shuffle = !shuffle; log.debug("Shuffle: {}", shuffle); }

	public void cycleRepeat() {
		/*
		 * repeatMode = switch (repeatMode) { case NONE -> RepeatMode.ALL; case ALL ->
		 * RepeatMode.ONE; case ONE -> RepeatMode.NONE; }; log.debug("RepeatMode: {}",
		 * repeatMode);
		 */}

    public void toggleFavorite() {
        if (currentSong != null) {
            currentSong.setFavorite(!currentSong.isFavorite());
            notifySong();
        }
    }

    // ── Observer registration ──────────────────────────────────────────────────

    public void addStateListener(Consumer<PlayerState> l)    { stateListeners.add(l); }
    public void addSongListener(Consumer<Song> l)            { songListeners.add(l); }
    public void addProgressListener(Consumer<Double> l)      { progressListeners.add(l); }
    public void addVolumeListener(Consumer<Integer> l)       { volumeListeners.add(l); }

    // ── Getters ────────────────────────────────────────────────────────────────

    public Song        getCurrentSong()  { return currentSong; }
    public PlayerState getPlayerState()  { return playerState; }
    public RepeatMode  getRepeatMode()   { return repeatMode; }
    public boolean     isShuffle()       { return shuffle; }
    public int         getVolume()       { return volume; }
    public double      getProgress()     { return progress; }
    public double      getCurrentSec()   { return currentSec; }
    public List<Song>  getQueue()        { return Collections.unmodifiableList(queue); }
    public int         getQueueIndex()   { return queueIndex; }

    // ── Internal helpers ───────────────────────────────────────────────────────

    private void playCurrent() {
        stopTimer();
        currentSong = queue.get(queueIndex);
        progress = 0; currentSec = 0;
        currentSong.incrementPlayCount();
        playerState = PlayerState.PLAYING;
        notifySong(); notifyState();
        startTimer();
        log.info("Now playing: {}", currentSong);
    }

    private void startTimer() {
        progressTimer = new Timer("player-tick", true);
        progressTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (playerState != PlayerState.PLAYING || currentSong == null) return;
                double total = currentSong.getDuration().getSeconds();
                currentSec += 0.5;
                progress = currentSec / total;
                if (progress >= 1.0) {
                    progress = 1.0; notifyProgress();
                    onSongEnd();
                } else {
                    notifyProgress();
                }
            }
        }, 500, 500);
    }

    private void stopTimer() {
        if (progressTimer != null) { progressTimer.cancel(); progressTimer = null; }
    }

    private void onSongEnd() {
        stopTimer();
        switch (repeatMode) {
            case ONE:
                progress = 0; currentSec = 0;
                playerState = PlayerState.PLAYING;
                startTimer();
                break;
            case ALL:
                next();
                break;
            case NONE:
                if (queueIndex < queue.size() - 1) next();
                else { playerState = PlayerState.STOPPED; notifyState(); }
                break;
        }
    }

    private int randomIndex() {
        if (queue.size() <= 1) return 0;
        int idx;
        do { idx = (int)(Math.random() * queue.size()); } while (idx == queueIndex);
        return idx;
    }

    private void notifyState()    { stateListeners.forEach(l -> l.accept(playerState)); }
    private void notifySong()     { songListeners.forEach(l -> l.accept(currentSong)); }
    private void notifyProgress() { progressListeners.forEach(l -> l.accept(progress)); }
    private void notifyVolume()   { volumeListeners.forEach(l -> l.accept(volume)); }
}
