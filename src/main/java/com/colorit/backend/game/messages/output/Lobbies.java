package com.colorit.backend.game.messages.output;

import java.util.List;

public class Lobbies extends LobbyOutMessage {

    private final List<OneLobbyInfo> lobbies;

    public Lobbies(List<OneLobbyInfo> lobbies) {
        this.lobbies = lobbies;
    }

    public List<OneLobbyInfo> getLobbies() {
        return lobbies;
    }
}