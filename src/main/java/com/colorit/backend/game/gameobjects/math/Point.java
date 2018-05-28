package com.colorit.backend.game.gameobjects.math;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Point {
    private int xPos;
    private int yPos;

    public Point(@JsonProperty("x") int xPos, @JsonProperty("y") int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getX() {
        return xPos;
    }

    public void setX(int xPos) {
        this.xPos = xPos;
    }

    public int getY() {
        return yPos;
    }

    public void setY(int yPos) {
        this.yPos = yPos;
    }

    public void add(Point other) {
        this.xPos += other.xPos;
        this.yPos += other.yPos;
    }

    public void sub(Point other) {
        this.xPos -= other.xPos;
        this.yPos -= other.yPos;
    }
}
