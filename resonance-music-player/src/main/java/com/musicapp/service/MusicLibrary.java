package com.musicapp.service;

import com.musicapp.model.Playlist;
import com.musicapp.model.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Singleton service that acts as the central data store for songs and playlists.
 * In a production app this would delegate to a database or file-based persistence layer.
 */
public class MusicLibrary {

    private static final Logger log = LoggerFactory.getLogger(MusicLibrary.class);

    // Lazy-initialised singleton via inner holder class (thread-safe, no synchronisation cost)
    private static final class Holder {
        static final MusicLibrary INSTANCE = new MusicLibrary();
    }

    public static MusicLibrary getInstance() { return Holder.INSTANCE; }

    private final Map<String, Song>     songs     = new LinkedHashMap<>();
    private final Map<String, Playlist> playlists = new LinkedHashMap<>();

    private MusicLibrary() {
        loadSampleData();
        log.info("MusicLibrary initialised: {} songs, {} playlists", songs.size(), playlists.size());
    }

    // ── Seed data ──────────────────────────────────────────────────────────────

    private void loadSampleData() {
        Song[] seed = {
            new Song("1",  "Blinding Lights",        "The Weeknd",                "After Hours",               "Pop",         2020, dur(3,20)),
            new Song("2",  "Levitating",              "Dua Lipa",                  "Future Nostalgia",          "Pop",         2020, dur(3,23)),
            new Song("3",  "Bohemian Rhapsody",       "Queen",                     "A Night at the Opera",      "Rock",        1975, dur(5,55)),
            new Song("4",  "Hotel California",        "Eagles",                    "Hotel California",          "Rock",        1977, dur(6,30)),
            new Song("5",  "Shape of You",            "Ed Sheeran",                "Divide",                    "Pop",         2017, dur(3,53)),
            new Song("6",  "Uptown Funk",             "Mark Ronson ft. Bruno Mars","Uptown Special",            "Funk",        2014, dur(4,30)),
            new Song("7",  "Rolling in the Deep",     "Adele",                     "21",                        "Soul",        2010, dur(3,48)),
            new Song("8",  "Smells Like Teen Spirit", "Nirvana",                   "Nevermind",                 "Rock",        1991, dur(5,1)),
            new Song("9",  "Billie Jean",             "Michael Jackson",           "Thriller",                  "Pop",         1982, dur(4,54)),
            new Song("10", "Lose Yourself",           "Eminem",                    "8 Mile",                    "Hip-Hop",     2002, dur(5,26)),
            new Song("11", "Stairway to Heaven",      "Led Zeppelin",              "Led Zeppelin IV",           "Rock",        1971, dur(8,2)),
            new Song("12", "Watermelon Sugar",        "Harry Styles",              "Fine Line",                 "Pop",         2019, dur(2,54)),
            new Song("13", "Old Town Road",           "Lil Nas X",                 "7 EP",                      "Country Rap", 2019, dur(1,53)),
            new Song("14", "Bad Guy",                 "Billie Eilish",             "When We All Fall Asleep",   "Pop",         2019, dur(3,14)),
            new Song("15", "Humble",                  "Kendrick Lamar",            "DAMN.",                     "Hip-Hop",     2017, dur(2,57)),
            new Song("16", "Someone Like You",        "Adele",                     "21",                        "Soul",        2011, dur(4,45)),
            new Song("17", "Mr. Brightside",          "The Killers",               "Hot Fuss",                  "Indie Rock",  2003, dur(3,42)),
            new Song("18", "Superstition",            "Stevie Wonder",             "Talking Book",              "R&B",         1972, dur(4,26)),
            new Song("19", "Sweet Child O'Mine",      "Guns N' Roses",             "Appetite for Destruction",  "Rock",        1987, dur(5,56)),
            new Song("20", "Stay With Me",            "Sam Smith",                 "In the Lonely Hour",        "Pop",         2014, dur(2,52)),
        };

        for (Song s : seed) songs.put(s.getId(), s);

        // Favourites
        for (String id : new String[]{"3","6","9","11"}) songs.get(id).setFavorite(true);

        buildPlaylists();
    }

    private void buildPlaylists() {
        addPlaylist(playlistOf("p1", "Top Hits 2020",  "Best songs of 2020",            "#E91E63", "1","2","12","13","14"));
        addPlaylist(playlistOf("p2", "Rock Classics",  "Timeless rock anthems",          "#FF5722", "3","4","8","11","17","19"));
        addPlaylist(playlistOf("p3", "Hip-Hop Beats",  "Rap & hip-hop collection",       "#9C27B0", "10","15"));
        addPlaylist(playlistOf("p4", "Chill Vibes",    "Relaxing background music",      "#00BCD4", "16","20","7","5"));
    }

    private Playlist playlistOf(String id, String name, String desc, String color, String... ids) {
        Playlist p = new Playlist(id, name, desc, color);
        for (String sid : ids) {
            Song s = songs.get(sid);
            if (s != null) p.addSong(s);
        }
        return p;
    }

    private static Duration dur(int minutes, int seconds) {
        return Duration.ofMinutes(minutes).plusSeconds(seconds);
    }

    // ── Song CRUD ──────────────────────────────────────────────────────────────

    public List<Song> getAllSongs()   { return new ArrayList<>(songs.values()); }
    public Song getSong(String id)    { return songs.get(id); }
    public void addSong(Song song)    { songs.put(song.getId(), song); }
    public void removeSong(String id) { songs.remove(id); }

    public List<Song> getFavorites() {
        return songs.values().stream().filter(Song::isFavorite).collect(Collectors.toList());
    }

    /**
     * Case-insensitive search across title, artist, album, and genre.
     */
    public List<Song> search(String query) {
        if (query == null || query.isBlank()) return getAllSongs();
        String q = query.toLowerCase(Locale.ROOT);
        return songs.values().stream()
            .filter(s -> s.getTitle().toLowerCase(Locale.ROOT).contains(q)
                      || s.getArtist().toLowerCase(Locale.ROOT).contains(q)
                      || s.getAlbum().toLowerCase(Locale.ROOT).contains(q)
                      || s.getGenre().toLowerCase(Locale.ROOT).contains(q))
            .collect(Collectors.toList());
    }

    // ── Playlist CRUD ──────────────────────────────────────────────────────────

    public List<Playlist> getAllPlaylists()      { return new ArrayList<>(playlists.values()); }
    public Playlist getPlaylist(String id)       { return playlists.get(id); }
    public void addPlaylist(Playlist p)          { playlists.put(p.getId(), p); }
    public void removePlaylist(String id)        { playlists.remove(id); }

    // ── Meta queries ───────────────────────────────────────────────────────────

    public List<String> getAllGenres() {
        return songs.values().stream().map(Song::getGenre).distinct().sorted().collect(Collectors.toList());
    }

    public List<String> getAllArtists() {
        return songs.values().stream().map(Song::getArtist).distinct().sorted().collect(Collectors.toList());
    }
}
