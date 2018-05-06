package com.colorit.backend.game;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.GameStart;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.websocket.RemotePointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameSessionsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSessionsController.class);
    private List<GameSession> gameSessions = new ArrayList<>(); // formed sessions
    private GameSession currentSession;
    private RemotePointService remotePointService;

    GameSessionsController(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
         currentSession = new GameSession(remotePointService);
    }

    public List<GameSession> getGameSessions() {
        return gameSessions;
    }

    public void addUser(Id<UserEntity> userId) {
        currentSession.addUser(userId);
        try {
            remotePointService.sendMessageToUser( userId, new Connected("hi " + userId.getAdditionalInfo()));
        } catch (IOException ignore) {

        }
        if (currentSession.isFullParty()) {
            gameSessions.add(currentSession);
            currentSession.setStatus(GameSession.Status.FILLED);
            try {
                for (Id<UserEntity> uId : currentSession.getUsers()) {
                    remotePointService.sendMessageToUser(uId, new GameStart());
                }
            } catch (IOException err) {
                LOGGER.error("GAME cant start");
            }
            currentSession = new GameSession(remotePointService);
        }
    }
}
