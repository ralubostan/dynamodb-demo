package com.softvision.dynamodb.rest.domain;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument
public class Song {

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String artist;

    @Getter
    @Setter
    private String album;

    @Getter
    @Setter
    private Long length;

    @Getter
    @Setter
    private String added;

    public Song(Song song) {
        this.id = song.id;
        this.name = song.name;
        this.artist = song.artist;
        this.added = song.added;
        this.album = song.album;
        this.length = song.length;
    }
}
