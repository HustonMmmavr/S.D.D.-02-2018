package com.colorit.backend.game.mechanics;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.GameSessionsController;
import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.messages.ClientSnapshot;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.websocket.RemotePointService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class GameMechanics implements IGameMechanics {
    @NotNull
    private final GameSessionsController gameSessionsController;

    @NotNull
    private final RemotePointService remotePointService;



    @Override
    public void addClientSnapshot(@NotNull Id<UserEntity> userId, @NotNull ClientSnapshot clientSnap) {
        final GameSession gameSession = gameSessionsController.getGameUserSessions().get(userId);
        gameSession.changeDirection(userId, clientSnap.getDirection());

    }

    GameMechanics(@NotNull GameSessionsController gameSessionsController,
                  @NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
        this.gameSessionsController = gameSessionsController;
    }

    @Override
    public void changeDirection(@NotNull Id<UserEntity> userId, @NotNull Direction direction) {
//        gameSessionsController.getGameSessions().forEach();
    }


    @Override
    public void gameStep(long frameTime) {
        for (GameSession gameSession: gameSessionsController.getGameSessions()) {
            gameSession.movePlayers(frameTime);
            gameSession.sendGameInfo();
        }
    }

    @Override
    public void reset() {

    }
}
