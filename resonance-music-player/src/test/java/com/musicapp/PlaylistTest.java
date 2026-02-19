package com.musicapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Playlist model")
class PlaylistTest {

    private Playlist playlist;
    private Song     song1, song2, song3;

    @BeforeEach
    void setUp() {
        playlist = new Playlist("p1", "My Playlist", "A test playlist", "#E91E63");
        song1 = new Song("1","Song One",  "Artist A","Album","Pop",2020,Duration.ofMinutes(3).plusSeconds(30));
        song2 = new Song("2","Song Two",  "Artist B","Album","Rock",2019,Duration.ofMinutes(4));
        song3 = new Song("3","Song Three","Artist C","Album","Jazz",2018,Duration.ofMinutes(2).plusSeconds(10));
    }

    @Test
    @DisplayName("addSong appends song and size grows")
    void addSong() {
        assertTrue(playlist.isEmpty());
        playlist.addSong(song1);
        assertEquals(1, playlist.size());
        assertTrue(playlist.contains(song1));
    }

    @Test
    @DisplayName("addSong does not add duplicates")
    void addSongNoDuplicate() {
        playlist.addSong(song1);
        playlist.addSong(song1);
        assertEquals(1, playlist.size());
    }

    @Test
    @DisplayName("removeSong by reference shrinks list")
    void removeSongByRef() {
        playlist.addSong(song1);
        playlist.addSong(song2);
        playlist.removeSong(song1);
        assertEquals(1, playlist.size());
        assertFalse(playlist.contains(song1));
    }

    @Test
    @DisplayName("moveSong reorders correctly")
    void moveSong() {
        playlist.addSong(song1); playlist.addSong(song2); playlist.addSong(song3);
        playlist.moveSong(0, 2);   // move song1 to end
        assertEquals(song2, playlist.getSong(0));
        assertEquals(song3, playlist.getSong(1));
        assertEquals(song1, playlist.getSong(2));
    }

    @Test
    @DisplayName("getTotalDurationFormatted returns correct string")
    void totalDuration() {
        playlist.addSong(song1); // 3:30
        playlist.addSong(song2); // 4:00
        playlist.addSong(song3); // 2:10
        // total = 9 min 40 sec → "9 min"
        assertTrue(playlist.getTotalDurationFormatted().contains("9"));
    }

    @Test
    @DisplayName("getSongs returns unmodifiable view")
    void getSongsUnmodifiable() {
        playlist.addSong(song1);
        assertThrows(UnsupportedOperationException.class,
            () -> playlist.getSongs().add(song2));
    }

    @Test
    @DisplayName("equals is id-based")
    void equalsById() {
        Playlist same = new Playlist("p1","Different","Desc","#000000");
        Playlist diff = new Playlist("p2","My Playlist","A test playlist","#E91E63");
        assertEquals(playlist, same);
        assertNotEquals(playlist, diff);
    }
}
