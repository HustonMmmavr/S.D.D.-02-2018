package com.colorit.backend.game.session;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.output.LobbyError;
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
    @NotNull
    private RemotePointService remotePointService;
    @NotNull
    private final Set<GameSession> gamesSessions = new LinkedHashSet<>();

    private HashMap<Id<UserEntity>, GameSession> gameUserSessions = new HashMap<>();

    public GameSessionsController(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    // connected to lobby
    public GameSession createSession(Integer fieldSize, long gameTime) {
        final GameSession gameSession = new GameSession(this, fieldSize, gameTime);
        gamesSessions.add(gameSession);
        return gameSession;
    }

    public void deleteSession(GameSession gameSession) {
        gameSession.getUsers().forEach(user -> gameUserSessions.remove(user));
        gamesSessions.remove(gameSession);
    }

    public HashMap<Id<UserEntity>, GameSession> getGameUserSessions() {
        return gameUserSessions;
    }

    public Set<GameSession> getGameSessions() {
        return gamesSessions;
    }

    public void removeUser(Id<UserEntity> uId, GameSession gameSession) {
        gameUserSessions.remove(uId);
        gameSession.removeUser(uId);
    }

    public void terminateSession(GameSession gameSession, boolean terminat) {

    }

    public void addUser(Id<UserEntity> uId, GameSession gameSession) {
        gameUserSessions.put(uId, gameSession);
        gameSession.addUser(uId);

        if (gameSession.isFullParty()) {
            gameSession.startSession();
            gameSession.setStatus(GameSession.Status.FILLED);
            try {
                for (Id<UserEntity> user : gameSession.getUsers()) {
                    remotePointService.sendMessageToUser(user, new LobbyError("s"));
                }
            } catch (IOException err) {
                LOGGER.error("GAME cant start");
            }
        }
    }
}

//        final GameSession gameSession = new GameSession(remotePointService, this, fieldSize, gameTime);
