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
    private State state;

    public enum State {
        WAITING,
        READY,
        GAME,
        FINISHED
    }

    public Lobby(LobbySettings lobbySettings, Id<UserEntity> ownerId, GameSession gameSession) {
        this.state = State.WAITING;
        this.id = Id.of(ID_GENERATOR.getAndIncrement());
        this.ownerId = ownerId;
        id.setAdditionalInfo(lobbySettings.getName());
        this.associatedSession = gameSession;
    }

    public State getState() {
        return state;
    }

    public void setReady() {
        associatedSession.setReady();
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setWaiting() {
        this.associatedSession.setWaiting();
    }



    public int getFiledSize() {
        return associatedSession.getFieldSize();
    }

    public long getGameTime() {
        return associatedSession.getGameTime() / 1000;
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

    public boolean isAlive() {
//        if (associatedSession.getUsers().isEmpty()) {
//            return false;
//        }
        return !associatedSession.getUsers().isEmpty();
     }

    public List<Id<UserEntity>> getUsers() {
        return associatedSession.getUsers();
    }

    public boolean changeOwner() {
        if (associatedSession.getUsers().isEmpty()) {
            return false;
        }
        ownerId = associatedSession.getUsers().get(0);
        return true;
    }

    public boolean isFinished() {
        return associatedSession.isFinised();
    }

    public Boolean isActive() {
        return state == State.WAITING || state == State.READY;
    }
}


//    private final Integer fieldSize;\
//    private final Integer gameTime;
//    private final Boolean isMultiplayer;
//        this.fieldSize = lobbySettings.getFieldSize();
//        this.gameTime = lobbySettings.getGameTime();
//        this.isMultiplayer = lobbySettings.getMultiplayer();