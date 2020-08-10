package com.softvision.dynamodb.rest.service;

import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.softvision.dynamodb.rest.domain.Playlist;
import com.softvision.dynamodb.rest.domain.Song;
import com.softvision.dynamodb.rest.dto.CreatePlaylistDTO;
import com.softvision.dynamodb.rest.dto.PatchPlaylistDTO;
import com.softvision.dynamodb.rest.repository.PlaylistDAO;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlaylistService {

    private final PlaylistDAO playlistDAO;

    @Autowired
    public PlaylistService(PlaylistDAO playlistDAO) {
        this.playlistDAO = playlistDAO;
    }

    public Playlist createPlaylist(CreatePlaylistDTO playlistRequest) {
        List<Song> songList = new ArrayList<>();
        playlistRequest.getSongList()
            .forEach(songDTO -> songList.add(
                    Song.builder()
                    .id(UUID.randomUUID().toString())
                    .name(songDTO.getName())
                    .artist(songDTO.getArtist())
                    .album(songDTO.getAlbum())
                    .length(songDTO.getLength())
                    .added(DateTime.now().toString())
                    .build())
            );
        Playlist playlist = Playlist.builder()
                .id(UUID.randomUUID().toString())
                .playlistName(playlistRequest.getName())
                .genre(playlistRequest.getGenre())
                .type(playlistRequest.getType())
                .songList(songList)
                .createdDate(DateTime.now())
                .build();

        return playlistDAO.create(playlist);
    }

    public Playlist patchPlaylist(PatchPlaylistDTO playlistRequest, String id) {
        final Optional<Playlist> optionalPlaylist = playlistDAO.findById(id);
        if (!optionalPlaylist.isPresent()) {
            throw new ResourceNotFoundException("Playlist with id " + id + " hasn't been found");
        }

        Playlist retrievedPlaylist = optionalPlaylist.get();
        List<Song> newList = new ArrayList<>(retrievedPlaylist.getSongList());
        playlistRequest.getSongList()
                .forEach(songDTO -> newList.add(
                        Song.builder()
                                .id(UUID.randomUUID().toString())
                                .name(songDTO.getName())
                                .artist(songDTO.getArtist())
                                .album(songDTO.getAlbum())
                                .length(songDTO.getLength())
                                .added(DateTime.now().toString())
                                .build()
                ));
        retrievedPlaylist.setSongList(newList);
        return playlistDAO.update(retrievedPlaylist);
    }

    public void deletePlaylist(String id) {
        final Optional<Playlist> optionalPlaylist = playlistDAO.findById(id);
        if (!optionalPlaylist.isPresent()) {
            throw new ResourceNotFoundException("Playlist with id " + id + " hasn't been found");
        }

        playlistDAO.delete(id);
    }

    public Playlist findById(String id) {
        final Optional<Playlist> optionalPlaylist = playlistDAO.findById(id);
        if (!optionalPlaylist.isPresent()) {
            throw new ResourceNotFoundException("Playlist with id " + id + " hasn't been found");
        }

        return optionalPlaylist.get();
    }

    public List<Playlist> findAllPlaylists() {
        return playlistDAO.findAll();
    }

    public List<Playlist> findAllPlaylistsByGenre(String genre) {
        return playlistDAO.findAllByGenre(genre);
    }

    public void deleteAllPlaylists() {
        playlistDAO.deleteAllPlaylists();
    }
}
