package com.colorit.backend.game.lobby;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.output.Lobbies;
import com.colorit.backend.game.messages.output.LobbyError;
import com.colorit.backend.game.messages.output.LobbyUsers;
import com.colorit.backend.game.session.GameSessionsController;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.services.IUserService;
import com.colorit.backend.services.UserServiceJpa;
import com.colorit.backend.websocket.RemotePointService;
import org.springframework.stereotype.Service;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;

@Service
public class LobbyController {
    @NotNull
    private final GameSessionsController gameSessionsController;
    @NotNull
    private final RemotePointService remotePointService;

    @NotNull
    private final IUserService userService;

    private final HashMap<Id<Lobby>, Lobby> lobbiesMap = new HashMap<>();
    private final Set<Lobby> lobbies = new HashSet<>();

    public LobbyController(@NotNull GameSessionsController gameSessionsController,
                           @NotNull RemotePointService remotePointService,
                           @NotNull IUserService userService) {
        this.gameSessionsController = gameSessionsController;
        this.remotePointService = remotePointService;
        this.userService = userService;
    }

    public void addUser(Id<Lobby> lId, Id<UserEntity> uId) {
        final Lobby lobby = lobbiesMap.get(lId);
        if (lId == null || lobbiesMap.get(lId) == null) {
            try {
                remotePointService.sendMessageToUser(uId, new LobbyError("Sorry lobby not found"));
            } catch (IOException ignore) {

            }
            return;
        }
//        try {
        gameSessionsController.addUser(uId, lobby.getAssociatedSession());
        //remotePointService.sendMessageToUser(uId, new LobbyConnected());
    }

    private boolean insureCandidate(@NotNull Id<UserEntity> candidate) {
        return remotePointService.isConnected(candidate)
                && userService.getUser(candidate.getAdditionalInfo()) != null;
    }


    public void removeUser(Id<Lobby> lId, Id<UserEntity> uId) {
        final Lobby lobby = lobbiesMap.get(lId);
        if (lId == null) {
            try {
                remotePointService.sendMessageToUser(uId, new LobbyError("Sorry lobby not found"));
            } catch (IOException ignore) {

            }
            return;
        }

        gameSessionsController.removeUser(uId, lobby.getAssociatedSession());
        if (lobby.getAssociatedSession().getUsers().isEmpty()) {
            gameSessionsController.deleteSession(lobby.getAssociatedSession());
            lobbies.remove(lobby);
        }
    }

    public void getLobbyUsers(Id<Lobby> lId, Id<UserEntity> uId) {
        final Lobby lobby = lobbiesMap.get(lId);
        try {
            if (lobby == null) {
                remotePointService.sendMessageToUser(uId, new LobbyError("Sorry lobby not found"));
                return;
            }
            final List<String> users = new ArrayList<>();
            lobby.getUsers().forEach(user -> users.add(user.getAdditionalInfo()));
            remotePointService.sendMessageToUser(uId, new LobbyUsers(lId, users));
        } catch (IOException ignore) {

        }
    }

    public void getLobbies(Id<UserEntity> uId) {
        try {
            final List<Lobbies.OneLobby> lobbiesList = new ArrayList<>();
            for (Id<Lobby> lId : lobbiesMap.keySet()) {
                final Lobby lobby = lobbiesMap.get(lId);
                if (lobby != null && lobby.isActive()) {
                    lobbiesList.add(new Lobbies.OneLobby(lId.getId(), lId.getAdditionalInfo(),
                            lobby.getUsers().size(), lobby.getOwnerId().getAdditionalInfo(),
                            lobby.getFiledSize(), lobby.getGameTime()));
                }
            }
            remotePointService.sendMessageToUser(uId, new Lobbies(lobbiesList));
        } catch (IOException ignore) {
        }
    }

    public void init(Id<UserEntity> uId, LobbySettings lobbySettings) {
        try {
            if (gameSessionsController.getGameUserSessions().get(uId) != null) {
                remotePointService.sendMessageToUser(uId, new LobbyError("You cant create while you already play"));
                return;
            }
            final GameSession gameSession = gameSessionsController.createSession(lobbySettings.getFieldSize(),
                    lobbySettings.getGameTime());
            gameSessionsController.addUser(uId, gameSession);
            final Lobby lobby = new Lobby(lobbySettings, uId, gameSession);
            lobbiesMap.put(lobby.getId(), lobby);
            lobbies.add(lobby);
        } catch (IOException ignore) {
        }
    }
}