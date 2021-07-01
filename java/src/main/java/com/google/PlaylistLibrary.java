package com.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** A class used to represent a Playlist Library. */
public class PlaylistLibrary {
    // playlist name as key, Playlist as value
    private final HashMap<String, Playlist> playlist_map;

    PlaylistLibrary() {
        playlist_map = new HashMap<>();
    }

    public void addToLibrary(Playlist pl) {
        playlist_map.put(pl.getPlaylistName(), pl);
    }

    public void removeFromLibrary(Playlist pl) {
        playlist_map.remove(pl.getPlaylistName(), pl);
    }

    public List<Playlist> getPlaylists() {
        return new ArrayList<>(this.playlist_map.values());
    }

    public Playlist getPlaylist(String playlistName) {
        return this.playlist_map.get(playlistName);
    }

    public List<String> getPlaylistNames() {
        return new ArrayList<>(this.playlist_map.keySet());
    }

}
