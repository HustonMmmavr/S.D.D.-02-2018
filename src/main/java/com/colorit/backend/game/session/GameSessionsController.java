package com.colorit.backend.game.session;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.output.Connected;
import com.colorit.backend.game.messages.output.GameStart;
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

    GameSessionsController(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    // connected to lobby
    public GameSession createSession(Integer fieldSize) {
        final GameSession gameSession = new GameSession(remotePointService, fieldSize);
        gamesSessions.add(gameSession);
        // todo add session to list

        return gameSession;
    }

    public void deleteSession(GameSession gameSession) {
        gameSession.getUsers().forEach(user -> gameUserSessions.remove(user));
        gamesSessions.remove(gameSession);
    }

    public HashMap<Id<UserEntity>, GameSession> getGameUserSessions() {return gameUserSessions;}

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
        try {
            remotePointService.sendMessageToUser( uId, new Connected("hi " + uId.getAdditionalInfo()));
        } catch (IOException ignore) {

        }
        if (gameSession.isFullParty()) {
            gameSession.startSession();
            gameSession.setStatus(GameSession.Status.FILLED);
            try {
                for (Id<UserEntity> user : gameSession.getUsers()) {
                    remotePointService.sendMessageToUser(user, new GameStart());
                }
            } catch (IOException err) {
                LOGGER.error("GAME cant start");
            }
        }
    }
}

//    public void addUser(Id<UserEntity> userId) {
//        currentSession.addUser(userId);
//        gameUserSessions.put(userId, currentSession);
//        try {
//            remotePointService.sendMessageToUser( userId, new Connected("hi " + userId.getAdditionalInfo()));
//        } catch (IOException ignore) {
//
//        }
//        if (currentSession.isFullParty()) {
//            gamesSessions.add(currentSession);
//            currentSession.setStatus(GameSession.Status.FILLED);
//            try {
//                for (Id<UserEntity> uId : currentSession.getUsers()) {
//                    remotePointService.sendMessageToUser(uId, new GameStart());
//                    Thread.sleep(1000);
//                }
//            } catch (Exception e) { //IOException err) {
//                LOGGER.error("GAME cant start");
//            }
//            currentSession = new GameSession(remotePointService);
//        }
//    }
//    private GameSession currentSession;
//        currentSession = new GameSession(remotePointService);
