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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private LinkedHashMap<String, List<Song>> songsCache;


    @InjectMocks
    private SongService songService;

    @Test
    void getAllSongs_ShouldReturnAllSongs() {
        List<Playlist> playlists = Collections.emptyList();
        List<Song> expectedSongs = Arrays.asList(
                new Song(1L, "Song 1", "Artist 1",playlists),
                new Song(2L, "Song 2", "Artist 2",playlists)
        );
        when(songRepository.findAll()).thenReturn(expectedSongs);

        List<Song> result = songService.getAllSongs();

        assertEquals(expectedSongs, result);
        verify(songRepository, times(1)).findAll();
    }

    @Test
    void getSongById_ShouldReturnSong_WhenExists() {
        Long songId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        Song expectedSong = new Song(songId, "Test Song", "Test Artist",playlists);
        when(songRepository.findById(songId)).thenReturn(Optional.of(expectedSong));

        Song result = songService.getSongById(songId);

        assertEquals(expectedSong, result);
        verify(songRepository, times(1)).findById(songId);
    }

    @Test
    void getSongById_ShouldThrowException_WhenNotExists() {
        Long songId = 999L;
        when(songRepository.findById(songId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> songService.getSongById(songId));
        verify(songRepository, times(1)).findById(songId);
    }

    @Test
    void createSongs_ShouldReturnSavedSongs() {
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

        List<Song> result = songService.createSongs(songsToSave);

        assertEquals(savedSongs, result);
        verify(songRepository, times(1)).saveAll(songsToSave);
    }

    @Test
    void updateSong_ShouldUpdateAndClearCache() {
        Long songId = 1L;
        List<Playlist> playlists = Collections.emptyList();
        Song existingSong = new Song(songId, "Old Title", "Old Artist",playlists);
        Song updatedSongData = new Song(null, "New Title", "New Artist",playlists);
        Song expectedSong = new Song(songId, "New Title", "New Artist",playlists);

        when(songRepository.findById(songId)).thenReturn(Optional.of(existingSong));
        when(songRepository.save(existingSong)).thenReturn(expectedSong);


        Song result = songService.updateSong(songId, updatedSongData);

        assertEquals(expectedSong, result);
        assertEquals("New Title", existingSong.getTitle());
        assertEquals("New Artist", existingSong.getArtist());
        verify(songRepository, times(1)).findById(songId);
        verify(songRepository, times(1)).save(existingSong);
        verify(songsCache, times(1)).remove("Old Artist");
    }

    @Test
    void deleteSong_ShouldRemoveFromPlaylistsAndClearCache() {
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

        songService.deleteSong(songId);

        verify(songRepository, times(1)).findById(songId);
        verify(playlistRepository, times(1)).save(playlist1);
        verify(playlistRepository, times(1)).save(playlist2);
        verify(songRepository, times(1)).delete(song);
        verify(songsCache, times(1)).remove("Test Artist");
    }

    @Test
    void getSongsByArtist_ShouldReturnFromCache_WhenAvailable() {
        String artist = "Cached Artist";
        List<Playlist> playlists = Collections.emptyList();
        List<Song> cachedSongs = Arrays.asList(
                new Song(1L, "Song 1", artist, playlists),
                new Song(2L, "Song 2", artist,playlists)
        );
        when(songsCache.containsKey(artist)).thenReturn(true);
        when(songsCache.get(artist)).thenReturn(cachedSongs);

        List<Song> result = songService.getSongsByArtist(artist);

        assertEquals(cachedSongs, result);
        verify(songsCache, times(1)).containsKey(artist);
        verify(songsCache, times(1)).get(artist);
        verify(songRepository, never()).findByArtist(any());
    }

    @Test
    void getSongsByArtist_ShouldQueryDBAndUpdateCache_WhenNotInCache() {
        String artist = "New Artist";
        List<Playlist> playlists = Collections.emptyList();
        List<Song> dbSongs = Arrays.asList(
                new Song(1L, "Song 1", artist,playlists),
                new Song(2L, "Song 2", artist,playlists)
        );
        when(songsCache.containsKey(artist)).thenReturn(false);
        when(songRepository.findByArtist(artist)).thenReturn(dbSongs);

        List<Song> result = songService.getSongsByArtist(artist);

        assertEquals(dbSongs, result);
        verify(songsCache, times(1)).containsKey(artist);
        verify(songRepository, times(1)).findByArtist(artist);
        verify(songsCache, times(1)).put(artist, dbSongs);
    }

    @Test
    void getSongsByArtist_ShouldThrowException_WhenArtistIsNull() {
        assertThrows(BadRequestException.class, () -> songService.getSongsByArtist(null));
        verifyNoInteractions(songsCache, songRepository);
    }

    @Test
    void getSongsByArtist_ShouldThrowException_WhenArtistIsEmpty() {
        assertThrows(BadRequestException.class, () -> songService.getSongsByArtist(""));
        verifyNoInteractions(songsCache, songRepository);
    }

    @Test
    void getSongsByArtist_ShouldThrowException_WhenArtistTooLong() {
        String longArtist = "a".repeat(101);

        assertThrows(BadRequestException.class, () -> songService.getSongsByArtist(longArtist));
        verifyNoInteractions(songsCache, songRepository);
    }

    @Test
    void getSongsByArtist_ShouldSanitizeInput() {
        String artist = "Artist\nWith\rNewlines";
        String sanitizedArtist = "Artist_With_Newlines";
        List<Playlist> playlists = Collections.emptyList();
        List<Song> dbSongs = Collections.singletonList(new Song(1L, "Song", sanitizedArtist,playlists));

        when(songsCache.containsKey(sanitizedArtist)).thenReturn(false);
        when(songRepository.findByArtist(sanitizedArtist)).thenReturn(dbSongs);

        List<Song> result = songService.getSongsByArtist(artist);

        assertEquals(dbSongs, result);
        verify(songRepository, times(1)).findByArtist(sanitizedArtist);
    }
}