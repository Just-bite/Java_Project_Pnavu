package com.example.restservice.model;

/**Class song that contains fields that allow to sort or display information about songs.
 * Will be connected to User class which will allow to gain new songs based on users preferences*/
public class Song {
    private int id;
    private String authorName;
    private String songName;

    public Song(int id, String songName, String authorName) {
        this.id = id;
        this.songName = songName;
        this.authorName = authorName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}