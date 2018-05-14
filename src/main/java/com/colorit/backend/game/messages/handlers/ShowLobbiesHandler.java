package com.colorit.backend.game.messages.handlers;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.lobby.LobbyController;
import com.colorit.backend.game.messages.input.ShowLobbies;
import com.colorit.backend.websocket.MessageHandler;
import com.colorit.backend.websocket.MessageHandlerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Service
public class ShowLobbiesHandler extends MessageHandler<ShowLobbies.Request> {
    @NotNull
    private final LobbyController lobbyController;
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public ShowLobbiesHandler(@NotNull LobbyController lobbyController,
                              @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(ShowLobbies.Request.class);
        this.lobbyController = lobbyController;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(ShowLobbies.Request.class, this);
    }

    @Override
    public void handle(@NotNull ShowLobbies.Request message, @NotNull Id<UserEntity> forUser) {
        lobbyController.getLobbies();
    }
}
