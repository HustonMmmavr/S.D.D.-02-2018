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
import java.util.stream.Collectors;

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
//        gameSession.setFinished();
//        usersMap.remove(gameSession//    @Override
//    public void changeDirection(@NotNull Id<UserEntity> userId, @NotNull Direction direction) {
//        GameSession gameSession = gameSessionsController.getGameUserSessions().get(userId);
////        if (gameSession != null) {
////            gameSession.changeDirection(userId, direction);
//        }
////        gameSessionsController.getGameSessions().forEach();
//    }.getFirst().getUserId());
//        usersMap.remove(gameSession.getSecond().getUserId());
//        final CloseStatus status = error ? CloseStatus.SERVER_ERROR : CloseStatus.NORMAL;
//        if (exists) {
//            remotePointService.cutDownConnection(gameSession.getFirst().getUserId(), status);
//            remotePointService.cutDownConnection(gameSession.getSecond().getUserId(), status);
//        }
//        gameSession.getUsers().forEach(clientSnapshotService::clearForUser);//user -> clientSnapshotService.clearForUser(user));
//                clientSnapshotsService.clearForUser(gameSession.getFirst().getUserId());
//        clientSnapshotsService.clearForUser(gameSession.getSecond().getUserId());
//
//        LOGGER.info("Game session " + gameSession.getId() + (error ? " was terminated due to error. " : " was cleaned. ")
//                + gameSession.toString());
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

//    public List<Id<UserEntity>> checkHealthState(@NotNull GameSession gameSession) {
//        final List<Id<UserEntity>> deadUsers = new ArrayList<>();
//        gameSession.getUsers().forEach(user -> {
//            if (!remotePointService.isConnected(user)) {
//                deadUsers.add(user);
//            }
//        });
//        return  deadUsers;
//    }

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
        if (gameSession.isReady()) {
            gameSession.setWaiting();
        }
        gameSession.removeUser(uId);
    }

    public void addUser(Id<UserEntity> uId, GameSession gameSession) {
        gameUserSessions.put(uId, gameSession);
        gameSession.addUser(uId);
        if (gameSession.isFullParty()) {
            gameSession.setReady();
        }
    }
}


//    public Lis
//        return gameSession.getUsers().stream().findAny()//filter(remotePointService::isConnected)//.//ecollect(Collectors.toList());
//    public boolean checkHealthState(@NotNull GameSession gameSession) {
//
//        return gameSession.getUsers().stream().allMatch(remotePointService::isConnected);

