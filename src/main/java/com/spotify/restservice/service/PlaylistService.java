package com.spotify.restservice.service;

import com.spotify.restservice.model.Playlist;
import com.spotify.restservice.model.Song;
import com.spotify.restservice.repository.PlaylistRepository;
import com.spotify.restservice.repository.SongRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    public Playlist getPlaylistById(Long id) {
        return playlistRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Плейлист не найден"));
    }

    public Playlist createPlaylist(Playlist playlist) {
        return playlistRepository.save(playlist);
    }

    public Playlist updatePlaylist(Long id, Playlist playlist) {
        Playlist existingPlaylist = getPlaylistById(id);
        existingPlaylist.setName(playlist.getName());
        return playlistRepository.save(existingPlaylist);
    }

    public void deletePlaylist(Long id) {
        playlistRepository.deleteById(id);
    }

    public Playlist addSongToPlaylist(Long playlistId, Long songId) {
        Playlist playlist = getPlaylistById(playlistId);
        Song song = songRepository.findById(songId).orElseThrow(()
                    -> new RuntimeException("Песня не найдена"));
        playlist.getSongs().add(song);
        return playlistRepository.save(playlist);
    }
}

