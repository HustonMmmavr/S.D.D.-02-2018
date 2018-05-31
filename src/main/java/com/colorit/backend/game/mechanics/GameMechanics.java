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
    private static final Logger LOGGER = LoggerFactory.getLogger(GameMechanics.class);

    private final @NotNull GameSessionsController gameSessionsController;

    private final @NotNull RemotePointService remotePointService;

    private final @NotNull ServerSnapshotService serverSnapshotService;

    private final @NotNull ClientSnapshotService clientSnapshotService;

    private final @NotNull LobbyController lobbyController;

    private final @NotNull MechanicsTimeService mechanicsTimeService;

    private final @NotNull GameTaskScheduler gameTaskScheduler;

    private final @NotNull Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

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
        tasks.add(() -> clientSnapshotService.pushClientSnap(userId, clientSnap));
        //LOGGER.info("{}", clientSnap.getFrameTime());
        //if (clientSnap.getDirection() != null) {
        //      int c = 1;
        //}
        final GameSession gameSession = gameSessionsController.getGameUserSessions().get(userId);
        //if (clientSnap.getDirection() != null) {
        //    gameSession.changeDirection(userId, clientSnap.getDirection());
        //}

        //gameSession.movePlayer(userId, clientSnap.getFrameTime(), clientSnap.getDirection());
        if (clientSnap.getDirection() != null) {
            gameSession.changeDirection(userId, clientSnap.getDirection());
        }
        //gameSession.movePlayers(clientSnap.getFrameTime());
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
        final List<Lobby> lobbiesToFinish = new ArrayList<>();
        final List<Lobby> deadLobbies = new ArrayList<>();
        for (Lobby lobby : lobbyController.getLobbies()) {
            if (!lobbyController.isLobbyAlive(lobby)) {
                deadLobbies.add(lobby);
            }

            // fuck
            if (lobby.isPlaying() && !lobby.isFinished()) {
                final GameSession gameSession = lobby.getAssociatedSession();

                gameSession.runMechanics(frameTime);
                gameSession.subTime(frameTime);
                serverSnapshotService.sendSnapshotsFor(gameSession, frameTime);
                // todo send info to users and delete dead users
            } else {
                lobbiesToFinish.add(lobby);
            }
        }

        deadLobbies.forEach(lobbyController::removeLobby);
        lobbiesToFinish.forEach(lobbyController::reset);

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