package com.colorit.backend.game.mechanics;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.services.ClientSnapshotService;
import com.colorit.backend.game.messages.services.ServerSnapshotService;
import com.colorit.backend.game.session.GameSessionsController;
import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.messages.input.ClientSnapshot;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.websocket.RemotePointService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class GameMechanics implements IGameMechanics {
    @NotNull
    private final GameSessionsController gameSessionsController;

    @NotNull
    private final RemotePointService remotePointService;

    @NotNull
    private final ServerSnapshotService serverSnapshotService;

    @NotNull
    private final ClientSnapshotService clientSnapshotService;

    @NotNull
    private final Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    @Override
    public void addClientSnapshot(@NotNull Id<UserEntity> userId, @NotNull ClientSnapshot clientSnap) {
//        tasks.add(() -> clientSnapshotService.pushClientSnap(userId, clientSnap));
        final GameSession gameSession = gameSessionsController.getGameUserSessions().get(userId);
        gameSession.changeDirection(userId, clientSnap.getDirection());
    }

    GameMechanics(@NotNull GameSessionsController gameSessionsController,
                  @NotNull RemotePointService remotePointService,
                  @NotNull ServerSnapshotService serverSnapshotService,
                  @NotNull ClientSnapshotService clientSnapshotService) {
        this.remotePointService = remotePointService;
        this.gameSessionsController = gameSessionsController;
        this.serverSnapshotService = serverSnapshotService;
        this.clientSnapshotService = clientSnapshotService;
    }

    @Override
    public void changeDirection(@NotNull Id<UserEntity> userId, @NotNull Direction direction) {
//        gameSessionsController.getGameSessions().forEach();
    }


    // Its for test TODO delete slee
    @Override
    public void gameStep(long frameTime) {
        try {
            for (GameSession gameSession : gameSessionsController.getGameSessions()) {
                if (gameSession.isFullParty()) {
                    gameSession.movePlayers(frameTime);
                    //gameSession.sendGameInfo();
                    serverSnapshotService.sendSnapshotsFor(gameSession, frameTime);
                    Thread.sleep(1000);
                }
            }
        } catch (Exception i) {

        }
    }

    @Override
    public void reset() {

    }
}
