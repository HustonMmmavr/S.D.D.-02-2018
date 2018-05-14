package com.colorit.backend.game.messages.output;

import com.colorit.backend.websocket.Message;

public class LobbyError extends Message {
    private String message;

    public LobbyError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
