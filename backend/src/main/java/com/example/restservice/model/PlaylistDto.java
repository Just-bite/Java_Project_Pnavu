package com.example.restservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDto {
    private Long id;
    private String name;
    private String username; // Только имя пользователя
    private List<String> songTitles; // Только названия песен

    public static PlaylistDto fromEntity(Playlist playlist) {
        PlaylistDto dto = new PlaylistDto();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setUsername(playlist.getUser() != null ? playlist.getUser().getUsername() : null);
        dto.setSongTitles(playlist.getSongs() != null ?
                playlist.getSongs().stream()
                        .map(Song::getTitle)
                        .collect(Collectors.toList()) :
                Collections.emptyList());
        return dto;
    }
}