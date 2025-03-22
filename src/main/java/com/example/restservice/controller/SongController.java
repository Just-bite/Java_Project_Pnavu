package com.example.restservice.controller;

import com.example.restservice.BadRequestException;
import com.example.restservice.NotFoundException;
import com.example.restservice.model.Song;
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
@Tag(name = "Контроллер песен", description = "Позволяет получать, создавать и удалять песни")
public class SongController {
    private final SongService songService;

    @GetMapping
    @Operation(
            summary = "Вывод песен",
            description = "Выводит все хранимые песни"
    )
    public List<Song> getAllSongs() {
        List<Song> songs = songService.getAllSongs();
        if (songs.isEmpty()) {
            throw new NotFoundException("Список песен пуст");
        }
        return songs;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Вывод песни",
            description = "Выводит песню по её id"
    )
    public Song getSongById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Некорректный id песни: " + id);
        }
        Song song = songService.getSongById(id);
        if (song == null) {
            throw new NotFoundException("Песни с id " + id + " не существует");
        }
        return song;
    }

    @PostMapping
    @Operation(
            summary = "Добавление песен",
            description = "Добавляет новые песни к существующим"
    )
    public List<Song> createSongs(@RequestBody List<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            throw new BadRequestException("Список песен не может быть пустым");
        }
        return songService.createSongs(songs);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Обновляет песню",
            description = "Обновляет песню по её id, заменяет автора и название"
    )
    public Song updateSong(@PathVariable Long id, @RequestBody Song song) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Некорректный id: " + id);
        }
        if (song == null) {
            throw new BadRequestException("Данные песни не могут быть null");
        }
        Song updatedSong = songService.updateSong(id, song);
        if (updatedSong == null) {
            throw new NotFoundException("Песня с id " + id + " не найдена");
        }
        return updatedSong;
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаляет песню",
            description = "Удаляет песню по её id"
    )
    public void deleteSong(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Некорректный id: " + id);
        }
        if (songService.getSongById(id) == null) {
            throw new NotFoundException("Песня с id " + id + " не найдена");
        }
        songService.deleteSong(id);
    }

    @GetMapping("/by-artist")
    @Operation(
            summary = "Вывод по автору",
            description = "Выводит все песни, принадлежащие переданному автору"
    )
    public List<Song> getSongsByArtist(@RequestParam @Parameter(description = "Псевдоним автора",
            example = "Монеточка") String artist) {
        if (artist == null || artist.isEmpty()) {
            throw new BadRequestException("Имя автора не может быть пустым");
        }
        List<Song> songs = songService.getSongsByArtist(artist);
        if (songs.isEmpty()) {
            throw new NotFoundException("Песни автора " + artist + " не найдены");
        }
        return songs;
    }

}
