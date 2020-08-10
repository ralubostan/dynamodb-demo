package com.softvision.dynamodb.rest.dto;

import lombok.Getter;

public enum PlaylistType {
    PRIVATE("Private"),
    PUBLIC("Public");

    @Getter
    private final String name;

    PlaylistType(String name) {
        this.name = name;
    }

}
