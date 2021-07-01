package com.google;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.codehaus.plexus.util.StringUtils;

public class VideoPlayer {

    private final VideoLibrary videoLibrary;
    private List<String> videoIds = new ArrayList<>();

    private Video currentlyPlayingVideo = null;
    private boolean pause_flag = false;

    private PlaylistLibrary playlistLibrary = new PlaylistLibrary();
    private List<Playlist> playlists = playlistLibrary.getPlaylists();
    private List<String> playlistNames = playlistLibrary.getPlaylistNames();

    private List<String> videoTitles = new ArrayList<>();

    List<Video> videosFlagged = new ArrayList<>();
    String default_reason = "Not supplied";

    public VideoPlayer() {
        this.videoLibrary = new VideoLibrary();
        for (Video video : this.videoLibrary.getVideos()) {
            this.videoIds.add(video.getVideoId());
            this.videoTitles.add(video.getTitle());
        }
    }

    public void numberOfVideos() {
        System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
    }

    public void showAllVideos() {
        System.out.println("Here's a list of all available videos:");
        List<Video> sortedList = videoLibrary.getVideos().stream().sorted().collect(Collectors.toList());
        for (Video video : sortedList) {
            System.out.println(video.toString());
        }
    }

    /**
     * Use a class attribute currentlyPlayingVideo to manage the currently playing
     * video.
     * 
     * @param videoId
     */
    public void playVideo(String videoId) {
        if (videoId == null)
            return;
        Video video = videoLibrary.getVideo(videoId);
        if (video == null) {
            System.out.println("Cannot play video: Video does not exist");
            return;
        } else if (video.getFlagInfo()) {
            System.out.println("Cannot play video: Video is currently flagged (reason: " + video.getFlagReason() + ")");
            return;
        }
        if (currentlyPlayingVideo != null)
            System.out.println("Stopping video: " + currentlyPlayingVideo.getTitle());

        currentlyPlayingVideo = videoLibrary.getVideo(videoId);
        System.out.println("Playing video: " + currentlyPlayingVideo.getTitle());
        pause_flag = false;
    }

    public void stopVideo() {
        if (currentlyPlayingVideo == null)
            System.out.println("Cannot stop video: No video is currently playing");
        else {
            System.out.println("Stopping video: " + currentlyPlayingVideo.getTitle());
            currentlyPlayingVideo = null;
        }
    }

    /**
     * Flagged videos will not be played here by excluding them out of the scope.
     */
    public void playRandomVideo() {
        List<Video> videosUnflagged = new ArrayList<>(videoLibrary.getVideos());
        videosUnflagged.removeAll(videosFlagged);
        int videosNumber;
        if ((videosNumber = videosUnflagged.size()) == 0) {
            System.out.println("No videos available");
            return;
        }
        int randomVideoNum = (int) (Math.random() * videosNumber);
        if (currentlyPlayingVideo != null)
            System.out.println("Stopping video: " + currentlyPlayingVideo.getTitle());
        currentlyPlayingVideo = videosUnflagged.get(randomVideoNum);
        pause_flag = false;
        System.out.println("Playing video: " + currentlyPlayingVideo.getTitle());
    }

    /**
     * Attribute pause_flag is used for maintaining the pause information.
     */
    public void pauseVideo() {
        if (currentlyPlayingVideo == null) {
            System.out.println("Cannot pause video: No video is currently playing");
            return;
        }
        if (!pause_flag) {
            pause_flag = true;
            System.out.println("Pausing video: " + currentlyPlayingVideo.getTitle());
        } else
            System.out.println("Video already paused: " + currentlyPlayingVideo.getTitle());
    }

    /**
     * Reversed implementation from the method pauseVideo().
     */
    public void continueVideo() {
        if (currentlyPlayingVideo == null) {
            System.out.println("Cannot continue video: No video is currently playing");
            return;
        }
        if (pause_flag) {
            System.out.println("Continuing video: " + currentlyPlayingVideo.getTitle());
            pause_flag = false;
        } else
            System.out.println("Cannot continue video: Video is not paused");
    }

    public void showPlaying() {
        if (currentlyPlayingVideo == null) {
            System.out.println("No video is currently playing");
            return;
        }
        if (!pause_flag)
            System.out.println("Currently playing: " + currentlyPlayingVideo.toString());
        else
            System.out.println("Currently playing: " + currentlyPlayingVideo.toString() + " - PAUSED");
    }

