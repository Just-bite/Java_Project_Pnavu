package com.example.restservice.controller;

import com.example.restservice.model.Playlist;
import com.example.restservice.model.User;
import com.example.restservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/relations")
public class RelationsController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRelations() {
        Map<String, Object> relations = new HashMap<>();

        // 1. Получаем пользователей с плейлистами
        List<User> users = userRepository.findAllWithPlaylists();

        // 2. Получаем все плейлисты
        List<Playlist> playlists = users.stream()
                .flatMap(u -> u.getPlaylists().stream())
                .collect(Collectors.toList());

        // 3. Инициируем загрузку песен (будет использован batch fetching)
        playlists.forEach(playlist -> {
            playlist.getSongs().size(); // Инициируем загрузку
        });

        relations.put("users", users);
        relations.put("playlists", playlists);
        relations.put("songs", playlists.stream()
                .flatMap(p -> p.getSongs().stream())
                .distinct()
                .collect(Collectors.toList()));

        return ResponseEntity.ok(relations);
    }
}