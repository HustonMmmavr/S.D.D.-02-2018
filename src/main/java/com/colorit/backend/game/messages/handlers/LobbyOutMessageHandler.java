package com.colorit.backend.game.messages.handlers;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.output.LobbyOutMessage;
import com.colorit.backend.websocket.RemotePointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@Service
public class LobbyOutMessageHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LobbyOutMessageHandler.class);
    @NotNull
    private final RemotePointService remotePointService;

    LobbyOutMessageHandler(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public boolean sendMessageToUser(LobbyOutMessage message, Id<UserEntity> user) {
        try {
            remotePointService.sendMessageToUser(user, message);
        } catch (IOException iOEx) {
            LOGGER.error("cant send message to user {}", user);
            return false;
        }
        return true;
    }
}
