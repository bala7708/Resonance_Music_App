package com.musicapp.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Song model")
class SongTest {

    private Song song;

    @BeforeEach
    void setUp() {
        song = new Song("1", "Bohemian Rhapsody", "Queen", "A Night at the Opera",
                        "Rock", 1975, Duration.ofMinutes(5).plusSeconds(55));
    }

    @Test
    @DisplayName("Constructor sets all fields correctly")
    void constructorSetsFields() {
        assertEquals("1",                    song.getId());
        assertEquals("Bohemian Rhapsody",    song.getTitle());
        assertEquals("Queen",                song.getArtist());
        assertEquals("A Night at the Opera", song.getAlbum());
        assertEquals("Rock",                 song.getGenre());
        assertEquals(1975,                   song.getYear());
        assertFalse(song.isFavorite());
        assertEquals(0, song.getPlayCount());
    }

    @Test
    @DisplayName("incrementPlayCount increases count by 1 each call")
    void incrementPlayCount() {
        song.incrementPlayCount();
        song.incrementPlayCount();
        assertEquals(2, song.getPlayCount());
    }

    @Test
    @DisplayName("setFavorite toggles the favourite flag")
    void setFavorite() {
        assertFalse(song.isFavorite());
        song.setFavorite(true);
        assertTrue(song.isFavorite());
        song.setFavorite(false);
        assertFalse(song.isFavorite());
    }

    @ParameterizedTest
    @DisplayName("getFormattedDuration produces correct m:ss strings")
    @CsvSource({"1,0,1:00", "3,5,3:05", "10,59,10:59", "0,9,0:09"})
    void formattedDuration(int mins, int secs, String expected) {
        Song s = new Song("x","T","A","AL","G",2020, Duration.ofMinutes(mins).plusSeconds(secs));
        assertEquals(expected, s.getFormattedDuration());
    }

    @Test
    @DisplayName("equals is id-based")
    void equalsById() {
        Song same = new Song("1","Other","Other","Other","Other",2000, Duration.ZERO);
        Song diff = new Song("2","Bohemian Rhapsody","Queen","A Night at the Opera","Rock",1975,Duration.ofMinutes(5).plusSeconds(55));
        assertEquals(song, same);
        assertNotEquals(song, diff);
    }

    @Test
    @DisplayName("Null id throws NullPointerException")
    void nullIdThrows() {
        assertThrows(NullPointerException.class,
            () -> new Song(null, "T", "A", "AL", "G", 2020, Duration.ZERO));
    }
}
