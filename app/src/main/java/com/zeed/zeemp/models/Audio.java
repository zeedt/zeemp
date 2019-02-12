package com.zeed.zeemp.models;

import java.io.Serializable;

/**
 * Created by zeed on 12/08/2018.
 */

public class Audio implements Serializable {

    private String data;

    private String album;

    private String albumId;

    private String title;

    private String duration;

    private String artist;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Audio passedAudio = (Audio) obj;
        if (passedAudio.getAlbumId().equals(this.getAlbumId()) && passedAudio.getData().equals(this.getData()) && passedAudio.getArtist().equals(this.getArtist())) {
            return true;
        }

        return super.equals(obj);
    }
}
