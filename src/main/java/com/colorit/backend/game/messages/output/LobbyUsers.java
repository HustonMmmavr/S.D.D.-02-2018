package com.colorit.backend.game.messages.output;

import com.colorit.backend.entities.Id;
import com.colorit.backend.game.lobby.Lobby;
import com.colorit.backend.websocket.Message;

import java.util.List;

public class LobbyUsers extends Message {
    Id<Lobby> lobbyId;
    private List<String> users;

    public LobbyUsers(Id<Lobby> lId, List<String> message) {
        this.lobbyId = lId;
        this.users = users;
    }

    public List<String> getMessage() {
        return users;
    }

    public Id<Lobby> getLobbyId() {
        return lobbyId;
    }
}
