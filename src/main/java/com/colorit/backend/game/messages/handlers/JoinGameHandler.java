package com.colorit.backend.game.messages.handlers;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.input.JoinGame;
import com.colorit.backend.game.session.GameSessionsController;
import com.colorit.backend.websocket.MessageHandler;
import com.colorit.backend.websocket.MessageHandlerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Component
public class JoinGameHandler extends MessageHandler<JoinGame.Request> {
    @NotNull
    private final GameSessionsController gameSessionsController;
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public JoinGameHandler(@NotNull GameSessionsController gameSessionsController,
                           @NotNull MessageHandlerContainer messageHandlerContainer) {

        super(JoinGame.Request.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.gameSessionsController = gameSessionsController;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(JoinGame.Request.class, this);
    }

    @Override
    public void handle(@NotNull JoinGame.Request message, @NotNull Id<UserEntity> forUser) {
        gameSessionsController.addUser(forUser);
    }
}
