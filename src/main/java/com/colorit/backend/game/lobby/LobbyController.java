package com.colorit.backend.game.lobby;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.output.Lobbies;
import com.colorit.backend.game.messages.output.LobbyError;
import com.colorit.backend.game.messages.output.LobbyUsers;
import com.colorit.backend.game.session.GameSessionsController;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.websocket.RemotePointService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;

@Service
public class LobbyController {
    @NotNull
    private final GameSessionsController gameSessionsController;
    @NotNull
    private final RemotePointService remotePointService;

    private final HashMap<Id<Lobby>, Lobby> lobbiesMap = new HashMap<>();
    private final Set<Lobby> lobbies = new HashSet<>();
//    private final Set<Id<Lobby>> activeLobbies = new HashSet<>();

    public LobbyController(@NotNull GameSessionsController gameSessionsController,
                           @NotNull RemotePointService remotePointService) {
        this.gameSessionsController = gameSessionsController;
        this.remotePointService = remotePointService;
    }

    public void addUser(Id<Lobby> lId, Id<UserEntity> uId) {
        final Lobby lobby = lobbiesMap.get(lId);
        if (lId == null) {
            try {
                remotePointService.sendMessageToUser(uId, new LobbyError("Sorry lobby not found"));
            } catch (IOException ignore) {

            }
            return;
        }
        gameSessionsController.addUser(uId, lobby.getAssociatedSession());
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
                    lobbiesList.add(new Lobbies.OneLobby(lId.getId(), lId.getAdditionalInfo(), lobby.getUsers().size(), lobby.getOwnerId().getAdditionalInfo()));
                }
            }
            remotePointService.sendMessageToUser(uId, new Lobbies(lobbiesList));
        } catch (IOException ignore) {
        }
    }

    public void init(Id<UserEntity> uId, LobbySettings lobbySettings) {
        final GameSession gameSession = gameSessionsController.createSession(lobbySettings.getFieldSize());
        gameSessionsController.addUser(uId, gameSession);
        final Lobby lobby = new Lobby(lobbySettings, uId, gameSession);
        lobbiesMap.put(lobby.getId(), lobby);
        lobbies.add(lobby);
    }
}