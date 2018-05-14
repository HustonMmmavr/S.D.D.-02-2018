package com.colorit.backend.game.messages.output;

import com.colorit.backend.websocket.Message;

import java.util.List;

public class LobbyUsers extends Message {
    private List<String> users;

    public LobbyUsers(List<String> message) {
        this.users = users;
    }

    public List<String> getMessage() {
        return users;
    }
}
