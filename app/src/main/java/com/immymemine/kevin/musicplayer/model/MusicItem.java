package com.immymemine.kevin.musicplayer.model;

import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by quf93 on 2017-10-15.
 */

public class MusicItem {
    private String musicId, albumId, title, artist, duration, date_added;
    private Uri musicUri, albumUri;

    public MusicItem(String musicId, String albumId, String title, String artist, String duration, String date_added) {
        this.musicId = musicId; this.albumId = albumId; this.title =title; this.artist = artist;
        this.duration = duration; this.date_added = date_added;
    }

    public void setMusicUri() {
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        musicUri = Uri.withAppendedPath(uri, musicId);
    }
    public void setAlbumUri() {
        String contentUri = "content://media/external/audio/albumart/";
        albumUri = Uri.parse(contentUri + albumId);
    }

    public String getMusicId() {
        return musicId;
    }
    public String getAlbumId() {
        return albumId;
    }
    public String getTitle() {
        return title;
    }
    public String getArtist() {
        return artist;
    }
    public String getDuration() {
        return duration;
    }
    public String getDate_added() {
        return date_added;
    }
    public Uri getMusicUri() {
        return musicUri;
    }
    public Uri getAlbumUri() {
        return albumUri;
    }
}
