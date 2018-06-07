package com.colorit.backend.game.gameobjects.players;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.math.Point;

public class Bot extends Player {
    public Bot(Id<UserEntity> userId, Id<Player> id, Point startPosition) {
        super(userId, id, startPosition);
    }

    @Override
    public boolean move(long delay, int minBorder, int maxBorder) {
        return true;
    }
}
