package com.colorit.backend.game.messages.handlers;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.mechanics.GameMechanics;
import com.colorit.backend.game.messages.input.ClientSnapshot;
import com.colorit.backend.websocket.MessageHandler;
import com.colorit.backend.websocket.MessageHandlerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;

@Service
public class ClientSnapshotHandler extends MessageHandler<ClientSnapshot> {
    @NotNull
    private final GameMechanics gameMechanics;
    @NotNull
    private final MessageHandlerContainer messageHandlerContainer;

    public ClientSnapshotHandler(@NotNull GameMechanics gameMechanics,
                          @NotNull MessageHandlerContainer messageHandlerContainer) {
        super(ClientSnapshot.class);
        this.messageHandlerContainer = messageHandlerContainer;
        this.gameMechanics = gameMechanics;
    }

    @PostConstruct
    private void init() {
        messageHandlerContainer.registerHandler(ClientSnapshot.class, this);
    }


    @Override
    public void handle(@NotNull ClientSnapshot clientSnapshot, @NotNull Id<UserEntity> forUser) {
        gameMechanics.addClientSnapshot(forUser, clientSnapshot);
    }
}