    public void createPlaylist(String playlistName) {
        if (playlistName == null)
            return;

        if (playlistNames.stream().anyMatch(playlistName::equalsIgnoreCase)) {
            System.out.println("Cannot create playlist: A playlist with the same name already exists");
        } else {
            Playlist pl = new Playlist(playlistName);
            playlistLibrary.addToLibrary(pl);
            playlists.add(pl);
            playlistNames.add(pl.getPlaylistName());
            System.out.println("Successfully created new playlist: " + playlistName);
        }
    }

    public void addVideoToPlaylist(String playlistName, String videoId) {
        if (playlistName == null || videoId == null)
            return;
        boolean pl_match = playlistNames.stream().anyMatch(playlistName::equalsIgnoreCase);
        if (!pl_match) {
            System.out.println("Cannot add video to " + playlistName + ": Playlist does not exist");
            return;
        }
        if (!videoIds.contains(videoId)) {
            System.out.println("Cannot add video to " + playlistName + ": Video does not exist");
            return;
        }
        Video video = videoLibrary.getVideo(videoId);
        if (video.getFlagInfo()) {
            System.out.println("Cannot add video to " + playlistName + ": Video is currently flagged (reason: "
                    + video.getFlagReason() + ")");
            return;
        }
        for (String plName : playlistNames) {
            if (plName.equalsIgnoreCase(playlistName)) {
                Playlist pl = playlistLibrary.getPlaylist(plName);
                if (!pl.getVideosInPlaylist().contains(video)) {
                    pl.addVideo(video);
                    System.out.println("Added video to " + playlistName + ": " + video.getTitle());
                } else
                    System.out.println("Cannot add video to " + playlistName + ": Video already added");
            }
        }
    }

    public void showAllPlaylists() {
        if (playlists.isEmpty())
            System.out.println("No playlists exist yet");
        else {
            List<String> sortedPlaylistNames = playlistNames.stream().sorted().collect(Collectors.toList());
            System.out.println("Showing all playlists:");
            for (String plName : sortedPlaylistNames) {
                System.out.println(plName);
            }
        }
    }

    public void showPlaylist(String playlistName) {
        if (playlistName == null)
            return;
        boolean match_flag = false;
        for (Playlist playlist : playlists) {
            if (playlist.getPlaylistName().equalsIgnoreCase(playlistName)) {
                match_flag = true;
                List<Video> videos = playlist.getVideosInPlaylist();
                System.out.println("Showing playlist: " + playlistName);
                if (videos.size() > 0) {
                    for (Video video : videos)
                        System.out.println(video.toString());
                } else {
                    System.out.println("No videos here yet");
                }
            }
        }
        if (!match_flag)
            System.out.println("Cannot show playlist " + playlistName + ": Playlist does not exist");
    }

    public void removeFromPlaylist(String playlistName, String videoId) {
        if (playlistName == null || videoId == null)
            return;
        boolean pl_match = playlistNames.stream().anyMatch(playlistName::equalsIgnoreCase);
        if (!pl_match) {
            System.out.println("Cannot remove video from " + playlistName + ": Playlist does not exist");
            return;
        }
        if (!videoIds.contains(videoId)) {
            System.out.println("Cannot remove video from " + playlistName + ": Video does not exist");
            return;
        }
        for (String plName : playlistNames) {
            if (plName.equalsIgnoreCase(playlistName)) {
                Playlist pl = playlistLibrary.getPlaylist(plName);
                Video video = videoLibrary.getVideo(videoId);
                if (pl.getVideosInPlaylist().contains(video)) {
                    pl.removeVideo(video);
                    System.out.println("Removed video from " + playlistName + ": " + video.getTitle());
                } else
                    System.out.println("Cannot remove video from " + playlistName + ": Video is not in playlist");
            }
        }
    }

    public void clearPlaylist(String playlistName) {
        if (playlistName == null)
            return;
        boolean match_flag = false;
        for (Playlist pl : playlists) {
            if (pl.getPlaylistName().equalsIgnoreCase(playlistName)) {
                match_flag = true;
                pl.getVideosInPlaylist().clear();
                System.out.println("Successfully removed all videos from " + playlistName);
            }
        }
        if (!match_flag)
            System.out.println("Cannot clear playlist " + playlistName + ": Playlist does not exist");
    }

    public void deletePlaylist(String playlistName) {
        if (playlistName == null)
            return;
        boolean match_flag = false;
        for (Playlist pl : playlists) {
            if (pl.getPlaylistName().equalsIgnoreCase(playlistName)) {
                match_flag = true;
                playlistLibrary.removeFromLibrary(pl);
                playlists.remove(pl);
                playlistNames.remove(pl.getPlaylistName());
                System.out.println("Deleted playlist: " + playlistName);
                break;
            }
        }
        if (!match_flag)
            System.out.println("Cannot delete playlist " + playlistName + ": Playlist does not exist");
    }

