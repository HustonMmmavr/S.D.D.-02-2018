package com.colorit.backend.game.messages.input;

import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.gameobjects.math.Point;
import com.colorit.backend.websocket.Message;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClientSnapshot extends Message {
    private Direction direction;
    private int velocity;
    private Point posititon;
    private long frameTime;

    @JsonProperty("direction")
    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @JsonProperty("position")
    public Point getPosititon() {
        return posititon;
    }

    public void setPosititon(Point posititon) {
        this.posititon = posititon;
    }

    @JsonProperty("clientTime")
    public long getFrameTime() {
        return frameTime;
    }

    public void setFrameTime(long frameTime) {
        this.frameTime = frameTime;
    }

    public boolean isChanged() {
        return direction != null;
    }
}
