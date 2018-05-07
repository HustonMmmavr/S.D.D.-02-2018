package com.colorit.backend.game.lobby;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.session.GameSessionsController;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.websocket.RemotePointService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Service
public class LobbyController {
    @NotNull
    private final GameSessionsController gameSessionsController;
    @NotNull
    private final RemotePointService remotePointService;

    private final HashMap<Id<Lobby>, Lobby> lobbiesMap = new HashMap<>();

    public LobbyController(@NotNull GameSessionsController gameSessionsController,
                           @NotNull RemotePointService remotePointService) {
        this.gameSessionsController = gameSessionsController;
        this.remotePointService = remotePointService;
    }

    public void addUser(Id<Lobby> lId, Id<UserEntity> uId) {
        final Lobby lobby = lobbiesMap.get(lId);
//        if (lId == null) {
//            remotePointService.sendMessageToUser(uId, new ErrorLobbyMessage());
        // return;
//        }
        gameSessionsController.addUser(uId, lobby.getAssociatedSession());
    }

    public void removeUser(Id<UserEntity> uId, Id<Lobby> lId) {
        final Lobby lobby = lobbiesMap.get(lId);
        //        if (lId == null) {
//            remotePointService.sendMessageToUser(uId, new ErrorLobbyMessage());
        // return;
//        }
        gameSessionsController.removeUser(uId, lobby.getAssociatedSession());
//        if (lobby.getAssociatedSession().getUsers().size() == 0) {
//            delete lobby;
//        }
    }



    public void init(Id<UserEntity> uId, LobbySettings lobbySettings) {
        final GameSession gameSession = gameSessionsController.createSession();
        gameSessionsController.addUser(uId, gameSession);
        final Lobby lobby = new Lobby(lobbySettings, uId, gameSession);
        lobbiesMap.put(lobby.getId(), lobby);
    }
}
