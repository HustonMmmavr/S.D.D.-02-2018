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
import java.util.ArrayList;
import java.util.List;

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

    public List<Id<UserEntity>> sendMessageToLobby(LobbyOutMessage message, List<Id<UserEntity>> users) {
        final List<Id<UserEntity>> deadUsers = new ArrayList<>();
        for (var user: users) {
            try {
                remotePointService.sendMessageToUser(user, message);
            } catch (IOException iOEx) {
                LOGGER.error("{} is unreacheble", user);
                deadUsers.add(user);
            }
        }
        return deadUsers;
    }

    public boolean sendLobbyMessage(LobbyOutMessage message, List<Id<UserEntity>> userIds, List<Id<UserEntity>> deadUsers) {
        boolean existDeadUsers = false;
        for (var userId: userIds) {
            try {
                remotePointService.sendMessageToUser(userId, message);
            } catch (IOException iOEx) {
                LOGGER.error("User {} is unreacheble", userId);
                deadUsers.add(userId);
                existDeadUsers = true;
            }
        }
        return existDeadUsers;
    }
}
