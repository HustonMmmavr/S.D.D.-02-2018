package com.colorit.backend.game.gameobjects.math;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Point {
    private int posX;
    private int posY;

    public Point(@JsonProperty("x") int posX, @JsonProperty("y") int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @JsonProperty("x")
    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    @JsonProperty("y")
    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void add(Point other) {
        this.posX += other.posX;
        this.posY += other.posY;
    }

    public void sub(Point other) {
        this.posX -= other.posX;
        this.posY -= other.posY;
    }
}
