package com.colorit.backend.game.lobby;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.session.GameSession;

import java.util.concurrent.atomic.AtomicLong;

public class Lobby {
    private final static AtomicLong ID_GENERATOR = new AtomicLong(0);
    private Id<UserEntity> ownerId;
    private final Id<Lobby> id;
    private final GameSession associatedSession;
    private final Integer fieldSize;
    private final Integer gameTime;
    private final Boolean isMultiplayer;

    public Lobby(LobbySettings lobbySettings, Id<UserEntity> ownerId, GameSession gameSession) {
        this.id = Id.of(ID_GENERATOR.getAndIncrement());
        this.ownerId = ownerId;
        this.associatedSession = gameSession;
        this.fieldSize = lobbySettings.getFieldSize();
        this.gameTime = lobbySettings.getGameTime();
        this.isMultiplayer = lobbySettings.getMultiplayer();
    }

    public Id<Lobby> getId() {
        return id;
    }

    public GameSession getAssociatedSession() {
        return associatedSession;
    }

    public boolean changeOwner() {
        if (associatedSession.getUsers().size() == 0) {
            return false;
        }
        ownerId = associatedSession.getUsers().get(0);
        return true;
    }
}
