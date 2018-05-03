package com.colorit.backend.game.gameobjects;

public enum Direction {
    UP (1),
    DOWN (2),
    LEFT (3),
    RIGHT (4);

    Integer id;

    Direction(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
