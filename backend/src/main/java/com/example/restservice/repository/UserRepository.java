package com.example.restservice.repository;

import com.example.restservice.model.Playlist;
import com.example.restservice.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"playlists"})
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.playlists")
    List<User> findAllWithPlaylists();
}

