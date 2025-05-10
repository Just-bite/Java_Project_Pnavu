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
public class UserDto {
    private Long id;
    private String username; // Только имя пользователя
    private List<PlaylistDto> playlists; // Список DTO плейлистов

    public static UserDto fromEntity(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPlaylists(user.getPlaylists() != null ?
                user.getPlaylists().stream()
                        .map(PlaylistDto::fromEntity)
                        .collect(Collectors.toList()) :
                Collections.emptyList());
        return dto;
    }
}
