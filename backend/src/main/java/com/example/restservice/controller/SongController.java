package com.example.restservice.controller;

import com.example.restservice.exception.BadRequestException;
import com.example.restservice.exception.CustomExceptionHandler;
import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.Song;
import com.example.restservice.model.SongDto;
import com.example.restservice.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/songs")
@RequiredArgsConstructor
@Tag(name = "Song Controller", description = "Allows retrieving, creating, and deleting songs")
@CustomExceptionHandler
public class SongController {
    private final SongService songService;
    private static final String MESSAGE = "Song with id ";

    @GetMapping
    @Operation(
            summary = "Get all songs",
            description = "Retrieves all stored songs"
    )
    public List<SongDto> getAllSongs() {
        List<SongDto> songs = songService.getAllSongs();
        if (songs.isEmpty()) {
            throw new NotFoundException("The song list is empty");
        }
        return songs;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get song by id",
            description = "Retrieves a song by its id"
    )
    public Song getSongById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid song id: " + id);
        }
        Song song = songService.getSongById(id);
        if (song == null) {
            throw new NotFoundException(MESSAGE + id + " does not exist");
        }
        return song;
    }

    @PostMapping
    @Operation(
            summary = "Add songs",
            description = "Adds new songs to the existing ones"
    )
    public List<Song> createSongs(@RequestBody List<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            throw new BadRequestException("The song list cannot be empty");
        }
        return songService.createSongs(songs);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a song",
            description = "Updates a song by its id, replacing the artist and title"
    )
    public Song updateSong(@PathVariable Long id, @RequestBody Song song) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid id: " + id);
        }
        if (song == null) {
            throw new BadRequestException("Song data cannot be null");
        }
        Song updatedSong = songService.updateSong(id, song);
        if (updatedSong == null) {
            throw new NotFoundException(MESSAGE + id + " not found");
        }
        return updatedSong;
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a song",
            description = "Deletes a song by its id"
    )
    public void deleteSong(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid id: " + id);
        }
        if (songService.getSongById(id) == null) {
            throw new NotFoundException(MESSAGE + id + " not found");
        }
        songService.deleteSong(id);
    }

    @GetMapping("/by-artist")
    @Operation(
            summary = "Get songs by artist",
            description = "Retrieves all songs by the specified artist"
    )
    public List<Song> getSongsByArtist(@RequestParam @Parameter(description = "Artist's pseudonym",
            example = "Монеточка") String artist) {
        if (artist == null || artist.isEmpty()) {
            throw new BadRequestException("Artist name cannot be empty");
        }
        List<Song> songs = songService.getSongsByArtist(artist);
        if (songs.isEmpty()) {
            throw new NotFoundException("No songs found for artist " + artist);
        }
        return songs;
    }
}