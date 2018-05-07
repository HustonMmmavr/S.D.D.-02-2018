package com.colorit.backend.game.lobby;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.GameSessionsController;
import com.colorit.backend.game.session.GameSession;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

@Service
public class LobbyController {
    @NotNull
    private final GameSessionsController gameSessionsController;

    private final HashMap<Id<Lobby>, Lobby> lobbiesMap = new HashMap<>();

    public LobbyController(@NotNull GameSessionsController gameSessionsController) {
        this.gameSessionsController = gameSessionsController;
    }

    // we need to send to gamesessioncontroller uid, gamesession
    // if remove need delete this
    // also need



    public void addUser(Id<Lobby> lId, Id<UserEntity> uId) {
        final Lobby lobby = lobbiesMap.get(lId);
        // TODO chekc null
        gameSessionsController.addUser(uId, lobby.getAssociatedSession());
//        lobby.getAssociatedSession().addUser(uId);
    }

    public void removeUser() {

    }



    public void init(Id<UserEntity> uId, LobbySettings lobbySettings) {
        final GameSession gameSession = gameSessionsController.createSession();
        gameSessionsController.addUser(uId, gameSession);
        final Lobby lobby = new Lobby(lobbySettings, uId, gameSession);
        lobbiesMap.put(lobby.getId(), lobby);
    }
}
