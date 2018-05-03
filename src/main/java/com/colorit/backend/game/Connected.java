package com.colorit.backend.game;

import com.colorit.backend.websocket.Message;

public class Connected extends Message {
    private String message;
    public Connected(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
