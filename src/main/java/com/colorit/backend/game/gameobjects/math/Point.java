package com.colorit.backend.game.gameobjects.math;

public class Point<T extends Number>  {
    private double x;
    private double y;
    private Class<T> clazz;

    public Point(T x, T y) {
        this.x = x.doubleValue();
        this.y = y.doubleValue();
    }

    public T getX() {
        return clazz.cast(x);
    }

    public void setX(T x) {
        this.x = x.doubleValue();
    }

    public T getY() {
        return clazz.cast(y);
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
