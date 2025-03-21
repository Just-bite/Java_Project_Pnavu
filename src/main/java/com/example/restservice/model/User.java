package com.example.restservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сущность пользователя")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Ункальный идентификатор пользователя",
            example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(description = "Имя пользователя",
            example = "User_nickname", accessMode = Schema.AccessMode.READ_ONLY)
    private String username;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Schema(description = "Название плейлистов принадлежащих пользователю",
            example = "My playlist", accessMode = Schema.AccessMode.READ_ONLY)
    private List<Playlist> playlists;

}

