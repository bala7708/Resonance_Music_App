package com.musicapp.model;

import java.time.Duration;
import java.util.Objects;

/**
 * Immutable-ish domain object representing a single music track.
 */
public class Song {

    private final String id;
    private final String title;
    private final String artist;
    private final String album;
    private final String genre;
    private final int year;
    private final Duration duration;
    private String filePath;
    private boolean favorite;
    private int playCount;

    public Song(String id, String title, String artist, String album,
                String genre, int year, Duration duration) {
        this.id       = Objects.requireNonNull(id, "id must not be null");
        this.title    = Objects.requireNonNull(title, "title must not be null");
        this.artist   = Objects.requireNonNull(artist, "artist must not be null");
        this.album    = album  != null ? album  : "Unknown Album";
        this.genre    = genre  != null ? genre  : "Unknown Genre";
        this.year     = year;
        this.duration = Objects.requireNonNull(duration, "duration must not be null");
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    public String getId()       { return id; }
    public String getTitle()    { return title; }
    public String getArtist()   { return artist; }
    public String getAlbum()    { return album; }
    public String getGenre()    { return genre; }
    public int    getYear()     { return year; }
    public Duration getDuration() { return duration; }
    public String getFilePath() { return filePath; }
    public boolean isFavorite() { return favorite; }
    public int getPlayCount()   { return playCount; }

    // ── Mutators ───────────────────────────────────────────────────────────────

    public void setFilePath(String filePath) { this.filePath = filePath; }
    public void setFavorite(boolean favorite) { this.favorite = favorite; }
    public void incrementPlayCount()          { this.playCount++; }

    // ── Helpers ────────────────────────────────────────────────────────────────

    /**
     * Returns duration formatted as m:ss (e.g. "3:45").
     */
    public String getFormattedDuration() {
        long mins = duration.toMinutes();
        long secs = duration.toSecondsPart();
        return String.format("%d:%02d", mins, secs);
    }

    // ── Object overrides ───────────────────────────────────────────────────────

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        return id.equals(((Song) o).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() { return title + " — " + artist; }
}
