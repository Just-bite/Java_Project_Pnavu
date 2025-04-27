package com.example.restservice.repository;

import com.example.restservice.model.Playlist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {

    // 1. Ваш существующий нативный запрос
    @Query(value = """
        SELECT p.*
        FROM playlists p
        JOIN playlist_songs ps ON p.id = ps.playlist_id
        GROUP BY p.id
        HAVING COUNT(ps.song_id) > 0
        """, nativeQuery = true)
    List<Playlist> findPlaylistsWithSongs();

    @Query("SELECT DISTINCT p FROM Playlist p LEFT JOIN FETCH p.songs WHERE p IN :playlists")
    List<Playlist> fetchSongsForPlaylists(@Param("playlists") List<Playlist> playlists);

}
