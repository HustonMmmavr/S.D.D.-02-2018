package com.colorit.backend.game.session;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.gameobjects.GameField;
import com.colorit.backend.game.gameobjects.bonus.Bonus;
import com.colorit.backend.game.gameobjects.math.Point;
import com.colorit.backend.game.gameobjects.players.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.colorit.backend.game.GameConfig.FULL_PARTY;
import static com.colorit.backend.game.GameConfig.MIN_BORDER;

public class GameSession {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private List<Id<UserEntity>> users = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private HashMap<Id<UserEntity>, Player> playersMap = new HashMap<>();
    private Id<GameSession> id;
    private GameField gameField;
    private final GameSessionsController gameSessionsController;
    private Status sessionStatus;
    private long gameTime;
    private long timePlaying;

    public GameSession(GameSessionsController gameSessionsController, int fieldSize, long gameTime) {
        id = Id.of(ID_GENERATOR.getAndIncrement());
        gameField = new GameField(fieldSize);
        sessionStatus = Status.WAITING;
        this.gameTime = gameTime * 1000; //milisseconds
        this.timePlaying = 0;
        this.gameSessionsController = gameSessionsController;
    }

    public enum Status {
        WAITING,
        READY,
        PLAYING,
        FINISHED
    }

    public void setWaiting() {
        this.sessionStatus = Status.WAITING;
    }

    public boolean isWaiting() {
        return this.sessionStatus == Status.WAITING;
    }

    public void setReady() {
        this.sessionStatus = Status.READY;
    }

    public boolean isReady() {
        return this.sessionStatus == Status.READY;
    }

    //public void setFinished() {
    //this.sessionStatus = Status.FINISHED;
    //}

    public boolean isFinished() {
        return this.sessionStatus == Status.FINISHED;
    }

    public void setPlaying() {
        this.sessionStatus = Status.PLAYING;
    }

    public boolean isPlaying() {
        return this.sessionStatus == Status.PLAYING;
    }

    public Id<GameSession> getId() {
        return id;
    }

    public void resetSession() {
        this.timePlaying = 0;
        this.sessionStatus = Status.WAITING;
    }

    public List<Id<UserEntity>> getUsers() {
        return users;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void subTime(double time) {
        this.timePlaying += time;
    }

    public boolean isFinised() {
        return gameTime < timePlaying;
        //sessionStatus == Status.FINISHED;
    }

    public void addUser(Id<UserEntity> userId) {
        users.add(userId);
        if (users.size() == FULL_PARTY) {
            this.sessionStatus = Status.READY;
        }
    }

    //todo
    public void initSingleplayerSession() {

    }

    public List<Id<Player>> getPlayerIds() {
        final List<Id<Player>> playerIds = new ArrayList<>();
        players.forEach(player -> playerIds.add(player.getPlayerId()));
        return playerIds;
    }

    public void initMultiplayerSession() {
        playersMap.clear();
        players.clear();
        long playerId = 1;
        for (Id<UserEntity> user : users) {
            final Point startPoint = new Point(playerId == 1 || playerId == 4 ? 0 : gameField.getRank() - 1,
                    playerId < 3 ? 0 : gameField.getRank() - 1);
            final Player player = new Player(user, Id.of(playerId), startPoint);
            gameField.markCell(startPoint, playerId);
            playersMap.put(user, player);
            players.add(player);
            playerId += 1;
        }
    }

    public List<Bonus> getBonuses() {
        return new ArrayList<>();
    }

    public GameField getGameField() {
        return gameField;
    }

    public void removeUser(Id<UserEntity> userId) {
        users.remove(userId);
        final Player player = playersMap.get(userId);
        playersMap.remove(userId);
        players.remove(player);
    }

    public void changeDirection(Id<UserEntity> userId, Direction direction) {
        final Player player = playersMap.get(userId);
        player.setNewDirection(direction);
    }

    public int getFieldSize() {
        return gameField.getRank();
    }

    public long getGameTime() {
        return gameTime;
    }


    public void movePlayer(Id<UserEntity> userId, long time, Direction direction) {
        final Player player = playersMap.get(userId);
        //player.setNewDirection(direction);
        if (player.move((double) time, MIN_BORDER, gameField.getRank() - 1)) {
            gameField.markCell(player.getPosition(), player.getPlayerId().getId());
            if (player.isAddScore()) {
                player.setScore(gameField.countScoresForPlayer((int) player.getPlayerId().getId()));
                player.setAddScore(false);
            } else {
                player.setAddScore(gameField.checkArea(player.getPosition(), (int) player.getPlayerId().getId()));
            }
        }
        //player.move((double) time, MIN_BORDER, gameField.getRank() - 1);
    }

    //public void movePlayers(long delay) {
    //    players.forEach(player -> {
    //       if (player.move((double) delay, MIN_BORDER, gameField.getRank() - 1)) {
    //            gameField.markCell(player.getPosition(), player.getPlayerId().getId());
    //            if (player.isAddScore()) {
    //                player.setScore(gameField.countScoresForPlayer((int) player.getPlayerId().getId()));
    //                player.setAddScore(false);
    //            } else {
    //                player.setAddScore(gameField.checkArea(player.getPosition(), (int) player.getPlayerId().getId()));
    //            }
    //        }
    //    });
    //}

    public void terminateSession() {
        gameSessionsController.forceTerminate(this, true);
    }

    public boolean isFullParty() {
        return users.size() == FULL_PARTY;
    }
}