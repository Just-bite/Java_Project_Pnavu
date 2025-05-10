package com.example.restservice.service;

import com.example.restservice.exception.BadRequestException;
import com.example.restservice.exception.NotFoundException;
import com.example.restservice.model.Playlist;
import com.example.restservice.model.Song;
import com.example.restservice.model.SongDto;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.SongRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class SongService {
    Logger logger = Logger.getLogger(SongService.class.getName());
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final LinkedHashMap<String, List<Song>> songsCache;

    public List<SongDto> getAllSongs() {
        return songRepository.findAll().stream()
                .map(SongDto::fromEntity)
                .collect(Collectors.toList());
    }

    public Song getSongById(Long id) {
        return songRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Song not found"));
    }

    public List<Song> createSongs(List<Song> songs) {
        if (songs == null || songs.isEmpty()) {
            throw new BadRequestException("Song list must not be empty");
        }

        songs.forEach(song -> {
            if (song.getTitle() == null || song.getTitle().isBlank()) {
                throw new BadRequestException("Song title must not be empty");
            }
        });

        return songRepository.saveAll(songs);
    }

    public Song updateSong(Long id, Song songDetails) {
        Song existingSong = songRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Song not found with id: " + id));

        existingSong.setTitle(songDetails.getTitle());
        existingSong.setArtist(songDetails.getArtist());

        Song updatedSong = songRepository.save(existingSong);

        songsCache.remove(existingSong.getArtist());
        logger.log(Level.INFO, "[CACHE] Removed outdated cache for artist: {0}",
                existingSong.getArtist());

        return updatedSong;
    }

    public void deleteSong(Long id) {
        Song song = getSongById(id);
        String artist = song.getArtist();

        for (Playlist playlist : song.getPlaylists()) {
            playlist.getSongs().remove(song);
            playlistRepository.save(playlist);
        }
        songRepository.delete(song);

        songsCache.remove(artist);
        logger.log(Level.INFO, "[CACHE] Removed songs cache for artist: {0}", artist);
    }

    public List<Song> getSongsByArtist(String artist) {
        if (artist == null || artist.isEmpty()) {
            throw new BadRequestException("Artist name cannot be null or empty");
        }

        if (artist.length() > 100) {
            throw new BadRequestException("Artist name is too long");
        }

        artist = artist.replaceAll("[\n\r]", "_");

        if (songsCache.containsKey(artist)) {
            logger.log(Level.INFO, "[CACHE] Retrieved songs for artist: {0}", artist);
            return songsCache.get(artist);
        }

        logger.log(Level.INFO, "[DB] Querying database for artist: {0}", artist);

        List<Song> songs = songRepository.findByArtist(artist);

        songsCache.put(artist, songs);

        logger.log(Level.INFO, "[CACHE] Added songs to cache for artist: {0}", artist);
        return songs;
    }
}


