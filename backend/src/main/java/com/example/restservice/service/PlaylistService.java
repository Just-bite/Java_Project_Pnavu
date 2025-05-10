package com.example.restservice.service;

import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.*;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.SongRepository;
import com.example.restservice.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public List<PlaylistDto> getAllPlaylists() {
        return playlistRepository.findAll().stream()
                .map(PlaylistDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Playlist getPlaylistById(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Playlist with id not found"));

    }

    public PlaylistDto createPlaylist(Playlist playlist, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        playlist.setUser(user);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return PlaylistDto.fromEntity(savedPlaylist);
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

    @Transactional
    public Playlist updatePlaylist(Long playlistId, PlaylistUpdateRequest request) {
        // 1. Находим плейлист
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new NotFoundException("Playlist not found"));

        // 2. Обновляем название (если передано)
        if (request.getName() != null) {
            playlist.setName(request.getName());
        }

        // 3. Обновляем песни (если переданы)
        if (request.getSongIds() != null) {
            // Получаем все песни разом
            List<Song> songs = songRepository.findAllById(request.getSongIds());

            // Очищаем текущие песни
            playlist.getSongs().clear();

            // Добавляем новые
            playlist.getSongs().addAll(songs);
        }

        return playlistRepository.save(playlist);
    }

    public List<Playlist> getPlaylistsWithSongs() {
        return playlistRepository.findPlaylistsWithSongs();
    }

}




