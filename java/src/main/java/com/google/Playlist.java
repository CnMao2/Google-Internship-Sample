package com.google;

import java.util.ArrayList;
import java.util.List;

/** A class used to represent a Playlist */
class Playlist {
    private String name;
    private List<Video> videos;

    Playlist(String name) {
        this.name = name;
        this.videos = new ArrayList<>();
    }

    public void addVideo(Video video) {
        videos.add(video);
    }

    public void removeVideo(Video video) {
        videos.remove(video);
    }

    public String getPlaylistName() {
        return this.name;
    }

    public List<Video> getVideosInPlaylist() {
        return this.videos;
    }
}
