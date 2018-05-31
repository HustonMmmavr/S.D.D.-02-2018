package com.colorit.backend.game.messages.output;

import com.colorit.backend.game.gameobjects.GameField;
import com.colorit.backend.game.gameobjects.bonus.Bonus;
import com.colorit.backend.game.gameobjects.players.Player;
import com.colorit.backend.game.session.GameSession;
import com.colorit.backend.websocket.Message;

import java.util.ArrayList;
import java.util.List;

public class ServerSnapshot extends Message {
    private final long frameTime;
    private final long timestamp = System.currentTimeMillis();
    private final GameField.GameFieldSnap gameFieldSnap;
    private final List<Player.PlayerSnap> playersSnap;
    private final List<Bonus.BonusSnap> bonusesSnap;

    public ServerSnapshot(long frameTime, GameField.GameFieldSnap gameFieldSnap,
                          List<Player.PlayerSnap> playersSnap,
                          List<Bonus.BonusSnap> bonusesSnap) {
        this.frameTime = frameTime;
        this.bonusesSnap = bonusesSnap;
        this.playersSnap = playersSnap;
        this.gameFieldSnap = gameFieldSnap;
    }

    public static ServerSnapshot getSnapshot(GameSession gameSession, long frameTime) {
        final List<Player.PlayerSnap> playerSnaps = new ArrayList<>();
        final GameField.GameFieldSnap gameFieldSnap = gameSession.getGameField().getSnap();
        gameSession.getPlayers().forEach(player -> playerSnaps.add(player.getSnap()));
        final List<Bonus.BonusSnap> bonusSnaps = new ArrayList<>();
        gameSession.getBonuses().forEach(bonus -> bonusSnaps.add(bonus.getSnap()));
        return new ServerSnapshot(frameTime, gameFieldSnap, playerSnaps, bonusSnaps);
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

    public long getTimestamp() {
        return timestamp;
    }

    public long getFrameTime() {
        return frameTime;
    }
}
