package com.colorit.backend.game.gameobjects;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Direction {
    @JsonProperty("UP")
    UP(1),
    @JsonProperty("DOWN")
    DOWN(2),
    @JsonProperty("LEFT")
    LEFT(3),
    @JsonProperty("RIGHT")
    RIGHT(4);

    private Integer id;

    Direction(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
