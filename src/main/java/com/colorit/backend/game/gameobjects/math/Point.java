package com.colorit.backend.game.gameobjects.math;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Point<T extends Number>  {
    private double x;
    private double y;
//    private Class<T> clazz;

    public Point(@JsonProperty("x") T x, @JsonProperty("y") T y) {
        this.x = x.doubleValue();
        this.y = y.doubleValue();
    }

    public Double getX() {
        return x;
    }

    public void setX(T x) {
        this.x = x.doubleValue();
    }

    public Double getY() {
        return y;
    }

    public void setY(T y) {
        this.y = y.doubleValue();
    }

    public void add(Point<T> other) {
        this.x += other.x;
        this.y += other.y;
    }

    public void sub(Point<T> other) {
        this.x -= other.x;
        this.y -= other.y;
    }
}
