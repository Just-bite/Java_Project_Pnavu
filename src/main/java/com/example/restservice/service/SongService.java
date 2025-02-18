package com.example.restservice.service;

import com.example.restservice.model.Song;
import com.example.restservice.repository.SongRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SongService {

    private final List<Song> songList;

    public SongService(SongRepository songRepository) {
        this.songList = songRepository.getAllSongs();
    }

    public Optional<Song> getSong(Integer id) {
        Optional<Song> song = songList.stream()
                .filter(s -> s.getId() == id)
                .findFirst();
        if (song.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Song not found");
        }
        return song;
    }

    public List<Song> getSongsByAuthor(String author) {
        List<Song> songs = songList.stream()
                .filter(song -> song.getAuthorName().equalsIgnoreCase(author))
                .toList();
        if (songs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No songs found for this author");
        }
        return songs;
    }

    public List<Song> getSongsByName(String name) {
        List<Song> songs = songList.stream()
                .filter(song -> song.getSongName().equalsIgnoreCase(name))
                .toList();
        if (songs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No songs found with this name");
        }
        return songs;
    }

    public List<Song> searchSongsByAuthorAndName(String author, String name) {
        List<Song> songs = songList.stream()
                .filter(song -> song.getAuthorName().equalsIgnoreCase(author)
                        && song.getSongName().equalsIgnoreCase(name))
                .toList();
        if (songs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No songs found for this author and name");
        }
        return songs;
    }
}
