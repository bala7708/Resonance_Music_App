package com.musicapp;

import com.musicapp.model.Playlist;
import com.musicapp.model.Song;
import com.musicapp.service.MusicLibrary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MusicLibrary service")
class MusicLibraryTest {

    private final MusicLibrary lib = MusicLibrary.getInstance();

    @Test
    @DisplayName("Singleton always returns the same instance")
    void singleton() {
        assertSame(lib, MusicLibrary.getInstance());
    }

    @Test
    @DisplayName("Seed data loads at least 20 songs")
    void seedSongs() {
        assertTrue(lib.getAllSongs().size() >= 20);
    }

    @Test
    @DisplayName("Seed data loads at least 4 playlists")
    void seedPlaylists() {
        assertTrue(lib.getAllPlaylists().size() >= 4);
    }

    @Test
    @DisplayName("search returns matching songs case-insensitively")
    void searchCaseInsensitive() {
        List<Song> results = lib.search("queen");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(s -> s.getArtist().equalsIgnoreCase("Queen")));
    }

    @Test
    @DisplayName("search with blank query returns all songs")
    void searchBlankReturnsAll() {
        assertEquals(lib.getAllSongs().size(), lib.search("").size());
    }

    @Test
    @DisplayName("getFavorites returns only favourite songs")
    void getFavoritesOnlyFavs() {
        List<Song> favs = lib.getFavorites();
        assertFalse(favs.isEmpty());
        assertTrue(favs.stream().allMatch(Song::isFavorite));
    }

    @Test
    @DisplayName("getAllGenres returns sorted distinct genres")
    void genres() {
        List<String> genres = lib.getAllGenres();
        assertFalse(genres.isEmpty());
        // Verify sorted
        for (int i = 0; i < genres.size() - 1; i++) {
            assertTrue(genres.get(i).compareTo(genres.get(i + 1)) <= 0);
        }
    }

    @Test
    @DisplayName("addPlaylist and removePlaylist work correctly")
    void addRemovePlaylist() {
        int before = lib.getAllPlaylists().size();
        Playlist pl = new Playlist("test-pl","Test","desc","#fff");
        lib.addPlaylist(pl);
        assertEquals(before + 1, lib.getAllPlaylists().size());
        assertSame(pl, lib.getPlaylist("test-pl"));

        lib.removePlaylist("test-pl");
        assertEquals(before, lib.getAllPlaylists().size());
        assertNull(lib.getPlaylist("test-pl"));
    }
}
