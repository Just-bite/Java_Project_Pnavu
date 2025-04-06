package com.example.restservice;

import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.Playlist;
import com.example.restservice.model.Song;
import com.example.restservice.model.User;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.SongRepository;
import com.example.restservice.repository.UserRepository;
import com.example.restservice.service.PlaylistService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PlaylistService playlistService;

    @Test
    void getAllPlaylists_ShouldReturnAllPlaylists() {
        // Arrange
        List<Playlist> playlists = Collections.emptyList();
        User user = new User(1L,"test user",playlists);
        List<Song> songs = Collections.emptyList();
        List<Playlist> expectedPlaylists = Arrays.asList(
                new Playlist(1L, "Playlist 1",user,songs),
                new Playlist(2L, "Playlist 2",user,songs)
        );
        when(playlistRepository.findAll()).thenReturn(expectedPlaylists);

        // Act
        List<Playlist> result = playlistService.getAllPlaylists();

        // Assert
        assertEquals(expectedPlaylists, result);
        verify(playlistRepository, times(1)).findAll();
    }

    @Test
    void getPlaylistById_ShouldReturnPlaylist_WhenExists() {
        // Arrange
        Long playlistId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        User user = new User(1L,"test user",playlists);
        List<Song> songs = Collections.emptyList();
        Playlist expectedPlaylist = new Playlist(playlistId, "Test Playlist",user,songs);
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(expectedPlaylist));

        // Act
        Playlist result = playlistService.getPlaylistById(playlistId);

        // Assert
        assertEquals(expectedPlaylist, result);
        verify(playlistRepository, times(1)).findById(playlistId);
    }

    @Test
    void getPlaylistById_ShouldThrowNotFoundException_WhenNotExists() {
        // Arrange
        Long playlistId = 999L;
        when(playlistRepository.findById(playlistId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> playlistService.getPlaylistById(playlistId));
        verify(playlistRepository, times(1)).findById(playlistId);
    }

    @Test
    void createPlaylist_ShouldCreateAndAssignToUser() {
        // Arrange
        Long userId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        List<Song> songs = Collections.emptyList();
        User user = new User(userId, "testUser",playlists);
        Playlist newPlaylist = new Playlist(null, "New Playlist",user,songs);
        Playlist savedPlaylist = new Playlist(1L, "New Playlist",user,songs);
        savedPlaylist.setUser(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(playlistRepository.save(newPlaylist)).thenReturn(savedPlaylist);

        // Act
        Playlist result = playlistService.createPlaylist(newPlaylist, userId);

        // Assert
        assertNotNull(result.getId());
        assertEquals(user, result.getUser());
        verify(userRepository, times(1)).findById(userId);
        verify(playlistRepository, times(1)).save(newPlaylist);
    }

    @Test
    void createPlaylist_ShouldThrowNotFoundException_WhenUserNotExists() {
        // Arrange
        Long userId = 999L;
        List<Playlist> playlists = Collections.emptyList();
        List<Song> songs = Collections.emptyList();
        User user = new User(userId, "testUser",playlists);
        Playlist newPlaylist = new Playlist(null, "New Playlist",user,songs);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> playlistService.createPlaylist(newPlaylist, userId));
        verify(userRepository, times(1)).findById(userId);
        verify(playlistRepository, never()).save(any());
    }

    @Test
    @Transactional
    void addSongsToPlaylist_ShouldAddSongs() {
        // Arrange
        Long playlistId = 1L;
        List<Long> songIds = Arrays.asList(1L, 2L);
        List<Playlist> playlists = Collections.emptyList();
        List<Song> songsT = Collections.emptyList();
        User user = new User(1L, "testUser",playlists);
        Playlist playlist = new Playlist(playlistId, "Existing Playlist",user, songsT);
        List<Song> songs = Arrays.asList(
                new Song(1L, "Song 1", "Artist 1",playlists),
                new Song(2L, "Song 2", "Artist 2",playlists)
        );

        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(songRepository.findAllById(songIds)).thenReturn(songs);
        when(playlistRepository.save(playlist)).thenReturn(playlist);

        // Act
        Playlist result = playlistService.addSongsToPlaylist(playlistId, songIds);

        // Assert
        assertEquals(songs, result.getSongs());
        verify(playlistRepository, times(1)).findById(playlistId);
        verify(songRepository, times(1)).findAllById(songIds);
        verify(playlistRepository, times(1)).save(playlist);
    }

    @Test
    @Transactional
    void addSongsToPlaylist_ShouldClearExistingSongs() {
        // Arrange
        Long playlistId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        List<Song> songsT = Collections.emptyList();
        User user = new User(1L, "testUser",playlists);
        Playlist playlist = new Playlist(playlistId, "Playlist with Songs",user,songsT);
        playlist.setSongs(List.of(new Song(3L, "Old Song", "Old Artist", playlists)));

        List<Long> newSongIds = List.of(1L, 2L);
        List<Song> newSongs = List.of(
                new Song(1L, "New Song 1", "Artist 1",playlists),
                new Song(2L, "New Song 2", "Artist 2",playlists)
        );

        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        when(songRepository.findAllById(newSongIds)).thenReturn(newSongs);
        when(playlistRepository.save(playlist)).thenReturn(playlist);

        // Act
        Playlist result = playlistService.addSongsToPlaylist(playlistId, newSongIds);

        // Assert
        assertEquals(2, result.getSongs().size());
        assertTrue(result.getSongs().containsAll(newSongs));
    }

    @Test
    void deletePlaylist_ShouldClearSongsAndDelete() {
        // Arrange
        Long playlistId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        User user = new User(1L, "testUser",playlists);
        List<Song> songs = new ArrayList<>(Arrays.asList(
                new Song(1L, "Song 1", "Artist 1",playlists),
                new Song(2L, "Song 2", "Artist 2",playlists)
        ));
        Playlist playlist = new Playlist(playlistId, "To be deleted", user, songs);

        when(playlistRepository.findById(playlistId)).thenReturn(Optional.of(playlist));
        doNothing().when(playlistRepository).delete(playlist);

        // Act
        playlistService.deletePlaylist(playlistId);

        // Assert
        assertTrue(playlist.getSongs().isEmpty());
        verify(playlistRepository, times(1)).findById(playlistId);
        verify(playlistRepository, times(1)).delete(playlist);
    }

    @Test
    void getPlaylistsWithSongs_ShouldReturnPlaylistsWithSongs() {
        // Arrange
        List<Playlist> playlists = Collections.emptyList();
        List<Song> songsT = Collections.emptyList();
        User user = new User(1L, "testUser",playlists);
        List<Playlist> expectedPlaylists = Arrays.asList(
                new Playlist(1L, "Playlist 1",user,songsT),
                new Playlist(2L, "Playlist 2",user,songsT)
        );
        expectedPlaylists.get(0).setSongs(List.of(new Song(1L, "Song 1", "Artist 1",playlists)));

        when(playlistRepository.findPlaylistsWithSongs()).thenReturn(expectedPlaylists);

        // Act
        List<Playlist> result = playlistService.getPlaylistsWithSongs();

        // Assert
        assertEquals(expectedPlaylists, result);
        assertEquals(1, result.get(0).getSongs().size());
        verify(playlistRepository, times(1)).findPlaylistsWithSongs();
    }
}
