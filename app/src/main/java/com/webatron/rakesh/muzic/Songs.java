package com.webatron.rakesh.muzic;

/**
 * Created by rakesh on 5/2/18.
 */

public class Songs {
    String name,path,artist;

    public Songs(String name, String path, String artist) {
        this.name = name;
        this.path = path;
        this.artist = artist;
    }

    public Songs() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
