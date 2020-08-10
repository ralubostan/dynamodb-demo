package com.softvision.dynamodb.rest.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEpochDate;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import com.softvision.dynamodb.rest.dto.PlaylistType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.List;
import java.util.stream.Collectors;

@DynamoDBDocument
@DynamoDBTable(tableName = "Playlist")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {

    @Getter
    @Setter
    @DynamoDBHashKey
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "genrePlaylists")
    private String id;

    @Getter
    @Setter
    @DynamoDBAttribute
    @DynamoDBIndexHashKey(globalSecondaryIndexName = "genrePlaylists")
    private String genre;

    @Getter
    @Setter
    @DynamoDBAttribute
    private String playlistName;

    @Getter
    @Setter
    @DynamoDBAttribute
    @DynamoDBTypeConvertedEnum
    private PlaylistType type;

    @Getter
    @Setter
    @DynamoDBTypeConvertedEpochDate
    private DateTime createdDate;

    @Getter
    @Setter
    @DynamoDBVersionAttribute
    private Long version;

    private List<Song> songList;

    @DynamoDBAttribute(attributeName = "songList")
    @DynamoDBTypeConverted(converter = DynamoDBListConverter.class)
    public List<Song> getSongList() {
        return this.songList.stream().map(Song::new).collect(Collectors.toList());
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList.stream().map(Song::new).collect(Collectors.toList());
    }
}
