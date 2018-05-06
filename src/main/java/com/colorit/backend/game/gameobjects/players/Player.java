package com.colorit.backend.game.gameobjects.players;

import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.gameobjects.GameObject;
import com.colorit.backend.game.gameobjects.math.Point;

import static com.colorit.backend.game.GameConfig.*;

public class Player extends GameObject {
    protected String nickname;
    protected Integer score;
    protected Direction currentDirection;
    protected Direction newDirection;
    protected Point<Double> currentPosition;
    protected Integer velocity = DEFAULT_VELOCITY;

    public Player(String nickname) {
        this.nickname = nickname;
        this.score = 0;
        if (id.getId() == 1) {
            currentPosition = new Point<>(50.0,50.0);
            this.currentDirection = Direction.RIGHT;
        } else if (id.getId() == 2) {
            currentPosition = new Point<>( 750.0,50.0);
            this.currentDirection = Direction.DOWN;
        } else if (id.getId() == 3) {
            currentPosition = new Point<>(750.0,750.0);
            this.currentDirection = Direction.LEFT;
        } else {
            currentPosition = new Point<>(50.0,750.0);
            this.currentDirection = Direction.UP;
        }
        this.newDirection = currentDirection;
    }

    public  void move(double timeDelay) {
        final Double newVelocity = velocity * timeDelay;
        if (currentDirection == Direction.RIGHT) {
            currentPosition.setX(currentPosition.getX() + velocity);//newVelocity);
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

        Double minCoordinate = (double)(SQUARE_SIZE / 2);
        Double maxCoordinate = SQUARE_SIZE * DEFAULT_FILED_SIZE - minCoordinate;

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
