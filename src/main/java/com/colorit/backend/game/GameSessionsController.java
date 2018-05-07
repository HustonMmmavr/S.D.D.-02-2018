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
import java.util.*;

@Service
public class GameSessionsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSessionsController.class);
    private GameSession currentSession;
    private RemotePointService remotePointService;
    @NotNull
    private final Set<GameSession> gamesSessions = new LinkedHashSet<>();

    private HashMap<Id<UserEntity>, GameSession> gameUserSessions = new HashMap<>();

    GameSessionsController(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
        currentSession = new GameSession(remotePointService);
    }


    // connected to lobby
    public void createSession() {

    }

    public HashMap<Id<UserEntity>, GameSession> getGameUserSessions() {return gameUserSessions;}

    public Set<GameSession> getGameSessions() {
        return gamesSessions;
    }

    public void addUser(Id<UserEntity> userId) {
        currentSession.addUser(userId);
        gameUserSessions.put(userId, currentSession);
        try {
            remotePointService.sendMessageToUser( userId, new Connected("hi " + userId.getAdditionalInfo()));
        } catch (IOException ignore) {

        }
        if (currentSession.isFullParty()) {
            gamesSessions.add(currentSession);
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
