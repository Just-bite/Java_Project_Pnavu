package com.example.restservice.controller;

import com.example.restservice.exception.BadRequestException;
import com.example.restservice.exception.CustomExceptionHandler;
import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.Playlist;
import com.example.restservice.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
@Tag(name = "Playlist Controller", description =
        "Allows retrieving, creating, and deleting playlists, "
                + "as well as adding songs to them and modifying their contents")
@CustomExceptionHandler
public class PlaylistController {
    private final PlaylistService playlistService;

    @GetMapping
    @Operation(
            summary = "Get all playlists",
            description = "Retrieves all playlists and information about the songs they contain"
    )
    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        if (playlists.isEmpty()) {
            throw new NotFoundException("The playlist list is empty");
        }
        return playlists;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get playlist by id"
    )
    public Playlist getPlaylistById(@PathVariable Long id) {
        Playlist playlist = playlistService.getPlaylistById(id);
        if (playlist == null) {
            throw new NotFoundException("Playlist with id " + id + " not found");
        }
        return playlist;
    }

    @PostMapping("/create/{userId}")
    @Operation(
            summary = "Create a playlist",
            description = "Creates an empty playlist for the user with the provided id"
    )
    public Playlist createPlaylist(@Valid @RequestBody Playlist playlist,
                                   @NotNull @PathVariable Long userId) {
        if (playlist == null) {
            throw new BadRequestException("Playlist cannot be null");
        }
        if (userId <= 0) {
            throw new BadRequestException("Invalid userId: " + userId);
        }
        return playlistService.createPlaylist(playlist, userId);
    }

    @PostMapping("/{playlistId}/add-songs")
    @Operation(
            summary = "Add songs to a playlist",
            description = "Adds songs to the playlist with the provided id"
    )
    public Playlist addSongsToPlaylist(@PathVariable Long playlistId,
                                       @RequestBody List<Long> songIds) {
        if (playlistId == null || playlistId <= 0) {
            throw new BadRequestException("Invalid playlistId: " + playlistId);
        }
        if (songIds == null || songIds.isEmpty()) {
            throw new BadRequestException("The songIds list cannot be empty");
        }
        return playlistService.addSongsToPlaylist(playlistId, songIds);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a playlist",
            description = "Deletes the playlist with the provided id"
    )
    public void deletePlaylist(@PathVariable Long id) {
        Playlist playlist = getPlaylistById(id);
        if (playlist == null) {
            throw new NotFoundException("Playlist with id " + id + " not found");
        }
        playlistService.deletePlaylist(id);
    }

    @GetMapping("/with-songs")
    @Operation(
            summary = "Get non-empty playlists",
            description = "Retrieves all playlists and information about the songs they contain, "
                    + "if they are not empty"
    )
    public List<Playlist> getPlaylistsWithSongs() {
        List<Playlist> playlists = playlistService.getPlaylistsWithSongs();
        if (playlists.isEmpty()) {
            throw new NotFoundException("No non-empty playlists found");
        }
        return playlists;
    }
}