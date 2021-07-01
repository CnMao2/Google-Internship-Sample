package com.google;

import java.util.Collections;
import java.util.List;

/**
 * A class used to represent a video. Added and overrode some functions for
 * sorting and comparing purpose. Modified toString() for the flagging
 * functionality.
 */

class Video implements Comparable<Video> {

    private final String title;
    private final String videoId;
    private final List<String> tags;
    private boolean flagged;
    private String reason;

    Video(String title, String videoId, List<String> tags) {
        this.title = title;
        this.videoId = videoId;
        this.tags = Collections.unmodifiableList(tags);
        this.flagged = false;
        this.reason = null;
    }

    /** Returns the title of the video. */
    String getTitle() {
        return title;
    }

    /** Returns the video id of the video. */
    String getVideoId() {
        return videoId;
    }

    /** Returns a readonly collection of the tags of the video. */
    List<String> getTags() {
        return tags;
    }

    boolean getFlagInfo() {
        return flagged;
    }

    String getFlagReason() {
        return reason;
    }

    void updateFlagInfo(boolean bool) {
        flagged = bool;
    }

    void updateFlagReason(String new_reason) {
        reason = new_reason;
    }

    @Override
    public boolean equals(Object that) {
        if (that == null || !(that instanceof Video))
            return false;
        Video temp = (Video) that;
        return (this.getTitle().equals(temp.getTitle()) && this.getVideoId().equals(temp.getVideoId())
                && this.getTags().equals(temp.getTags()));
    }

    @Override
    public int compareTo(Video that) {
        int res;
        if ((res = this.getTitle().compareTo(that.getTitle())) < 0) {
            return -1;
        } else if (res > 0)
            return 0;
        else
            return res;
    }

    @Override
    public String toString() {
        String res = this.getTitle() + " (" + this.getVideoId() + ") ";
        String tags_no_comma = this.getTags().toString().replaceAll(", ", " ");
        if (this.getFlagInfo())
            return res + tags_no_comma + " - FLAGGED (reason: " + this.getFlagReason() + ")";
        return res + tags_no_comma;
    }
}
