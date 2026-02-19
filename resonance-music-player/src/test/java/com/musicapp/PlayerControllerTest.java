package com.musicapp;

import com.musicapp.controller.PlayerController;
import com.musicapp.model.*;
import com.musicapp.service.MusicLibrary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PlayerController")
class PlayerControllerTest {

    private final PlayerController player = PlayerController.getInstance();
    private List<Song> songs;

    @BeforeEach
    void setUp() {
        songs = MusicLibrary.getInstance().getAllSongs();
        player.stop();
    }

    @Test
    @DisplayName("Singleton returns same instance")
    void singleton() {
        assertSame(player, PlayerController.getInstance());
    }

    @Test
    @DisplayName("play() sets PLAYING state and correct current song")
    void playSetsSong() {
        Song s = songs.get(0);
        player.play(s, songs);
        assertEquals(PlayerState.PLAYING, player.getPlayerState());
        assertEquals(s, player.getCurrentSong());
    }

    @Test
    @DisplayName("pause() transitions PLAYING → PAUSED")
    void pauseTransition() {
        player.play(songs.get(0), songs);
        player.pause();
        assertEquals(PlayerState.PAUSED, player.getPlayerState());
    }

    @Test
    @DisplayName("resume() transitions PAUSED → PLAYING")
    void resumeTransition() {
        player.play(songs.get(0), songs);
        player.pause();
        player.resume();
        assertEquals(PlayerState.PLAYING, player.getPlayerState());
    }

    @Test
    @DisplayName("togglePlayPause cycles between PLAYING and PAUSED")
    void togglePlayPause() {
        player.play(songs.get(0), songs);
        assertEquals(PlayerState.PLAYING, player.getPlayerState());
        player.togglePlayPause();
        assertEquals(PlayerState.PAUSED, player.getPlayerState());
        player.togglePlayPause();
        assertEquals(PlayerState.PLAYING, player.getPlayerState());
    }

    @Test
    @DisplayName("stop() resets progress to zero and state to STOPPED")
    void stopResetsState() {
        player.play(songs.get(0), songs);
        player.stop();
        assertEquals(PlayerState.STOPPED, player.getPlayerState());
        assertEquals(0.0, player.getProgress(), 1e-9);
    }

    @Test
    @DisplayName("next() advances queue index by 1")
    void nextAdvancesIndex() {
        player.play(songs.get(0), songs);
        int before = player.getQueueIndex();
        player.next();
        assertEquals(before + 1, player.getQueueIndex());
    }

    @Test
    @DisplayName("seek() clamps progress to [0,1]")
    void seekClamps() {
        player.play(songs.get(0), songs);
        player.seek(-5.0);
        assertEquals(0.0, player.getProgress(), 1e-9);
        player.seek(100.0);
        assertEquals(1.0, player.getProgress(), 1e-9);
    }

    @Test
    @DisplayName("setVolume() clamps to [0,100]")
    void volumeClamps() {
        player.setVolume(-10);
        assertEquals(0, player.getVolume());
        player.setVolume(200);
        assertEquals(100, player.getVolume());
        player.setVolume(50);
        assertEquals(50, player.getVolume());
    }

    @Test
    @DisplayName("cycleRepeat() cycles NONE → ALL → ONE → NONE")
    void cycleRepeat() {
        // ensure we start from NONE
        while (player.getRepeatMode() != RepeatMode.NONE) player.cycleRepeat();
        player.cycleRepeat(); assertEquals(RepeatMode.ALL, player.getRepeatMode());
        player.cycleRepeat(); assertEquals(RepeatMode.ONE, player.getRepeatMode());
        player.cycleRepeat(); assertEquals(RepeatMode.NONE, player.getRepeatMode());
    }

    @Test
    @DisplayName("toggleShuffle() flips the shuffle flag")
    void toggleShuffle() {
        boolean initial = player.isShuffle();
        player.toggleShuffle();
        assertNotEquals(initial, player.isShuffle());
        player.toggleShuffle();
        assertEquals(initial, player.isShuffle());
    }

    @Test
    @DisplayName("toggleFavorite() toggles current song's favourite flag")
    void toggleFavorite() {
        Song s = songs.get(0);
        boolean before = s.isFavorite();
        player.play(s, songs);
        player.toggleFavorite();
        assertNotEquals(before, s.isFavorite());
        player.toggleFavorite();
        assertEquals(before, s.isFavorite());
    }

    @Test
    @DisplayName("play() increments the song's play count")
    void playIncrementsCount() {
        Song s = songs.get(3);
        int before = s.getPlayCount();
        player.play(s, songs);
        assertEquals(before + 1, s.getPlayCount());
    }
}
