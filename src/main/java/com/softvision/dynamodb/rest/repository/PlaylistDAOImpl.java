package com.softvision.dynamodb.rest.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDeleteExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBSaveExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnConsumedCapacity;
import com.google.common.collect.ImmutableMap;
import com.softvision.dynamodb.rest.domain.Playlist;
import com.softvision.dynamodb.rest.exceptions.DynamoDBException;
import lombok.extern.slf4j.Slf4j;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@EnableScan
public class PlaylistDAOImpl implements PlaylistDAO {

    private static final String PLAYLIST_ID_KEY = "id";
    private static final String GENRE_PLAYLISTS_INDEX = "genrePlaylists";

    private final DynamoDBMapper dynamoDBMapper;

    @Autowired
    public PlaylistDAOImpl(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    @Override
    public Optional<Playlist> findById(String id) {
        try {
            return Optional.ofNullable(dynamoDBMapper.load(Playlist.class, id));
        } catch (Exception ex) {
            log.error("Could not load the entity from DynamoDB", ex);
            throw new DynamoDBException("Entity could not be loaded from dynamoDB", ex);
        }
    }

    @Override
    public List<Playlist> findAll() {
        try {
            return dynamoDBMapper.scan(Playlist.class, new DynamoDBScanExpression());
        } catch (Exception ex) {
            log.error("Could not load all entities from DynamoDB", ex);
            throw new DynamoDBException("Could not load all entities from DynamoDB", ex);
        }
    }

    @Override
    public PaginatedQueryList<Playlist> findAllByGenre(String genre) {
        Map<String, AttributeValue> eav = new HashMap<>();
        eav.put(":val1", new AttributeValue().withS(genre));

        DynamoDBQueryExpression<Playlist> queryExpression = new DynamoDBQueryExpression<Playlist>()
                .withIndexName(GENRE_PLAYLISTS_INDEX)
                .withKeyConditionExpression("genre = :val1")
                .withExpressionAttributeValues(eav)
                .withConsistentRead(false)
                .withReturnConsumedCapacity(ReturnConsumedCapacity.TOTAL);

        try {
            return dynamoDBMapper.query(Playlist.class, queryExpression);
        } catch (Exception ex) {
            log.error("Couldn't load all playlists from DynamoDB with genre =" + genre, ex);
            throw new DynamoDBException("Error loading entities by genre", ex);
        }
    }

    @Override
    public Playlist create(Playlist playlist) {
        try {
            DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
            Map<String, ExpectedAttributeValue> expectedAttributes =
                    ImmutableMap.<String, ExpectedAttributeValue>builder()
                            .put(PLAYLIST_ID_KEY, new ExpectedAttributeValue(false))
                            .build();
            saveExpression.setExpected(expectedAttributes);
            dynamoDBMapper.save(playlist, saveExpression);
            return playlist;
        } catch (Exception ex) {
            log.error("Could not save the playlist in DynamoDB with id={}", playlist.getId(), ex);
            throw new DynamoDBException("Entity could not be saved in DynamoDB", ex);
        }
    }

    @Override
    public Playlist update(Playlist playlist) {
        DynamoDBSaveExpression saveExpression = new DynamoDBSaveExpression();
        Map<String, ExpectedAttributeValue> expectedAttributes =
                ImmutableMap.<String, ExpectedAttributeValue>builder()
                        .put(PLAYLIST_ID_KEY, new ExpectedAttributeValue(new AttributeValue(playlist.getId())))
                        .build();
        saveExpression.setExpected(expectedAttributes);
        dynamoDBMapper.save(playlist, saveExpression);
        return playlist;
    }

    @Override
    public boolean delete(String id) {
        Optional<Playlist> playlist = Optional.ofNullable(dynamoDBMapper.load(Playlist.class, id));
        if (playlist.isPresent()) {
            Map<String, ExpectedAttributeValue> expectedAttributes =
                    ImmutableMap.<String, ExpectedAttributeValue>builder()
                            .put(PLAYLIST_ID_KEY, new ExpectedAttributeValue(new AttributeValue(id)))
                            .build();
            DynamoDBDeleteExpression deleteExpression = new DynamoDBDeleteExpression()
                    .withExpected(expectedAttributes);
            dynamoDBMapper.delete(playlist.get(), deleteExpression);
            return true;
        }

        return false;
    }

    @Override
    public void deleteAllPlaylists() {
        List<Playlist> playlists = findAll();
        if (!CollectionUtils.isEmpty(playlists)) {
            dynamoDBMapper.batchDelete(playlists);
        }
    }
}
