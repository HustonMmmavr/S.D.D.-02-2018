package com.colorit.backend.game.messages.services;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.players.Player;
import com.colorit.backend.game.mechanics.GameMechanics;
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

    @NotNull
    public List<ClientSnapshot> getSnapForUser(@NotNull Id<UserEntity> user) {
        return snaps.getOrDefault(user, Collections.emptyList());
    }

    public void processSnapshotsFor(@NotNull GameSession gameSession) {
        final Collection<Player> players = new ArrayList<>();
        players.addAll(gameSession.getPlayers());
        for (Player player: players) {
            final List<ClientSnapshot> playerSnaps = getSnapForUser(player.getUserId());
            if (playerSnaps.isEmpty()) {
                continue;
            }

            playerSnaps.stream().filter(ClientSnapshot::isChanged()).findFirst().ifPres
         }
//        players.add(gameSession.getFirst());
//        players.add(gameSession.getSecond());
//        for (GameUser player : players) {
//            final List<ClientSnap> playerSnaps = getSnapForUser(player.getUserId());
//            if (playerSnaps.isEmpty()) {
//                continue;
//            }
//
//            playerSnaps.stream().filter(ClientSnap::isFiring).findFirst().ifPresent(snap -> processClick(snap, gameSession, player));
//
//            final ClientSnap lastSnap = playerSnaps.get(playerSnaps.size() - 1);
//            processMouseMove(player, lastSnap.getMouse());
//        }
    }

    private void processDirectionChange(@NotNull ClientSnapshot snap, @NotNull GameSession gameSession,
                                   @NotNull Player player) {

    }

    //private void processClick(@NotNull ClientSnap snap, @NotNull GameSession gameSession, @NotNull GameUser gameUser) {
        //final MechanicPart mechanicPart = gameUser.claimPart(MechanicPart.class);
        //if (mechanicPart.tryFire()) {
        //    gameSession.getBoard().fireAt(snap.getMouse());
        //}
    //}

    //private void processMouseMove(@NotNull GameUser gameUser, @NotNull Coords mouse) {
        //gameUser.claimPart(MousePart.class).setMouse(mouse);
    //}

    public void clearForUser(Id<UserEntity> uId) {
        snaps.remove(uId);
    }

    public void reset() {
        snaps.clear();
    }
}