package com.colorit.backend.game.mechanics;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.GameTaskScheduler;
import com.colorit.backend.game.MechanicsTimeService;
import com.colorit.backend.game.lobby.Lobby;
import com.colorit.backend.game.lobby.LobbyController;
import com.colorit.backend.game.messages.services.ClientSnapshotService;
import com.colorit.backend.game.messages.services.ServerSnapshotService;
import com.colorit.backend.game.session.GameSessionsController;
import com.colorit.backend.game.messages.input.ClientSnapshot;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.websocket.RemotePointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class GameMechanics implements IGameMechanics {
    private final static Logger LOGGER = LoggerFactory.getLogger(GameMechanics.class);

    @NotNull
    private final GameSessionsController gameSessionsController;

    @NotNull
    private final RemotePointService remotePointService;

    @NotNull
    private final ServerSnapshotService serverSnapshotService;

    @NotNull
    private final ClientSnapshotService clientSnapshotService;

    @NotNull
    private final LobbyController lobbyController;

    @NotNull
    private final MechanicsTimeService mechanicsTimeService;

    @NotNull
    private final GameTaskScheduler gameTaskScheduler;

    @NotNull
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    public GameMechanics(@NotNull GameSessionsController gameSessionsController,
                         @NotNull RemotePointService remotePointService,
                         @NotNull ServerSnapshotService serverSnapshotService,
                         @NotNull ClientSnapshotService clientSnapshotService,
                         @NotNull MechanicsTimeService mechanicsTimeService,
                         @NotNull GameTaskScheduler gameTaskScheduler,
                         @NotNull LobbyController lobbyController) {
        this.remotePointService = remotePointService;
        this.gameSessionsController = gameSessionsController;
        this.serverSnapshotService = serverSnapshotService;
        this.clientSnapshotService = clientSnapshotService;
        this.mechanicsTimeService = mechanicsTimeService;
        this.gameTaskScheduler = gameTaskScheduler;
        this.lobbyController = lobbyController;
    }

    @Override
    public void addClientSnapshot(@NotNull Id<UserEntity> userId, @NotNull ClientSnapshot clientSnap) {
        //tasks.add(() -> clientSnapshotService.pushClientSnap(userId, clientSnap));
        final GameSession gameSession = gameSessionsController.getGameUserSessions().get(userId);
        gameSession.changeDirection(userId, clientSnap.getDirection());
    }

    @Override
    public void gameStep(long frameTime) {
        while (!tasks.isEmpty()) {
            final Runnable nextTask = tasks.poll();
            if (nextTask != null) {
                try {
                    nextTask.run();
                } catch (RuntimeException ex) {
                    LOGGER.error("Can't handle game task", ex);
                }
            }
        }

        for (GameSession session : gameSessionsController.getGameSessions()) {
            clientSnapshotService.processSnapshotsFor(session);
        }


        gameTaskScheduler.tick();

        // скорость если хотим ускорение
        final List<GameSession> sessionsToTerminate = new ArrayList<>();
        final List<GameSession> sessionsToFinish = new ArrayList<>();
        final List<Lobby> deadLobbies = new ArrayList<>();
        for (Lobby lobby : lobbyController.getLobbies()) {
            if (lobbyController.isLobbyAlive(lobby) && !lobby.isFinished()) {
                GameSession gameSession = lobby.getAssociatedSession();
                if (gameSession.isFinised()) {
                    sessionsToFinish.add(gameSession);
                }


                // todo returs array of dea users and deletes them from session
                //  List<Id<UserEntity>> deadUsers = gameSessionsController.checkHealthState(gameSession);
                //  if (!deadUsers.isEmpty()) {
                //      deadUsers.forEach(user -> lobbyController.removeUser(lobby.getId(), user));
                //  }

                // todo get al
                try {
                    if (gameSession.isPlaying()) {
                        gameSession.movePlayers(frameTime);
                        gameSession.subTime(frameTime);
                        serverSnapshotService.sendSnapshotsFor(gameSession, frameTime);
                    }
                } catch (RuntimeException ex) {
                    LOGGER.error("Failed to send snapshots, terminating the session", ex);
                    sessionsToTerminate.add(gameSession);
                }
            } else {
                deadLobbies.add(lobby);
            }
        }

        deadLobbies.forEach(lobbyController::removeLobby);
        sessionsToFinish.forEach(GameSession::initMultiplayerSession);//gameSessionsController.(session, false));

        clientSnapshotService.reset();
        mechanicsTimeService.tick(frameTime);
    }

    @Override
    public void reset() {
        for (GameSession session : gameSessionsController.getGameSessions()) {
            gameSessionsController.forceTerminate(session, true);
        }
        tasks.clear();
        clientSnapshotService.reset();
        mechanicsTimeService.reset();
        gameTaskScheduler.reset();
    }
}