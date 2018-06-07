package com.colorit.backend.game.messages.output;

import com.colorit.backend.game.lobby.Lobby;

public class LobbyDeletedInfo extends LobbyOutMessage {
    private final long id;

    public LobbyDeletedInfo(Lobby lobby) {
        this.id = lobby.getId().getId();
    }

    public long getId() {
        return id;
    }
}
