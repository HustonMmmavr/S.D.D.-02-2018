package com.colorit.backend.game.messages.output;

import com.colorit.backend.game.gameobjects.GameField;
import com.colorit.backend.game.gameobjects.bonus.Bonus;
import com.colorit.backend.game.gameobjects.players.Player;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.websocket.Message;

import java.util.ArrayList;
import java.util.List;

public class ServerSnapshot extends Message {
    private final GameField.GameFieldSnap gameFieldSnap;
    private final List<Player.PlayerSnap> playersSnap;
    private final List<Bonus.BonusSnap> bonusesSnap;

    public ServerSnapshot(GameField.GameFieldSnap gameFieldSnap,
                          List<Player.PlayerSnap> playersSnap,
                          List<Bonus.BonusSnap> bonusesSnap) {
        this.bonusesSnap = bonusesSnap;
        this.playersSnap = playersSnap;
        this.gameFieldSnap = gameFieldSnap;
    }

    public static ServerSnapshot getSnapshot(GameSession gameSession) {
        final List<Player.PlayerSnap> playerSnaps = new ArrayList<>();
        final GameField.GameFieldSnap gameFieldSnap = gameSession.getGameField().getSnap();
        gameSession.getPlayers().forEach(player -> playerSnaps.add(player.getSnap()));
        final List<Bonus.BonusSnap> bonusSnaps = new ArrayList<>();
        gameSession.getBonuses().forEach(bonus -> bonusSnaps.add(bonus.getSnap()));
        return new ServerSnapshot(gameFieldSnap, playerSnaps, bonusSnaps);
    }

    public GameField.GameFieldSnap getGameFieldSnap() {
        return gameFieldSnap;
    }

    public List<Player.PlayerSnap> getPlayersSnap() {
        return playersSnap;
    }

    public List<Bonus.BonusSnap> getBonusesSnap() {
        return bonusesSnap;
    }
}
