package com.colorit.backend.game.lobby;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.session.GameSession;

import java.util.concurrent.atomic.AtomicLong;

public class Lobby {
    private final static AtomicLong ID_GENERATOR = new AtomicLong(0);
    private final Id<UserEntity> ownerId;
    private final Id<Lobby> id;
    private final GameSession associatedSession;

    public Lobby(LobbySettings lobbySettings, Id<UserEntity> ownerId, GameSession gameSession) {
        this.id = Id.of(ID_GENERATOR.getAndIncrement());
        this.ownerId = ownerId;
        this.associatedSession = gameSession;
    }

    public Id<Lobby> getId() {
        return id;
    }

    public GameSession getAssociatedSession() {
        return associatedSession;
    }
}
