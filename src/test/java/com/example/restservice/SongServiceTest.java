package com.example.restservice;

import com.example.restservice.exception.BadRequestException;
import com.example.restservice.model.Playlist;
import com.example.restservice.model.Song;
import com.example.restservice.model.User;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.SongRepository;
import com.example.restservice.service.SongService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private LinkedHashMap<String, List<Song>> songsCache;

    @Mock
    private Logger logger;

    @InjectMocks
    private SongService songService;

    @Test
    void getAllSongs_ShouldReturnAllSongs() {
        // Arrange
        List<Playlist> playlists = Collections.emptyList();
        List<Song> expectedSongs = Arrays.asList(
                new Song(1L, "Song 1", "Artist 1",playlists),
                new Song(2L, "Song 2", "Artist 2",playlists)
        );
        when(songRepository.findAll()).thenReturn(expectedSongs);

        // Act
        List<Song> result = songService.getAllSongs();

        // Assert
        assertEquals(expectedSongs, result);
        verify(songRepository, times(1)).findAll();
    }

    @Test
    void getSongById_ShouldReturnSong_WhenExists() {
        // Arrange
        Long songId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        Song expectedSong = new Song(songId, "Test Song", "Test Artist",playlists);
        when(songRepository.findById(songId)).thenReturn(Optional.of(expectedSong));

        // Act
        Song result = songService.getSongById(songId);

        // Assert
        assertEquals(expectedSong, result);
        verify(songRepository, times(1)).findById(songId);
    }

    @Test
    void getSongById_ShouldThrowException_WhenNotExists() {
        // Arrange
        Long songId = 999L;
        when(songRepository.findById(songId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> songService.getSongById(songId));
        verify(songRepository, times(1)).findById(songId);
    }

    @Test
    void createSongs_ShouldReturnSavedSongs() {
        // Arrange
        List<Playlist> playlists = Collections.emptyList();
        List<Song> songsToSave = Arrays.asList(
                new Song(null, "New Song 1", "New Artist",playlists),
                new Song(null, "New Song 2", "New Artist",playlists)
        );
        List<Song> savedSongs = Arrays.asList(
                new Song(1L, "New Song 1", "New Artist",playlists),
                new Song(2L, "New Song 2", "New Artist",playlists)
        );
        when(songRepository.saveAll(songsToSave)).thenReturn(savedSongs);

        // Act
        List<Song> result = songService.createSongs(songsToSave);

        // Assert
        assertEquals(savedSongs, result);
        verify(songRepository, times(1)).saveAll(songsToSave);
    }

    @Test
    void updateSong_ShouldUpdateAndClearCache() {
        // Arrange
        Long songId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        Song existingSong = new Song(songId, "Old Title", "Old Artist",playlists);
        Song updatedSongData = new Song(null, "New Title", "New Artist",playlists);
        Song expectedSong = new Song(songId, "New Title", "New Artist",playlists);

        when(songRepository.findById(songId)).thenReturn(Optional.of(existingSong));
        when(songRepository.save(existingSong)).thenReturn(expectedSong);

        // Act
        Song result = songService.updateSong(songId, updatedSongData);

        // Assert
        assertEquals(expectedSong, result);
        assertEquals("New Title", existingSong.getTitle());
        assertEquals("New Artist", existingSong.getArtist());
        verify(songRepository, times(1)).findById(songId);
        verify(songRepository, times(1)).save(existingSong);
        verify(songsCache, times(1)).remove("Old Artist");
    }

    @Test
    void deleteSong_ShouldRemoveFromPlaylistsAndClearCache() {
        // Arrange
        Long songId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        List<Song> songsToDelete = Collections.emptyList();
        User user = new User(1L,"test user",playlists);
        Song song = new Song(songId, "Test Song", "Test Artist",playlists);
        Playlist playlist1 = new Playlist(1L, "Playlist 1",user,songsToDelete);
        Playlist playlist2 = new Playlist(2L, "Playlist 2",user,songsToDelete);
        song.setPlaylists(Arrays.asList(playlist1, playlist2));

        when(songRepository.findById(songId)).thenReturn(Optional.of(song));
        doNothing().when(songRepository).delete(song);

        // Act
        songService.deleteSong(songId);

        // Assert
        verify(songRepository, times(1)).findById(songId);
        verify(playlistRepository, times(1)).save(playlist1);
        verify(playlistRepository, times(1)).save(playlist2);
        verify(songRepository, times(1)).delete(song);
        verify(songsCache, times(1)).remove("Test Artist");
    }

    @Test
    void getSongsByArtist_ShouldReturnFromCache_WhenAvailable() {
        // Arrange
        String artist = "Cached Artist";
        List<Playlist> playlists = Collections.emptyList();
        List<Song> cachedSongs = Arrays.asList(
                new Song(1L, "Song 1", artist, playlists),
                new Song(2L, "Song 2", artist,playlists)
        );
        when(songsCache.containsKey(artist)).thenReturn(true);
        when(songsCache.get(artist)).thenReturn(cachedSongs);

        // Act
        List<Song> result = songService.getSongsByArtist(artist);

        // Assert
        assertEquals(cachedSongs, result);
        verify(songsCache, times(1)).containsKey(artist);
        verify(songsCache, times(1)).get(artist);
        verify(songRepository, never()).findByArtist(any());
    }

    @Test
    void getSongsByArtist_ShouldQueryDBAndUpdateCache_WhenNotInCache() {
        // Arrange
        String artist = "New Artist";
        List<Playlist> playlists = Collections.emptyList();
        List<Song> dbSongs = Arrays.asList(
                new Song(1L, "Song 1", artist,playlists),
                new Song(2L, "Song 2", artist,playlists)
        );
        when(songsCache.containsKey(artist)).thenReturn(false);
        when(songRepository.findByArtist(artist)).thenReturn(dbSongs);

        // Act
        List<Song> result = songService.getSongsByArtist(artist);

        // Assert
        assertEquals(dbSongs, result);
        verify(songsCache, times(1)).containsKey(artist);
        verify(songRepository, times(1)).findByArtist(artist);
        verify(songsCache, times(1)).put(artist, dbSongs);
    }

    @Test
    void getSongsByArtist_ShouldThrowException_WhenArtistIsNull() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> songService.getSongsByArtist(null));
        verifyNoInteractions(songsCache, songRepository);
    }

    @Test
    void getSongsByArtist_ShouldThrowException_WhenArtistIsEmpty() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> songService.getSongsByArtist(""));
        verifyNoInteractions(songsCache, songRepository);
    }

    @Test
    void getSongsByArtist_ShouldThrowException_WhenArtistTooLong() {
        // Arrange
        String longArtist = "a".repeat(101);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> songService.getSongsByArtist(longArtist));
        verifyNoInteractions(songsCache, songRepository);
    }

    @Test
    void getSongsByArtist_ShouldSanitizeInput() {
        // Arrange
        String artist = "Artist\nWith\rNewlines";
        String sanitizedArtist = "Artist_With_Newlines";
        List<Playlist> playlists = Collections.emptyList();
        List<Song> dbSongs = Collections.singletonList(new Song(1L, "Song", sanitizedArtist,playlists));

        when(songsCache.containsKey(sanitizedArtist)).thenReturn(false);
        when(songRepository.findByArtist(sanitizedArtist)).thenReturn(dbSongs);

        // Act
        List<Song> result = songService.getSongsByArtist(artist);

        // Assert
        assertEquals(dbSongs, result);
        verify(songRepository, times(1)).findByArtist(sanitizedArtist);
    }
}