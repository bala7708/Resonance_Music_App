package com.musicapp.model;

import java.util.*;

/**
 * A named, ordered collection of {@link Song} objects.
 */
public class Playlist {

    private final String id;
    private String name;
    private String description;
    private final List<Song> songs;
    private final String coverHexColor;

    public Playlist(String id, String name, String description, String coverHexColor) {
        this.id            = Objects.requireNonNull(id);
        this.name          = Objects.requireNonNull(name);
        this.description   = description != null ? description : "";
        this.coverHexColor = coverHexColor != null ? coverHexColor : "#9C27B0";
        this.songs         = new ArrayList<>();
    }

    // ── Mutation ───────────────────────────────────────────────────────────────

    public void addSong(Song song) {
        if (song != null && !songs.contains(song)) songs.add(song);
    }

    public void removeSong(Song song)  { songs.remove(song); }
    public void removeSong(int index)  { if (index >= 0 && index < songs.size()) songs.remove(index); }

    public void moveSong(int from, int to) {
        if (from >= 0 && from < songs.size() && to >= 0 && to < songs.size()) {
            songs.add(to, songs.remove(from));
        }
    }

    // ── Query ──────────────────────────────────────────────────────────────────

    public Song getSong(int index) {
        return (index >= 0 && index < songs.size()) ? songs.get(index) : null;
    }

    public List<Song> getSongs()         { return Collections.unmodifiableList(songs); }
    public int        size()             { return songs.size(); }
    public boolean    isEmpty()          { return songs.isEmpty(); }
    public boolean    contains(Song s)   { return songs.contains(s); }

    /** Total duration formatted as "X hr Y min" or "Y min". */
    public String getTotalDurationFormatted() {
        long totalSecs = songs.stream().mapToLong(s -> s.getDuration().getSeconds()).sum();
        long hours = totalSecs / 3600;
        long mins  = (totalSecs % 3600) / 60;
        return hours > 0
            ? String.format("%d hr %d min", hours, mins)
            : String.format("%d min", mins);
    }

    // ── Getters / setters ──────────────────────────────────────────────────────

    public String getId()             { return id; }
    public String getName()           { return name; }
    public String getDescription()    { return description; }
    public String getCoverHexColor()  { return coverHexColor; }

    public void setName(String name)              { this.name = Objects.requireNonNull(name); }
    public void setDescription(String description){ this.description = description; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist)) return false;
        return id.equals(((Playlist) o).id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() { return name; }
}
