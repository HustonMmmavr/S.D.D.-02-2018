package com.colorit.backend.game.messages.services;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.output.ServerSnapshot;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.websocket.RemotePointService;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.io.IOException;

@Service
public class ServerSnapshotService {
    @NotNull
    private final RemotePointService remotePointService;

    public ServerSnapshotService(@NotNull RemotePointService remotePointService) {
        this.remotePointService = remotePointService;
    }

    public void sendSnapshotsFor(@NotNull GameSession gameSession, long frameTime) {

        final ServerSnapshot serverSnapshot = ServerSnapshot.getSnapshot(gameSession);

        //noinspection OverlyBroadCatchBlock
        try {
            for (Id<UserEntity> user : gameSession.getUsers()) {
                remotePointService.sendMessageToUser(user, serverSnapshot);
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed  sending snapshot", ex);
        }

    }
}