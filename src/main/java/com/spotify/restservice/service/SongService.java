package com.spotify.restservice.service;

import com.spotify.restservice.model.Song;
import com.spotify.restservice.repository.SongRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Song getSongById(Long id) {
        return songRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Песня не найдена"));
    }

    public Song createSong(Song song) {
        return songRepository.save(song);
    }

    public Song updateSong(Long id, Song song) {
        Song existingSong = getSongById(id);
        existingSong.setTitle(song.getTitle());
        existingSong.setArtist(song.getArtist());
        return songRepository.save(existingSong);
    }

    public void deleteSong(Long id) {
        songRepository.deleteById(id);
    }
}
