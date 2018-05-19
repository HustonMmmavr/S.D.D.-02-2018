package com.colorit.backend.game.lobby;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.output.*;
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
    private final Set<Id<UserEntity>> freeUsers = new HashSet<>();
//    private final HashMap<Id<Lobby>, Id<UserEntity>> lobbyUserMap = new HashMap<>();
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

        try {
            for (Id<UserEntity> user : lobby.getUsers()) {
                remotePointService.sendMessageToUser(user, new LobbyInfoMessage(lId.getId(), uId.getId(),
                        LobbyInfoMessage.Action.CONNECTED));
            }
        } catch (IOException e) {

        }

        freeUsers.remove(uId);
//        lobbyUserMap.put(lId, uId);
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

        try {
            for (Id<UserEntity> user : lobby.getUsers()) {
                remotePointService.sendMessageToUser(user, new LobbyInfoMessage(lId.getId(), uId.getId(),
                        LobbyInfoMessage.Action.DISCONNECTED));
            }
        } catch (IOException e) {

        }

        freeUsers.add(uId);
        gameSessionsController.removeUser(uId, lobby.getAssociatedSession());
        if (lobby.getAssociatedSession().getUsers().isEmpty()) {
            gameSessionsController.deleteSession(lobby.getAssociatedSession());
            lobbies.remove(lobby);
            lobbiesMap.remove(lId);
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
            // is it safe?
            freeUsers.add(uId);
            final List<OneLobbyInfo> lobbiesList = new ArrayList<>();
            for (Id<Lobby> lId : lobbiesMap.keySet()) {
                final Lobby lobby = lobbiesMap.get(lId);
                if (lobby != null && lobby.isActive()) {
                    lobbiesList.add(new OneLobbyInfo(lId.getId(), lId.getAdditionalInfo(),
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
            for (Id<UserEntity> user: freeUsers) {
                remotePointService.sendMessageToUser(user, new OneLobbyInfo(lobby.getId().getId(),
                        lobby.getId().getAdditionalInfo(),
                        lobby.getUsers().size(), lobby.getOwnerId().getAdditionalInfo(),
                        lobby.getFiledSize(), lobby.getGameTime()));
            }
            freeUsers.remove(uId);
//            lobbyUserMap.put(lobby.getId(), uId);
        } catch (IOException ignore) {
        }
    }
}