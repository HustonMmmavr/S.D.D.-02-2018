package com.colorit.backend.game.lobby;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.output.*;
import com.colorit.backend.game.session.GameSessionsController;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.services.IUserService;
import com.colorit.backend.websocket.Message;
import com.colorit.backend.websocket.RemotePointService;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;


// todo if user delete from lobby and in game session add bot
@Service
public class LobbyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LobbyController.class);
    @NotNull
    private final GameSessionsController gameSessionsController;
    @NotNull
    private final RemotePointService remotePointService;
    @NotNull
    private final IUserService userService;

    private final HashMap<Id<Lobby>, Lobby> lobbiesMap = new HashMap<>();
    private final Set<Id<UserEntity>> freeUsers = new HashSet<>();
    private final Set<Lobby> lobbies = new HashSet<>();

    public LobbyController(@NotNull GameSessionsController gameSessionsController,
                           @NotNull RemotePointService remotePointService,
                           @NotNull IUserService userService) {
        this.gameSessionsController = gameSessionsController;
        this.remotePointService = remotePointService;
        this.userService = userService;
    }

    public void removeLobby(Lobby lobby) {
        LOGGER.info("lobby deleted " + lobby.getId() + " owner " + lobby.getOwnerId().getAdditionalInfo() +
         " count users " + lobby.getAssociatedSession().getUsers().size());
        lobbiesMap.remove(lobby.getId());
        lobbies.remove(lobby);
        gameSessionsController.deleteSession(lobby.getAssociatedSession());
        // todo send all free users info that this lobby deleted
    }

    public boolean isLobbyAlive(Lobby lobby) {
        return lobby.isAlive();
    }

    // todo add method that sends messages to all users and if one no respond delete users

    public void deleteLobbyDeadUsers(Lobby lobby) {
        final List<Id<UserEntity>> users = lobby.getUsers();
        final List<Id<UserEntity>> deadUsers = new ArrayList<>();

        for (Id<UserEntity> user: users) {
//            if ()
        }
     }

    public boolean checkLobbyBeforeStart() {
        // todo check all users in lobby
        return true;
    }


    public boolean checkLobbyUsersHealth(Lobby lobby) {
        List<Id<UserEntity>> deadUsers = new ArrayList<>();
        for (Id<UserEntity> uId : lobby.getUsers()) {
            if (!remotePointService.isConnected(uId)) {
                deadUsers.add(uId);
            }
        }

        deadUsers.forEach(user -> lobby.getUsers().remove(user));
//        while ()
        if (!deadUsers.isEmpty()) {
            for (Id<UserEntity> user : lobby.getUsers()) {
                try {
                    remotePointService.sendMessageToUser(user, new LobbyStateMessage(lobby.getId(),
                            user, LobbyStateMessage.Action.DISCONNECTED));
                } catch (IOException exception) {
                    LOGGER.error("lobby error with users, lobby destroyed" + lobby.getId().toString());
                    return false;
                }
            }
            lobby.setState(Lobby.State.WAITING); // todo in function
        }

        return true;
    }

    public Set<Lobby> getLobbies() {
        return lobbies;
    }

    public void showLobbies(Id<UserEntity> uId) {
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

    public boolean checkUserInLobby(Id<UserEntity> uId) {
        try {
            if (gameSessionsController.getGameUserSessions().get(uId) != null) {
                remotePointService.sendMessageToUser(uId, new LobbyError("You cant create while you already play"));
                return true;
            }
            return false;
            // todo think what ret
        } catch (IOException ignore) {
            return false;
        }
    }

    private void trySendInfoToUsers(Lobby lobby) {
        List<Id<UserEntity>> users = lobby.getUsers();
        List<Id<UserEntity>> deadUsers = new ArrayList<>();
        for (Id<UserEntity> uId : users) {
            if (!remotePointService.isConnected(uId)) {
                deadUsers.add(uId);
            }
        }



        deadUsers.forEach(users::remove);
//        while ()
        while (!deadUsers.isEmpty() && !users.isEmpty()) {
            final List<Id<UserEntity>> newDeadUsers = new ArrayList<>();
            for (Id<UserEntity> user : users) {
                for (Id<UserEntity> deadUser: deadUsers) {
                    try {
                        remotePointService.sendMessageToUser(user, new LobbyStateMessage(
                                lobby.getId(), deadUser, LobbyStateMessage.Action.DISCONNECTED
                        ));
                    } catch (IOException i) {
                        newDeadUsers.add(user);
                    }
                }
                deadUsers.addAll(newDeadUsers);
                users.removeAll(newDeadUsers);
            }
            for (Id<UserEntity> user : lobby.getUsers()) {
                try {
                    remotePointService.sendMessageToUser(user, new LobbyStateMessage(lobby.getId(),
                            user, LobbyStateMessage.Action.DISCONNECTED));
                } catch (IOException exception) {
                    LOGGER.error("lobby error with users, lobby destroyed" + lobby.getId().toString());
//                    return false;
                }
            }
            lobby.setState(Lobby.State.WAITING); // todo in function
        }
    }

    private boolean checkLobbyExist(Id<Lobby> lId, Id<UserEntity> uId) {
        if (lId == null || lobbiesMap.get(lId) == null) {
            try {
                remotePointService.sendMessageToUser(uId, new LobbyError("Sorry lobby not found"));
            } catch (IOException ignore) {
            }
            return false;
        }
        return true;
    }


    public void addUser(Id<Lobby> lId, Id<UserEntity> uId) {
        /// user already in lobby
        if (checkUserInLobby(uId)) {
            return;
        }

        // error connecting to lobby
        if (!checkLobbyExist(lId, uId)) {
            return;
        }

        final Lobby lobby = lobbiesMap.get(lId);

        // adds user to session and removes from freeusers
//        gameSessionsController.addUser(uId, lobby.getAssociatedSession());
        try {
            for (Id<UserEntity> user : lobby.getUsers()) {
                remotePointService.sendMessageToUser(user, new LobbyStateMessage(lId, uId,
                        LobbyStateMessage.Action.CONNECTED));
            }
            remotePointService.sendMessageToUser(uId, new LobbyConnected(lobby.getUsers(), lobby.getId(),
                    lobby.getOwnerId(), lobby.getFiledSize(), lobby.getGameTime()));
        } catch (IOException e) {

        }
        freeUsers.remove(uId);
        gameSessionsController.addUser(uId, lobby.getAssociatedSession());
        try {
            if (lobby.getAssociatedSession().isFullParty()) {
                remotePointService.sendMessageToUser(lobby.getOwnerId(), new LobbyStateMessage(lobby.getId(), null,
                        LobbyStateMessage.Action.READY));
            }
        } catch (IOException ignore) {

        }
    }

    private boolean insureCandidate(@NotNull Id<UserEntity> candidate) {
        return remotePointService.isConnected(candidate)
                && userService.getUser(candidate.getAdditionalInfo()) != null;
    }



    public void startLobby(Id<UserEntity> uId, Id<Lobby> lId) {
        if (!checkLobbyExist(lId, uId)) {
            return;
        }

        final Lobby lobby = lobbiesMap.get(lId);
        if (!uId.equals(lobby.getOwnerId())) {
            try {
                remotePointService.sendMessageToUser(uId, new LobbyError("You cant start lobby"));
            } catch (IOException ignore) {
            }
            return;
        }


        if (lobby.getAssociatedSession().isReady()) {
            lobby.getAssociatedSession().initMultiplayerSession();
            lobby.getAssociatedSession().setPlaying();

            for (Id<UserEntity> user: lobby.getUsers()) {
                try {
                    remotePointService.sendMessageToUser(user, new GameStart(lobby.getAssociatedSession().getPlayerIds()));
                } catch (IOException i) {

                }
            }

            List<Id<UserEntity>> problemUsers = new ArrayList<>();
            for (Id<UserEntity> user : lobby.getUsers()) {
                if (!insureCandidate(user)) {
                    problemUsers.add(user);
                }
            }

            if (!problemUsers.isEmpty()) {
                problemUsers.forEach(user -> lobby.getUsers().remove(user));
                for (int i = 0; i < lobby.getUsers().size(); i++)
                {
                    for (int j = 0; j < problemUsers.size(); j++) {
                        try {
                            remotePointService.sendMessageToUser(lobby.getUsers().get(i),
                                    new LobbyStateMessage(lobby.getId(), problemUsers.get(j),
                                            LobbyStateMessage.Action.DISCONNECTED));//LobbyError("You cant start game, some users disconnetcted"));
                        } catch (IOException e) {
                            problemUsers.add(lobby.getUsers().get(i));
                        }
                    }
                }
                if (lobby.getUsers().isEmpty()) {
                    removeLobby(lobby);
                    return;
                }

                if (problemUsers.contains(lobby.getOwnerId())) {
//                    try {
//                        remotePointService.sendMessageToUser(lobby.getUsers().get(i),
//                                new LobbyStateMessage(lobby.getId().getId(), problemUsers.get(j).getId(),
//                                        LobbyStateMessage.Action.DISCONNECTED);//LobbyError("You cant start game, some users disconnetcted"));
//                    } catch (IOException e) {
//                        removeLobby(lobby);
//                    }
                }
            } else {
                lobby.setState(Lobby.State.GAME);
            }
        }
    }

    public void removeUser(Id<Lobby> lId, Id<UserEntity> uId) {
        if (!checkLobbyExist(lId, uId)) {
            return;
        }

        final Lobby lobby = lobbiesMap.get(lId);

        try {
            if (lobby.getUsers().contains(uId)) {
                for (Id<UserEntity> user : lobby.getUsers()) {
                    remotePointService.sendMessageToUser(user, new LobbyStateMessage(lId, uId,
                            LobbyStateMessage.Action.DISCONNECTED));
                }
            }
        } catch (IOException e) {

        }

        freeUsers.add(uId);
        try {
            remotePointService.sendMessageToUser(uId, new LobbyStateMessage(lId, uId, LobbyStateMessage.Action.DISCONNECTED));
        } catch (IOException i) {

        }
        gameSessionsController.removeUser(uId, lobby.getAssociatedSession());
        if (lobby.getAssociatedSession().getUsers().isEmpty()) {
            removeLobby(lobby);
        }
    }

    public void init(Id<UserEntity> uId, LobbySettings lobbySettings) {
        if (checkUserInLobby(uId)) {
            return;
        }

        final GameSession gameSession = gameSessionsController.createSession(lobbySettings.getFieldSize(),
                lobbySettings.getGameTime());
        gameSessionsController.addUser(uId, gameSession);
        final Lobby lobby = new Lobby(lobbySettings, uId, gameSession);
        lobbiesMap.put(lobby.getId(), lobby);
        lobbies.add(lobby);
        try {
            for (Id<UserEntity> user : freeUsers) {
                remotePointService.sendMessageToUser(user, new OneLobbyInfo(lobby.getId().getId(),
                        lobby.getId().getAdditionalInfo(),
                        lobby.getUsers().size(), lobby.getOwnerId().getAdditionalInfo(),
                        lobby.getFiledSize(), lobby.getGameTime()));
            }
            freeUsers.remove(uId);
        } catch (IOException ignore) {
        }
    }
}
