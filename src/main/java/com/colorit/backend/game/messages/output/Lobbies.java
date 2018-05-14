package com.colorit.backend.game.messages.output;

import com.colorit.backend.entities.Id;
import com.colorit.backend.game.lobby.Lobby;
import com.colorit.backend.websocket.Message;

import java.util.List;

public class Lobbies extends Message {
    List<Id<Lobby>> lobbies;

    public Lobbies(List<Id<Lobby>> lobbies) {
        this.lobbies = lobbies;
    }

    public List<Id<Lobby>> getLobbies() {
        return lobbies;
    }

    public void setLobbies(List<Id<Lobby>> lobbies) {
        this.lobbies = lobbies;
    }
}
