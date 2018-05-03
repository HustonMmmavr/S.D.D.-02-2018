package com.colorit.backend.game.messages;

import com.colorit.backend.game.gameobjects.Point;
import com.colorit.backend.websocket.Message;

import java.util.List;

public class Position extends Message {
    private List<Point> points;

    public Position(List<Point> points) {
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }
}
