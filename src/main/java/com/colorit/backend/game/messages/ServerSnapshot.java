package com.colorit.backend.game.messages;

import com.colorit.backend.game.gameobjects.math.Point;

import java.util.List;

public class ServerSnapshot {
    private List<List<Integer>> gameField;
    private List<Point> coordinates;
    private List<Integer> scores;
    private Integer frameTime;

}
