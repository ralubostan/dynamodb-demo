package com.softvision.dynamodb.rest.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.softvision.dynamodb.rest.domain.Playlist;

import java.util.List;
import java.util.Optional;

public interface PlaylistDAO {
    Optional<Playlist> findById(String id);
    List<Playlist> findAll();
    PaginatedQueryList<Playlist> findAllByGenre(String genre);
    Playlist create(Playlist playlist);
    Playlist update(Playlist playlist);
    boolean delete(String id);
    void deleteAllPlaylists();
}
