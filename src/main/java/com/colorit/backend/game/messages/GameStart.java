package com.colorit.backend.game.messages;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.Point;
import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.websocket.Message;
import javafx.scene.shape.Mesh;

import java.util.List;

public class GameStart extends Message {
    private Id<UserEntity> self;
    private List<Id<UserEntity>> enemies;
    Point position;
    Direction direction;

    public GameStart() {}

}
