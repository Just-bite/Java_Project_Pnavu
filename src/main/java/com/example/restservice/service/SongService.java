package com.example.restservice.service;

import com.example.restservice.model.Song;
import com.example.restservice.repository.SongRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SongService {

    private final List<Song> songList;

    public SongService(SongRepository songRepository) {
        this.songList = songRepository.getAllSongs();
    }

    public Optional<Song> getSong(Integer id) {
        return songList.stream()
                .filter(song -> song.getId() == id)
                .findFirst();
    }

    public List<Song> getSongsByAuthor(String author) {
        return songList.stream()
                .filter(song -> song.getAuthorName().equalsIgnoreCase(author))
                .toList();
    }

    public List<Song> getSongsByName(String name) {
        return songList.stream()
                .filter(song -> song.getSongName().equalsIgnoreCase(name))
                .toList();
    }

    public List<Song> searchSongsByAuthorAndName(String author, String name) {
        return songList.stream()
                .filter(song -> song.getAuthorName().equalsIgnoreCase(author)
                        && song.getSongName().equalsIgnoreCase(name))
                .toList();
    }
}

