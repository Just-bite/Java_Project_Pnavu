package com.example.restservice.controller;

import com.example.restservice.BadRequestException;
import com.example.restservice.model.Playlist;
import com.example.restservice.service.PlaylistService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "Вывод плейлистов",
            description = "Выводит все плейлисты и информацию о песнях, в них хранящихся"
    )
    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlists = playlistService.getAllPlaylists();
        if (playlists.isEmpty()) {
            throw new BadRequestException("Список плейлистов пуст");
        }
        return playlists;
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Возвращает плейлист по id"
    )
    public Playlist getPlaylistById(@PathVariable Long id) {
        Playlist playlist = playlistService.getPlaylistById(id);
        if (playlist == null) {
            throw new BadRequestException("Плейлист с id " + id + " не найден");
        }
        return playlist;
    }

    @PostMapping("/create/{userId}")
    @Operation(
            summary = "Создание плейлиста",
            description = "Создает пустой плейлист для пользователя с полученым id"
    )
    public Playlist createPlaylist(@RequestBody Playlist playlist, @PathVariable Long userId) {
        if (playlist == null) {
            throw new BadRequestException("Плейлист не может быть null");
        }
        if (userId == null || userId <= 0) {
            throw new BadRequestException("Некорректный userId: " + userId);
        }
        return playlistService.createPlaylist(playlist, userId);
    }

    @PostMapping("/{playlistId}/add-songs")
    @Operation(
            summary = "Добавление песен в плейлист",
            description = "В плейлист с полученным id добавляет песни"
    )
    public Playlist addSongsToPlaylist(@PathVariable Long playlistId,
                                       @RequestBody List<Long> songIds) {
        if (playlistId == null || playlistId <= 0) {
            throw new BadRequestException("Некорректный playlistId: " + playlistId);
        }
        if (songIds == null || songIds.isEmpty()) {
            throw new BadRequestException("Список songIds не может быть пустым");
        }
        return playlistService.addSongsToPlaylist(playlistId, songIds);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Удаление плейлиста",
            description = "Удаляет плейлист по его id"
    )
    public void deletePlaylist(@PathVariable Long id) {
        Playlist playlist = getPlaylistById(id);
        if (playlist == null) {
            throw new BadRequestException("Плейлист с id " + id + " не найден");
        }
        playlistService.deletePlaylist(id);
    }

    @GetMapping("/with-songs")
    @Operation(
            summary = "Вывод непустых плейлистов",
            description = "Выводит все плейлисты и информацию о песнях, в них хранящихся,"
                    + "если они непустые"
    )
    public List<Playlist> getPlaylistsWithSongs() {
        List<Playlist> playlists = playlistService.getPlaylistsWithSongs();
        if (playlists.isEmpty()) {
            throw new BadRequestException("Непустые плейлисты не найдены");
        }
        return playlists;
    }
}

