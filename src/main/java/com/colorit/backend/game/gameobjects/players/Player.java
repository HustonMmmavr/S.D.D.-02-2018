package com.colorit.backend.game.gameobjects.players;

import com.colorit.backend.entities.Id;
import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.gameobjects.GameObject;
import com.colorit.backend.game.gameobjects.math.Point;
import com.fasterxml.jackson.databind.node.POJONode;

import java.util.concurrent.atomic.AtomicLong;

import static com.colorit.backend.game.GameConfig.*;

public class Player extends GameObject {
    protected String nickname;
    protected Integer score;
    protected int offset;
    protected Direction currentDirection;
    protected Direction newDirection;
    protected Point currentPosition;
    protected Integer velocity = DEFAULT_VELOCITY;
    protected Id<Player> playerId;

    public Player(String nickname, Id<Player> playerId, Point startPoint) {
        this.nickname = nickname;
        this.score = 0;
        this.playerId = playerId;
        this.offset = 0;
        this.currentPosition = startPoint;

        if (playerId.getId() == 1) {
            this.init(startPoint, Direction.RIGHT);
        } else if (id.getId() == 2) {
            this.init(startPoint, Direction.DOWN);
        } else if (id.getId() == 3) {
            this.init(startPoint, Direction.LEFT);
        } else {
            this.init(startPoint, Direction.UP);
        }
    }

    private void init(Point startPoint, Direction startDirection) {
        this.currentPosition = startPoint;
        this.currentDirection = startDirection;
        this.newDirection = currentDirection;
    }


    public Point move(double timeDelay, int minBorder, int maxBorder) {
        offset += velocity;
        boolean isOnCell = offset / DISTANCE >= 1;
        if (isOnCell) {
            offset %= DISTANCE;
            if (currentDirection == Direction.RIGHT) {
                currentPosition.setX(currentPosition.getX() >= maxBorder ?
                        maxBorder : currentPosition.getX() + 1);
            }
            if (currentDirection == Direction.DOWN) {
                currentPosition.setY(currentPosition.getY() + 1 >= maxBorder ?
                        maxBorder : currentPosition.getY() + 1);
            }
            if (currentDirection == Direction.LEFT) {
                currentPosition.setX(currentPosition.getX() - 1 <= minBorder ?
                        minBorder : currentPosition.getX() + 1);
            }
            if (currentDirection == Direction.UP) {
                currentPosition.setY(currentPosition.getY() - 1 <=  minBorder ?
                        minBorder : currentPosition.getY() + 1);
            }

            if (newDirection != null && currentDirection != newDirection) {
                offset = 0;
                currentDirection = newDirection;
            }
        }

        return new Point(currentPosition.getX() % DISTANCE, currentPosition.getY() % DISTANCE);

    }

    public Id<Player> getPlayerId() {
        return playerId;
    }


    public Integer getScore() {
        return score;
    }

    public Point getPosition() {
        return currentPosition;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setDirection(Direction newDirection) {
        this.newDirection = newDirection;
    }
}