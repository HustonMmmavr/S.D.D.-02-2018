package com.colorit.backend.game.messages.output;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.lobby.Lobby;
import com.colorit.backend.websocket.Message;

import java.util.ArrayList;
import java.util.List;

public class LobbyConnected extends Message {
    private String owner;
    private long id;
    private String name;
    private int fieldSize;
    private long gameTime;
    private List<String> users;

    public LobbyConnected(List<Id<UserEntity>> users, Id<Lobby> lobbyId, String owner, int fieldSize, long gameTime) {
        this.users = new ArrayList<>();
        this.name = lobbyId.getAdditionalInfo();
        this.owner = owner;
        this.id = lobbyId.getId();
        this.fieldSize = fieldSize;
        users.forEach(user -> this.users.add(user.getAdditionalInfo()));
        this.gameTime = gameTime;
    }

    public List<String> getUsers() {
        return users;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
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
