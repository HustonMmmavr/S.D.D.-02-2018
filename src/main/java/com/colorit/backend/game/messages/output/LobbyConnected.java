package com.colorit.backend.game.messages.output;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.lobby.Lobby;

import java.util.ArrayList;
import java.util.List;

public class LobbyConnected extends LobbyOutMessage {
    private Id<UserEntity> owner;
    private List<Id<UserEntity>> users;
    private long id;
    private String name;
    private int fieldSize;
    private long gameTime;

    public LobbyConnected(Lobby lobby) {
        this.users = new ArrayList<>(lobby.getUsers());
        this.name = lobby.getId().getAdditionalInfo();
        this.id = lobby.getId().getId();
        this.owner = lobby.getOwnerId();
        this.fieldSize = lobby.getFiledSize();
        this.gameTime = lobby.getGameTime();
    }

    public LobbyConnected(List<Id<UserEntity>> users, Id<Lobby> lobbyId, Id<UserEntity> owner, int fieldSize, long gameTime) {
        this.users = new ArrayList<>();
        this.name = lobbyId.getAdditionalInfo();
        this.owner = owner;
        this.id = lobbyId.getId();
        this.fieldSize = fieldSize;
        this.users.addAll(users);
        this.gameTime = gameTime;
    }

    public List<Id<UserEntity>> getUsers() {
        return users;
    }

    public Id<UserEntity> getOwner() {
        return owner;
    }

    public void setOwner(Id<UserEntity> owner) {
        this.owner = owner;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public long getGameTime() {
        return gameTime;
    }
}
