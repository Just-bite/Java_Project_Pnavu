package com.example.restservice.service;

import com.example.restservice.model.Playlist;
import com.example.restservice.model.Song;
import com.example.restservice.repository.PlaylistRepository;
import com.example.restservice.repository.SongRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SongService {
    Logger logger = Logger.getLogger(SongService.class.getName());
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final LinkedHashMap<String, List<Song>> songsCache;
    
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    public Song getSongById(Long id) {
        return songRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Song not found"));
    }

    public List<Song> createSongs(List<Song> songs) {
        return songRepository.saveAll(songs);
    }

    public Song updateSong(Long id, Song song) {
        Song existingSong = getSongById(id);


        existingSong.setTitle(song.getTitle());
        existingSong.setArtist(song.getArtist());
        Song updatedSong = songRepository.save(existingSong);


        songsCache.remove(existingSong.getArtist());
        logger.log(Level.INFO,"[CACHE] Removed outdated cache for artist: {0}", existingSong.getArtist());

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
        logger.log(Level.INFO,"[CACHE] Removed songs cache for artist: {0}", artist);
    }

    public List<Song> getSongsByArtist(String artist) {
        if (songsCache.containsKey(artist)) {
            logger.log(Level.INFO, "[CACHE] Retrieved songs for artist: {0}", artist);
            return songsCache.get(artist);
        }

        logger.log(Level.INFO,"[DB] Querying database for artist: {0}", artist);
        List<Song> songs = songRepository.findByArtist(artist);
        songsCache.put(artist, songs);
        logger.log(Level.INFO,"[CACHE] Added songs to cache for artist: {0}", artist);
        return songs;
    }
}


