package com.example.restservice;

import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.Playlist;
import com.example.restservice.model.User;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetUserById_Success() {
        // Arrange
        List<Playlist> playlists = Collections.emptyList();
        Long userId = 1L;
        User expectedUser = new User(userId, "testUser",playlists);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        List<Playlist> playlists = Collections.emptyList();
        User user = new User(1L, "user1",playlists);
        List<User> expectedUsers = Collections.singletonList(user);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(1, result.size());
        assertEquals(expectedUsers, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreateUser() {
        // Arrange
        List<Playlist> playlists = Collections.emptyList();
        User newUser = new User(null, "newUser",playlists); // ID пока null
        User savedUser = new User(1L, "newUser",playlists); // После сохранения получает ID

        when(userRepository.save(newUser)).thenReturn(savedUser);

        // Act
        User result = userService.createUser(newUser);

        // Assert
        assertNotNull(result.getId());
        assertEquals(savedUser, result);
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void testDeleteUser_Success_WithEmptyPlaylists() {
        // Arrange
        Long userId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        User user = new User(userId, "testUser",playlists);


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(playlistRepository).deleteAll(user.getPlaylists());
        doNothing().when(userRepository).delete(user);

        // Act & Assert
        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(playlistRepository, times(1)).deleteAll(user.getPlaylists());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        // Arrange
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(playlistRepository, never()).deleteAll(any());
        verify(userRepository, never()).delete(any());
    }
}