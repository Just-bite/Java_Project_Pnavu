package com.example.restservice.service;

import com.example.restservice.model.Playlist;
import com.example.restservice.model.User;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> createUsers(List<User> users) {
        return userRepository.saveAll(users);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Playlist> playlists = user.getPlaylists();
        playlistRepository.deleteAll(playlists);
        userRepository.delete(user);
    }
}

