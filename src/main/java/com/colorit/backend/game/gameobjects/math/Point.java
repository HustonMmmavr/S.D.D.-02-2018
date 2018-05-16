package com.colorit.backend.game.gameobjects.math;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Point  {
    private int x;
    private int y;

    public Point(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void add(Point other) {
        this.x += other.x;
        this.y += other.y;
    }

    public void sub(Point other) {
        this.x -= other.x;
        this.y -= other.y;
    }
}
