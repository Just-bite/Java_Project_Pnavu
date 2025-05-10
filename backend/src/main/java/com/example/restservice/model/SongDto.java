package com.example.restservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

// SongDTO.java
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {
    private Long id;
    private String title;
    private String artist;
    private List<String> playlistNames; // Только названия плейлистов

    public static SongDto fromEntity(Song song) {
        SongDto dto = new SongDto();
        dto.setId(song.getId());
        dto.setTitle(song.getTitle());
        dto.setArtist(song.getArtist());
        dto.setPlaylistNames(song.getPlaylists().stream()
                .map(Playlist::getName)
                .collect(Collectors.toList()));
        return dto;
    }
}