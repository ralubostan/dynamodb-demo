package com.softvision.dynamodb.rest.controller;

import com.softvision.dynamodb.rest.service.PlaylistService;
import com.softvision.dynamodb.rest.domain.Playlist;
import com.softvision.dynamodb.rest.dto.CreatePlaylistDTO;
import com.softvision.dynamodb.rest.dto.PatchPlaylistDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping
    public ResponseEntity<Playlist> createPlaylist(@RequestBody CreatePlaylistDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(playlistService.createPlaylist(request));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Playlist> retrievePlaylistById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(playlistService.findById(id));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Playlist> addSongsToPlaylist(@RequestBody PatchPlaylistDTO request, @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(playlistService.patchPlaylist(request, id));
    }

    @GetMapping()
    public ResponseEntity<List<Playlist>> retrieveAllPlaylists() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(playlistService.findAllPlaylists());
    }

    @GetMapping(params = "genre")
    public ResponseEntity<List<Playlist>> retrieveAllPlaylistsByGenre(@RequestParam String genre) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(playlistService.findAllPlaylistsByGenre(genre));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> removePlaylist(@PathVariable String id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> removeAllPlaylist() {
        playlistService.deleteAllPlaylists();
        return ResponseEntity.noContent().build();
    }
}
