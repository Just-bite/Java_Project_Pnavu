package com.example.restservice.controller;

import com.example.restservice.model.Song;
import com.example.restservice.service.SongService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/{id}")
    public Song getSongById(@PathVariable Integer id) {
        Optional<Song> song = songService.getSong(id);
        return song.orElse(null);
    }

    @GetMapping("/info-by-author")
    public List<Song> getSongsInfoByAuthor(@RequestParam String author) {
        return songService.getSongsByAuthor(author);
    }

    @GetMapping("/info-by-song-name")
    public List<Song> getSongsInfoByName(@RequestParam String name) {
        return songService.getSongsByName(name);
    }

    @GetMapping("/info-by-author-and-song-name")
    public List<Song> searchSongsByAuthorAndName(@RequestParam String author,
                                                 @RequestParam String name) {
        return songService.searchSongsByAuthorAndName(author, name);
    }
}
