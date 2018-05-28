package com.colorit.backend.game.messages.handlers;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.lobby.LobbyController;
import com.colorit.backend.game.messages.input.LobbyMessage;
import com.colorit.backend.websocket.HandleException;
import com.colorit.backend.websocket.MessageHandler;
import com.colorit.backend.websocket.MessageHandlerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Service
public class LobbyHandler extends MessageHandler<LobbyMessage> {
    private final @NotNull LobbyController lobbyController;
    private final @NotNull MessageHandlerContainer messageHandlerContainer;

    public LobbyHandler(@NotNull LobbyController lobbyController,
                        @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(LobbyMessage.class);
        this.lobbyController = lobbyController;
        this.messageHandlerContainer = messageHandlerContainer;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(LobbyMessage.class, this);
    }

    @Override
    public void handle(@NotNull LobbyMessage message, @NotNull Id<UserEntity> forUser) throws HandleException {
        switch (message.getAction()) {
            case CONNCECT:
                lobbyController.addUser(Id.of(message.getLobbyId()), forUser);
                break;
            case CREATE:
                lobbyController.init(forUser, message.getSettings());
                break;
            case CHAT:
                break;
            case DISCONNECT:
                lobbyController.removeUser(Id.of(message.getLobbyId()), forUser);
                break;
            case START:
                lobbyController.startLobby(forUser, Id.of(message.getLobbyId()));
                break;
            default:
                break;

        }
    }
}
