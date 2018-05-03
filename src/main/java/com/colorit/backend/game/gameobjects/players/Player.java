package com.colorit.backend.game.gameobjects.players;

import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.gameobjects.Point;

import static com.colorit.backend.game.GameConfig.*;

public class Player {
    protected String nickname;
    protected Integer id;
    protected Integer score;
    protected Direction currentDirection;
    protected Direction newDirection;
    protected Point<Integer> currentPosition;
    protected Integer velocity = DEFAULT_VELOCITY;

    public Player(String nickname, Integer id) {
        this.nickname = nickname;
        this.score = 0;
        this.id = id;
        if (id == 1) {
            currentPosition = new Point<>(50,50);
            this.currentDirection = Direction.RIGHT;
        } else if (id == 2) {
            currentPosition = new Point<>( 750,50);
            this.currentDirection = Direction.DOWN;
        } else if (id == 3) {
            currentPosition = new Point<>(750,750);
            this.currentDirection = Direction.LEFT;
        } else {
            currentPosition = new Point<>(50,750);
            this.currentDirection = Direction.UP;
        }
        this.newDirection = currentDirection;
    }

    public  void move() {
        if (currentDirection == Direction.RIGHT) {
            currentPosition.setX(currentPosition.getX() + velocity);
        }
        if (currentDirection == Direction.DOWN) {
            currentPosition.setY(currentPosition.getY() + velocity);
        }
        if (currentDirection == Direction.LEFT) {
            currentPosition.setX(currentPosition.getX() - velocity);
        }
        if (currentDirection == Direction.UP) {
            currentPosition.setY(currentPosition.getY() - velocity);
        }

        Integer minCoordinate = SQUARE_SIZE / 2;
        Integer maxCoordinate = SQUARE_SIZE * DEFAULT_FILED_SIZE - minCoordinate;

        if (currentPosition.getX() < minCoordinate) {
            currentPosition.setX(minCoordinate);
        }

        if (currentPosition.getX() > maxCoordinate) {
            currentPosition.setX(maxCoordinate);
        }

        if (currentPosition.getY() < minCoordinate) {
            currentPosition.setY(minCoordinate);
        }

        if (currentPosition.getY() > maxCoordinate) {
            currentPosition.setY(maxCoordinate);
        }

        if (((currentPosition.getX() - minCoordinate) % SQUARE_SIZE == 0) &&
                ((currentPosition.getY() - minCoordinate) % SQUARE_SIZE == 0)) {
            currentDirection = newDirection;
        }
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
