package com.softvision.dynamodb.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SongDTO {

    @Getter
    private String name;

    @Getter
    private String artist;

    @Getter
    private String album;

    @Getter
    private Long length;

}
