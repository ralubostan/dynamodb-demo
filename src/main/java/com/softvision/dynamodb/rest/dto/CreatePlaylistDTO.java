package com.softvision.dynamodb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class CreatePlaylistDTO {

    @Getter
    private String genre;

    @Getter
    private String name;

    @Getter
    private PlaylistType type;

    @Getter
    private List<SongDTO> songList;

}
