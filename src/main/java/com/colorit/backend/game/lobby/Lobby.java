package com.colorit.backend.game.lobby;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.session.GameSession;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class Lobby {
    private final static AtomicLong ID_GENERATOR = new AtomicLong(0);
    private Id<UserEntity> ownerId;
    private final Id<Lobby> id;
    private final GameSession associatedSession;
    private final Integer fieldSize;
    private final Integer gameTime;
    private final Boolean isMultiplayer;
    private State state;

    public enum State {
        WAITING,
        FILLED,
        STARTED,
        EMPTY
    }

    public Lobby(LobbySettings lobbySettings, Id<UserEntity> ownerId, GameSession gameSession) {
        this.state = State.WAITING;
        this.id = Id.of(ID_GENERATOR.getAndIncrement());
        this.ownerId = ownerId;
        id.setAdditionalInfo(lobbySettings.getName());
        this.associatedSession = gameSession;
        this.fieldSize = lobbySettings.getFieldSize();
        this.gameTime = lobbySettings.getGameTime();
        this.isMultiplayer = lobbySettings.getMultiplayer();
    }

    public Id<UserEntity> getOwnerId() {
        return ownerId;
    }

    public Id<Lobby> getId() {
        return id;
    }

    public GameSession getAssociatedSession() {
        return associatedSession;
    }

    public List<Id<UserEntity>> getUsers() {
        return associatedSession.getUsers();
    }

    public boolean changeOwner() {
        if (associatedSession.getUsers().size() == 0) {
            return false;
        }
        ownerId = associatedSession.getUsers().get(0);
        return true;
    }

    public Boolean isActive() {
        return state == State.WAITING || state == State.FILLED;
    }
}


