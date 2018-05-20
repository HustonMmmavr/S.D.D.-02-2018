package com.colorit.backend.game.session;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.input.ClientSnapshot;
import com.colorit.backend.game.messages.output.LobbyError;
import com.colorit.backend.game.messages.services.ClientSnapshotService;
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
    @NotNull
    private final ClientSnapshotService clientSnapshotService;

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

    public void forceTerminate(@NotNull GameSession gameSession, boolean error) {
        // todo delete associated lobby or only remove session
        final boolean exists = gamesSessions.contains(gameSession);
        gameSession.setFinished();
        usersMap.remove(gameSession.getFirst().getUserId());
        usersMap.remove(gameSession.getSecond().getUserId());
        final CloseStatus status = error ? CloseStatus.SERVER_ERROR : CloseStatus.NORMAL;
        if (exists) {
            remotePointService.cutDownConnection(gameSession.getFirst().getUserId(), status);
            remotePointService.cutDownConnection(gameSession.getSecond().getUserId(), status);
        }
        gameSession.getUsers().forEach(clientSnapshotService::clearForUser);//user -> clientSnapshotService.clearForUser(user));
//                clientSnapshotsService.clearForUser(gameSession.getFirst().getUserId());
//        clientSnapshotsService.clearForUser(gameSession.getSecond().getUserId());

        LOGGER.info("Game session " + gameSession.getId() + (error ? " was terminated due to error. " : " was cleaned. ")
                + gameSession.toString());
    }

    public boolean checkHealthState(@NotNull GameSession gameSession) {
        return gameSession.getUsers().stream().allMatch(remotePointService::isConnected);
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