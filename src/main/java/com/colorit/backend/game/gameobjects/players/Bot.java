package com.colorit.backend.game.gameobjects.players;

import com.colorit.backend.entities.Id;
import com.colorit.backend.game.gameobjects.math.Point;

public class Bot extends Player {
    Bot(String nickname, Id<Player> id,  Point startPosition) {
        super(nickname, id, startPosition);
    }

    @Override
    public Point move(double delay, int minBorder, int maxBorder) {
        return new Point(0, 0);
    }

}
