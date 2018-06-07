package com.colorit.backend.game.messages.services;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.players.Player;
import com.colorit.backend.game.messages.input.ClientSnapshot;
import com.colorit.backend.game.session.GameSession;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
public class ClientSnapshotService {

    private final Map<Id<UserEntity>, List<ClientSnapshot>> snaps = new HashMap<>();

    public void pushClientSnap(@NotNull Id<UserEntity> user, @NotNull ClientSnapshot snap) {
        this.snaps.putIfAbsent(user, new ArrayList<>());
        final List<ClientSnapshot> clientSnaps = snaps.get(user);
        clientSnaps.add(snap);
    }

    public @NotNull List<ClientSnapshot> getSnapForUser(@NotNull Id<UserEntity> user) {
        return snaps.getOrDefault(user, Collections.emptyList());
    }

    public void processSnapshotsFor(@NotNull GameSession gameSession) {
        final Collection<Player> players = new ArrayList<>(gameSession.getPlayers());
        for (Player player : players) {
            final List<ClientSnapshot> playerSnaps = getSnapForUser(player.getUserId());
            if (playerSnaps.isEmpty()) {
                continue;
            }

            playerSnaps.forEach(snap -> process(snap, gameSession, player));
            snaps.get(player.getUserId()).removeAll(playerSnaps);
        }
    }

    private void process(@NotNull ClientSnapshot snap, @NotNull GameSession gameSession, @NotNull Player player) {
        if (snap.getDirection() !=  null) {
            player.setNewDirection(snap.getDirection());
        }
        gameSession.movePlayer(player.getUserId(), snap.getFrameTime(), snap.getDirection());
    }

    public void clearForUser(Id<UserEntity> userId) {
        snaps.remove(userId);
    }

    public void reset() {
        snaps.clear();
    }
}