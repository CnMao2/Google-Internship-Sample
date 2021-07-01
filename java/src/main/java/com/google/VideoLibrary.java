package com.google;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A class used to represent a Video Library. Added a HashMap for using video
 * title as key to video as value for better access.
 */
class VideoLibrary {

    private final HashMap<String, Video> videos;
    private final HashMap<String, Video> title_video_map;

    VideoLibrary() {
        this.videos = new HashMap<>();
        this.title_video_map = new HashMap<>();
        try {
            File file = new File(this.getClass().getResource("/videos.txt").getFile());

            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] split = line.split("\\|");
                String title = split[0].strip();
                String id = split[1].strip();
                List<String> tags;
                if (split.length > 2) {
                    tags = Arrays.stream(split[2].split(",")).map(String::strip).collect(Collectors.toList());
                } else {
                    tags = new ArrayList<>();
                }
                Video video = new Video(title, id, tags);
                this.videos.put(id, video);
                this.title_video_map.put(title, video);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Couldn't find videos.txt");
            e.printStackTrace();
        }
    }

    List<Video> getVideos() {
        return new ArrayList<>(this.videos.values());
    }

    /**
     * Get a video by id. Returns null if the video is not found.
     */
    Video getVideo(String videoId) {
        return this.videos.get(videoId);
    }

    Video getVideoByTitle(String videoTitle) {
        return this.title_video_map.get(videoTitle);
    }
}
