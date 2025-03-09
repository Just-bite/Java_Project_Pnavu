package com.example.restservice.service;

import com.example.restservice.model.Playlist;
import com.example.restservice.model.Song;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.SongRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SongService {
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;

    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Song getSongById(Long id) {
        return songRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Song not found"));
    }

    public List<Song> createSongs(List<Song> songs) {
        return songRepository.saveAll(songs);
    }

    public Song updateSong(Long id, Song song) {
        Song existingSong = getSongById(id);
        existingSong.setTitle(song.getTitle());
        existingSong.setArtist(song.getArtist());
        return songRepository.save(existingSong);
    }

    public void deleteSong(Long id) {
        Song song = getSongById(id);
        for (Playlist playlist : song.getPlaylists()) {
            playlist.getSongs().remove(song);
            playlistRepository.save(playlist);
        }
        songRepository.delete(song);
    }
}