    public void searchVideos(String searchTerm) {
        if (searchTerm == null)
            return;
        List<Video> videosFromSearch = new ArrayList<>();
        for (String videoTitle : videoTitles) {
            if (Pattern.compile(Pattern.quote(searchTerm), Pattern.CASE_INSENSITIVE).matcher(videoTitle).find()) {
                Video video = videoLibrary.getVideoByTitle(videoTitle);
                if (!video.getFlagInfo())
                    videosFromSearch.add(video);
            }
        }
        if (videosFromSearch.isEmpty()) {
            System.out.println("No search results for " + searchTerm);
        } else {
            Collections.sort(videosFromSearch);
            System.out.println("Here are the results for " + searchTerm + ":");
            int count = 0;
            for (Video video : videosFromSearch)
                System.out.println(++count + ") " + video.toString());

            var scanner = new Scanner(System.in);
            System.out.println("Would you like to play any of the above? If yes, specify the number of the video.\n"
                    + "If your answer is not a valid number, we will assume it's a no.");
            var input = scanner.nextLine();
            int num;
            if (StringUtils.isNumeric(input) && (num = Integer.parseInt(input)) <= count && num > 0)
                this.playVideo(videosFromSearch.get(num - 1).getVideoId());
        }
    }

    public void searchVideosWithTag(String videoTag) {
        if (videoTag == null)
            return;
        List<Video> videosFromSearch = new ArrayList<>();
        for (Video video : videoLibrary.getVideos()) {
            for (String tag : video.getTags()) {
                if (Pattern.compile(Pattern.quote(videoTag), Pattern.CASE_INSENSITIVE).matcher(tag).find()) {
                    if (!video.getFlagInfo()) {
                        videosFromSearch.add(video);
                        break;
                    }
                }
            }
        }
        if (videosFromSearch.isEmpty()) {
            System.out.println("No search results for " + videoTag);
        } else {
            Collections.sort(videosFromSearch);
            System.out.println("Here are the results for " + videoTag + ":");
            int count = 0;
            for (Video video : videosFromSearch)
                System.out.println(++count + ") " + video.toString());

            var scanner = new Scanner(System.in);
            System.out.println("Would you like to play any of the above? If yes, specify the number of the video.\n"
                    + "If your answer is not a valid number, we will assume it's a no.");
            var input = scanner.nextLine();
            int num;
            if (StringUtils.isNumeric(input) && (num = Integer.parseInt(input)) <= count && num > 0)
                this.playVideo(videosFromSearch.get(num - 1).getVideoId());
        }

    }

    public void flagVideo(String videoId) {
        if (videoId == null)
            return;

        Video target = videoLibrary.getVideo(videoId);
        if (target == null)
            System.out.println("Cannot flag video: Video does not exist");
        else if (target.getFlagInfo())
            System.out.println("Video is already flagged");
        else {
            target.updateFlagInfo(true);
            target.updateFlagReason(default_reason);
            videosFlagged.add(target);
            if (currentlyPlayingVideo != null && currentlyPlayingVideo.equals(target))
                this.stopVideo();
            System.out
                    .println("Successfully flagged video: " + target.getTitle() + " (reason: " + default_reason + ")");
        }
    }

    public void flagVideo(String videoId, String reason) {
        if (videoId == null)
            return;

        Video target = videoLibrary.getVideo(videoId);
        if (target == null)
            System.out.println("Cannot flag video: Video does not exist");
        else if (target.getFlagInfo())
            System.out.println("Cannot flag video: Video is already flagged");
        else {
            target.updateFlagInfo(true);
            target.updateFlagReason(reason);
            videosFlagged.add(target);
            if (currentlyPlayingVideo != null && currentlyPlayingVideo.equals(target))
                this.stopVideo();
            System.out.println("Successfully flagged video: " + target.getTitle() + " (reason: " + reason + ")");
        }
    }

    public void allowVideo(String videoId) {
        if (videoId == null)
            return;
        Video video = videoLibrary.getVideo(videoId);
        if (video == null)
            System.out.println("Cannot remove flag from video: Video does not exist");
        else if (!video.getFlagInfo())
            System.out.println("Cannot remove flag from video: Video is not flagged");
        else {
            video.updateFlagInfo(false);
            video.updateFlagReason(null);
            videosFlagged.remove(video);
            System.out.println("Successfully removed flag from video: " + video.getTitle());
        }
    }
}