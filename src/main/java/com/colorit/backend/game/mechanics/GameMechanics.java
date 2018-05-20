package com.colorit.backend.game.mechanics;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.GameTaskScheduler;
import com.colorit.backend.game.MechanicsTimeService;
import com.colorit.backend.game.messages.services.ClientSnapshotService;
import com.colorit.backend.game.messages.services.ServerSnapshotService;
import com.colorit.backend.game.session.GameSessionsController;
import com.colorit.backend.game.gameobjects.Direction;
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
                         @NotNull GameTaskScheduler gameTaskScheduler) {
        this.remotePointService = remotePointService;
        this.gameSessionsController = gameSessionsController;
        this.serverSnapshotService = serverSnapshotService;
        this.clientSnapshotService = clientSnapshotService;
        this.mechanicsTimeService = mechanicsTimeService;
        this.gameTaskScheduler = gameTaskScheduler;
    }

    @Override
    public void addClientSnapshot(@NotNull Id<UserEntity> userId, @NotNull ClientSnapshot clientSnap) {
//        tasks.add(() -> clientSnapshotService.pushClientSnap(userId, clientSnap));
        final GameSession gameSession = gameSessionsController.getGameUserSessions().get(userId);
        gameSession.changeDirection(userId, clientSnap.getDirection());
    }

//    @Override
//    public void changeDirection(@NotNull Id<UserEntity> userId, @NotNull Direction direction) {
//        GameSession gameSession = gameSessionsController.getGameUserSessions().get(userId);
////        if (gameSession != null) {
////            gameSession.changeDirection(userId, direction);
//        }
////        gameSessionsController.getGameSessions().forEach();
//    }

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

        final List<GameSession> sessionsToTerminate = new ArrayList<>();
        final List<GameSession> sessionsToFinish = new ArrayList<>();
        for (GameSession session : gameSessionsController.getGameSessions()) {

            try {
                if (session.isFullParty()) {
                    session.movePlayers(frameTime);
                    session.subTime(frameTime);
                    serverSnapshotService.sendSnapshotsFor(session, frameTime);
                    Thread.sleep(1000);
                }
            } catch (Exception e) {

            }
            // session needs to knew how mony time its playing
            // gamefield genrate bonus
            // todo add task that removes this effect or player stores itself its time

            if (session.isFinised()) {
                sessionsToFinish.add(session);
            }
            //          if (session.tryFinishGame()) {
            //              sessionsToFinish.add(session);
            //            continue;
            //      }

            //    if (!gameSessionsController.checkHealthState(session)) {
            //      sessionsToTerminate.add(session);
            //    continue;
            //}

            try {
                //serverSnapshotService.sendSnapshotsFor(session, frameTime);
            } catch (RuntimeException ex) {
                LOGGER.error("Failed to send snapshots, terminating the session", ex);
                sessionsToTerminate.add(session);
            }
            //pullTheTriggerService.pullTheTriggers(session);
        }
        //sessionsToTerminate.forEach(session -> gameSessionsController.forceTerminate(session, true));
//        sessionsToFinish.forEach(session -> gameSessionsController.forceTerminate(session, false));

//        tryStartGames();
        clientSnapshotService.reset();
//        timeService.tick(frameTime);
    }

    @Override
    public void reset() {
//        for (GameSession session : gameSessionsController.getGameSessions()) {
//            gameSessionsController.forceTerminate(session, true);
//        }
//        waiters.forEach(user -> remotePointService.cutDownConnection(user, CloseStatus.SERVER_ERROR));
//        waiters.clear();
//        tasks.clear();
//        clientSnapshotsService.reset();
//        timeService.reset();
//        gameTaskScheduler.reset();
    }
}


// Its for test TODO delete slee
// todo interpolate offset
//    @Override
//    public void gameStep(long frameTime) {
//        try {
//            mechanicsTimeService.tick(frameTime);
//            System.out.println(frameTime);
//            for (GameSession gameSession : gameSessionsController.getGameSessions()) {
//                if (gameSession.isFullParty()) {
//                    gameSession.movePlayers(frameTime);
//                    gameSession.subTime(frameTime);
//                    serverSnapshotService.sendSnapshotsFor(gameSession, frameTime);
////                    Thread.sleep(1000);
//                }
//
//                if (gameSession.isFinised()) {
//
//                }
//            }
//        } catch (Exception i) {
//
//        }
//    }