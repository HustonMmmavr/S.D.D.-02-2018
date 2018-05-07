package com.colorit.backend.game;

import com.colorit.backend.entities.Id;
import com.colorit.backend.game.messages.input.LobbyMessage;
import com.colorit.backend.websocket.HandleException;
import com.colorit.backend.websocket.Message;
import com.colorit.backend.websocket.MessageHandler;

import javax.validation.constraints.NotNull;

public class LobbyMessageHandler extends MessageHandler {
    @NotNull GameSessionsController gameSessionsController;

    public LobbyMessageHandler() {
        super(LobbyMessage.class);
    }

    @Override
    public void handleMessage(@NotNull Message message, @NotNull Id forUser) throws HandleException {
        super.handleMessage(message, forUser);
    }

    @Override
    public void handle(Message message, @NotNull Id forUser) throws HandleException {

    }
}
