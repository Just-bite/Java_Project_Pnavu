package com.example.restservice.service;

import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.Playlist;
import com.example.restservice.model.User;
import com.example.restservice.model.UserDto;
import com.example.restservice.model.UserUpdateRequest;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> createUsers(List<User> users) {
        return userRepository.saveAll(users);
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        // 1. Загружаем пользователя с его плейлистами
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // 2. Обновляем имя
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        // 3. Обрабатываем плейлисты только если переданы явно
        if (request.getPlaylistIds() != null) {
            // 3.1. Загружаем ВСЕ необходимые плейлисты за один запрос
            List<Playlist> newPlaylists = playlistRepository.findAllById(request.getPlaylistIds());

            // 3.2. Очищаем текущие связи (важно!)
            user.getPlaylists().forEach(p -> p.setUser(null)); // Разрываем старые связи
            user.getPlaylists().clear(); // Очищаем коллекцию

            // 3.3. Устанавливаем новые связи
            newPlaylists.forEach(playlist -> {
                playlist.setUser(user); // Устанавливаем владельца
                user.getPlaylists().add(playlist); // Добавляем в коллекцию
            });
        }

        // 4. Сохраняем (не обязательно, т.к. @Transactional)
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // Удаляем все плейлисты пользователя
        List<Playlist> playlists = user.getPlaylists();
        if (playlists != null) {
            // Сначала разрываем связи с песнями
            playlists.forEach(playlist -> {
                if (playlist.getSongs() != null) {
                    playlist.getSongs().clear();
                }
            });
            playlistRepository.deleteAll(playlists);
        }

        // Затем удаляем самого пользователя
        userRepository.delete(user);
    }
}

