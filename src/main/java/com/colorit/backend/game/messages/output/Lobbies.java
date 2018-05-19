package com.colorit.backend.game.messages.output;

import com.colorit.backend.websocket.Message;

import java.util.List;

public class Lobbies extends Message {

    private final List<OneLobbyInfo> lobbies;

    public Lobbies(List<OneLobbyInfo> lobbies) {
        this.lobbies = lobbies;
    }

    public List<OneLobbyInfo> getLobbies() {
        return lobbies;
    }
}