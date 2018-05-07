package com.colorit.backend.game.messages.output;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.math.Point;
import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.websocket.Message;

import java.util.List;

public class GameStart extends Message {
    private Id<UserEntity> self;
    private List<Id<UserEntity>> enemies;
    Point<Double> position;
    Direction direction;

    public GameStart() {}

}
