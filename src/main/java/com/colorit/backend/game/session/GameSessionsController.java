package com.colorit.backend.game.session;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.services.ClientSnapshotService;
import com.colorit.backend.websocket.RemotePointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
public class GameSessionsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSessionsController.class);
    private @NotNull RemotePointService remotePointService;
    private final @NotNull Set<GameSession> gamesSessions = new LinkedHashSet<>();

    private HashMap<Id<UserEntity>, GameSession> gameUserSessions = new HashMap<>();
    private final @NotNull ClientSnapshotService clientSnapshotService;

    public void forceTerminate(@NotNull GameSession gameSession, boolean error) {
        // todo delete associated lobby or only remove session
        final boolean exists = gamesSessions.contains(gameSession);
    }



    public GameSessionsController(@NotNull RemotePointService remotePointService,
                                  @NotNull ClientSnapshotService clientSnapshotService) {
        this.remotePointService = remotePointService;
        this.clientSnapshotService = clientSnapshotService;
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

    public void removeUser(Id<UserEntity> userId, GameSession gameSession) {
        gameUserSessions.remove(userId);
        if (gameSession.isReady()) {
            gameSession.setWaiting();
        }
        gameSession.removeUser(userId);
    }

    public void addUser(Id<UserEntity> userId, GameSession gameSession) {
        gameUserSessions.put(userId, gameSession);
        gameSession.addUser(userId);
        if (gameSession.isFullParty()) {
            gameSession.setReady();
        }
    }
}

