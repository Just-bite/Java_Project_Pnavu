package com.example.restservice.controller;

import com.example.restservice.model.Song;
import com.example.restservice.service.SongService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "Контроллер песен", description = "Позволяет получать, создавать и удалять песни")
public class SongController {
    private final SongService songService;

    @GetMapping
    @Operation(
            summary = "Вывод песен",
            description = "Выводит все хранимые песни"
    )
    public List<Song> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Вывод песни",
            description = "Выводит песню по ee id"
    )
    public Song getSongById(@PathVariable Long id) {
        return songService.getSongById(id);
    }

    @PostMapping
    @Operation(
            summary = "Добавление песен",
            description = "Добавляет новые песни к существующим"
    )
    public List<Song> createSongs(@RequestBody List<Song> songs) {
        return songService.createSongs(songs);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновляет песню",
            description = "Обновляет песню по ee id, заменяет автора и название"
    )
    public Song updateSong(@PathVariable Long id, @RequestBody Song song) {
        return songService.updateSong(id, song);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаляет песню",
            description = "Удаляет песню по ее id"
    )
    public void deleteSong(@PathVariable Long id) {
        songService.deleteSong(id);
    }

    @GetMapping("/by-artist")
    @Operation(
            summary = "Вывод по автору",
            description = "Выводит все песни принадлежащие переданному автору"
    )
    public List<Song> getSongsByArtist(@RequestParam String artist) {
        if (artist == null || artist.isEmpty()) {
            throw new IllegalArgumentException("Artist name cannot be null or empty");
        }

        return songService.getSongsByArtist(artist);
    }

}
