package com.example.restservice.controller;

import com.example.restservice.model.Playlist;
import com.example.restservice.service.PlaylistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
@Tag(name = "Контроллер плейлистов", description =
            "Позволяет получать, создавать и удалять плейлисты,"
          + "а также наполнять их песнями и изменять их содержимое")
public class PlaylistController {
    private final PlaylistService playlistService;

    @GetMapping
    public List<Playlist> getAllPlaylists() {
        return playlistService.getAllPlaylists();
    }

    @GetMapping("/{id}")
    public Playlist getPlaylistById(@PathVariable Long id) {
        return playlistService.getPlaylistById(id);
    }

    @PostMapping("/create/{userId}")
    public Playlist createPlaylist(@RequestBody Playlist playlist, @PathVariable Long userId) {
        return playlistService.createPlaylist(playlist, userId);
    }

    @PostMapping("/{playlistId}/add-songs")
    public Playlist addSongsToPlaylist(@PathVariable Long playlistId,
                                       @RequestBody List<Long> songIds) {
        return playlistService.addSongsToPlaylist(playlistId, songIds);
    }

    @DeleteMapping("/{id}")
    public void deletePlaylist(@PathVariable Long id) {
        playlistService.deletePlaylist(id);
    }

    @GetMapping("/with-songs")
    public List<Playlist> getPlaylistsWithSongs() {
        return playlistService.getPlaylistsWithSongs();
    }
}

