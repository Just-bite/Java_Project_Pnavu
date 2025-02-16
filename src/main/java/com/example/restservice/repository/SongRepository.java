package com.example.restservice.repository;

import com.example.restservice.model.Song;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class SongRepository {

    private final List<Song> songList;

    public SongRepository() {
        songList = new ArrayList<>();

        Song song1 = new Song(1, "Bohemian Rhapsody", "Queen");
        Song song2 = new Song(2, "Smells Like Teen Spirit", "Nirvana");
        Song song3 = new Song(3, "Imagine", "John Lennon");
        Song song4 = new Song(4, "Billie Jean", "Michael Jackson");
        Song song5 = new Song(5, "Hotel California", "Eagles");

        songList.addAll(Arrays.asList(song1, song2, song3, song4, song5));
    }

    public List<Song> getAllSongs() {
        return songList;
    }

}
