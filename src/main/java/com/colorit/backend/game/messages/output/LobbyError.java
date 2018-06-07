package com.colorit.backend.game.messages.output;

public class LobbyError extends LobbyOutMessage {
    private String message;

    public LobbyError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
