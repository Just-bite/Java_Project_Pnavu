package com.example.restservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "songs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Song entity")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Song ID", example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    @Column(nullable = false)
    @Schema(description = "Song title",
            example = "Hello", accessMode = Schema.AccessMode.READ_ONLY)
    private String title;
    @Column(nullable = false)
    @Schema(description = "Artists pseudonim",
            example = "Adele", accessMode = Schema.AccessMode.READ_ONLY)
    private String artist;
    @ManyToMany(mappedBy = "songs")
    @JsonBackReference
    @Schema(description = "Names of playlists that contain current song",
            example = "My playlist", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Playlist> playlists;

}
