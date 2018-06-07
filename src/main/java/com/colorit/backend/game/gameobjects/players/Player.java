package com.colorit.backend.game.gameobjects.players;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.gameobjects.GameObject;
import com.colorit.backend.game.gameobjects.Snap;
import com.colorit.backend.game.gameobjects.math.Point;

import static com.colorit.backend.game.GameConfig.*;

public class Player extends GameObject {
    private String nickname;
    private Integer score;
    private int offset;
    private Direction currentDirection;
    private Direction newDirection;
    private Point currentPosition;
    private Integer velocity = DEFAULT_VELOCITY;
    private Id<Player> playerId;
    private boolean addScore;
    private Id<UserEntity> userId;


    // передвигать по клиентскому фрейм тайму тику
    // интерполяцию на клиенте
    // каждый фрейм с клиента посылать
    // два фрейма с сервера
    // елсли произошли проблемы с браузером то применить последний снепшот
    // либо последний либо предпоследий
    //
    public Player(Id<UserEntity> userId, Id<Player> playerId, Point startPoint) {
        this.nickname = userId.getAdditionalInfo();
        this.score = 0;
        this.playerId = playerId;
        this.offset = 0;
        this.currentPosition = startPoint;
        this.addScore = false;
        this.userId = userId;
        playerId.setAdditionalInfo(userId.getAdditionalInfo());

        if (playerId.getId() == 1) {
            this.init(startPoint, Direction.RIGHT);
        } else if (playerId.getId() == 2) {
            this.init(startPoint, Direction.DOWN);
        } else if (playerId.getId() == 3) {
            this.init(startPoint, Direction.LEFT);
        } else {
            this.init(startPoint, Direction.UP);
        }
    }

    public Id<UserEntity> getUserId() {
        return userId;
    }


    public boolean isAddScore() {
        return addScore;
    }

    public void setAddScore(boolean addScore) {
        this.addScore = addScore;
    }

    private void init(Point startPoint, Direction startDirection) {
        this.currentPosition = startPoint;
        this.currentDirection = startDirection;
        this.newDirection = currentDirection;
    }


    // ставить таску на бонус
    public boolean move(long timeDelay, int minBorder, int maxBorder) {
        offset += (double) timeDelay / ONE_TIME_STEP * velocity;

        if ((currentPosition.getPosX() == minBorder && currentDirection == Direction.LEFT)
            || (currentPosition.getPosX() == maxBorder && currentDirection == Direction.RIGHT)
            || (currentPosition.getPosY() == minBorder && currentDirection == Direction.UP)
            || (currentPosition.getPosY() == maxBorder && currentDirection == Direction.DOWN)) {
            offset = 0;
        }

        final boolean isOnCell = offset >= DISTANCE || offset == 0;
        if (isOnCell) {
            offset %= DISTANCE;
            if (currentDirection == Direction.RIGHT) {
                currentPosition.setPosX(currentPosition.getPosX() >= maxBorder
                        ? maxBorder : currentPosition.getPosX() + 1);
            }
            if (currentDirection == Direction.DOWN) {
                currentPosition.setPosY(currentPosition.getPosY() >= maxBorder
                        ? maxBorder : currentPosition.getPosY() + 1);
            }
            if (currentDirection == Direction.LEFT) {
                currentPosition.setPosX(currentPosition.getPosX() <= minBorder
                        ? minBorder : currentPosition.getPosX() - 1);
            }
            if (currentDirection == Direction.UP) {
                currentPosition.setPosY(currentPosition.getPosY() <= minBorder
                        ? minBorder : currentPosition.getPosY() - 1);
            }

            if (newDirection != null && currentDirection != newDirection) {
                offset = 0;
                currentDirection = newDirection;
            }
        }

        return isOnCell;
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

    public void setNewDirection(Direction newDirection) {
        this.newDirection = newDirection;
    }

    public String getNickname() {
        return nickname;
    }

    public int getOffset() {
        return offset;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public Direction getNewDirection() {
        return newDirection;
    }

    public Point getCurrentPosition() {
        return currentPosition;
    }

    public Integer getVelocity() {
        return velocity;
    }

    @Override
    public PlayerSnap getSnap() {
        return new PlayerSnap(this);
    }

    public static class PlayerSnap implements Snap<Player> {
        private Point position;
        private Direction direction;
        private Integer score;
        private Integer id;
        private Integer velocity;
        private Integer offset;
        private Direction newDirection;

        //        public PlayerSnap(Direction direction, Integer score, Integer id, Integer velocity, Direction newDirection) {
        public PlayerSnap(Player player) {
            this.position = player.getPosition();
            this.direction = player.getCurrentDirection();
            this.score = player.getScore();
            this.id = (int) player.getPlayerId().getId();
            this.velocity = player.getVelocity();
            this.newDirection = player.getNewDirection();
            this.offset = player.getOffset();
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public Point getPosition() {
            return position;
        }

        public void setPosition(Point position) {
            this.position = position;
        }

        public Direction getDirection() {
            return direction;
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getVelocity() {
            return velocity;
        }

        public void setVelocity(Integer velocity) {
            this.velocity = velocity;
        }

        public Direction getNewDirection() {
            return newDirection;
        }

        public void setNewDirection(Direction newDirection) {
            this.newDirection = newDirection;
        }
    }
}