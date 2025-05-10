package com.example.restservice;

import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.Playlist;
import com.example.restservice.model.User;
import com.example.restservice.model.UserDto;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        List<Playlist> playlists = Collections.emptyList();
        Long userId = 1L;
        User expectedUser = new User(userId, "testUser",playlists);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(expectedUser, result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User result = userService.getUserById(userId);

        assertNull(result);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetAllUsers() {
        List<Playlist> playlists = Collections.emptyList();
        User user = new User(1L, "user1",playlists);
        List<User> expectedUsers = Collections.singletonList(user);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> result = userService.getAllUsers();

        assertEquals(1, result.size());
        //assertEquals(expectedUsers, result);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testCreateUser() {
        List<Playlist> playlists = Collections.emptyList();
        User newUser = new User(null, "newUser",playlists);
        User savedUser = new User(1L, "newUser",playlists);

        when(userRepository.save(newUser)).thenReturn(savedUser);

        User result = userService.createUser(newUser);

        assertNotNull(result.getId());
        assertEquals(savedUser, result);
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void testCreateUsers() {
        List<Playlist> playlists = Collections.emptyList();
        List<User> newUsers = Arrays.asList(
                new User(null,"user name 1",playlists),
                new User(null, "newUser 2",playlists)
        );
        List<User> savedUsers = Arrays.asList(
                new User(1L,"user name 1",playlists),
                new User(2L, "newUser 2",playlists)
        );

        when(userRepository.saveAll(newUsers)).thenReturn(savedUsers);

        List<User> result = userService.createUsers(newUsers);

        assertEquals(2, result.size());
        assertEquals(savedUsers, result);
        verify(userRepository, times(1)).saveAll(newUsers);
    }

    @Test
    void testDeleteUser_Success_WithEmptyPlaylists() {
        Long userId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        User user = new User(userId, "testUser",playlists);


        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(playlistRepository).deleteAll(user.getPlaylists());
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> userService.deleteUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(playlistRepository, times(1)).deleteAll(user.getPlaylists());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUser_NotFound() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(playlistRepository, never()).deleteAll(any());
        verify(userRepository, never()).delete(any());
    }
}