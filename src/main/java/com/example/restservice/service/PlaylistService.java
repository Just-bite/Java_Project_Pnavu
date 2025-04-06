package com.example.restservice.service;

import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.Playlist;
import com.example.restservice.model.Song;
import com.example.restservice.model.User;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.SongRepository;
import com.example.restservice.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    public Playlist getPlaylistById(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Playlist with id not found"));

    }

    public Playlist createPlaylist(Playlist playlist, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        playlist.setUser(user);
        return playlistRepository.save(playlist);
    }

    @Transactional
    public Playlist addSongsToPlaylist(Long playlistId, List<Long> songIds) {
        Playlist playlist = getPlaylistById(playlistId);
        List<Song> songs = songRepository.findAllById(songIds);
        playlist.setSongs(songs);
        return playlistRepository.save(playlist);
    }

    public void deletePlaylist(Long playlistId) {
        Playlist playlist = getPlaylistById(playlistId);
        playlist.getSongs().clear();
        playlistRepository.delete(playlist);
    }

    public List<Playlist> getPlaylistsWithSongs() {
        return playlistRepository.findPlaylistsWithSongs();
    }

}




