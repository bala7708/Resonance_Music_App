package com.musicapp.model;

/** Possible states of the music player. */
public enum PlayerState {
    /** No song loaded / playback fully stopped. */
    STOPPED,
    /** A song is actively playing. */
    PLAYING,
    /** Playback is paused at a specific position. */
    PAUSED
}
