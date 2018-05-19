package com.colorit.backend.game.session;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.gameobjects.GameField;
import com.colorit.backend.game.gameobjects.bonus.Bonus;
import com.colorit.backend.game.gameobjects.math.Point;
import com.colorit.backend.game.gameobjects.players.Player;
import com.colorit.backend.websocket.RemotePointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.colorit.backend.game.GameConfig.MIN_BORDER;

public class GameSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSession.class);
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private static final int FULL_PARTY = 2;

    private List<Id<UserEntity>> users = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private HashMap<Id<UserEntity>, Player> playersMap = new HashMap<>();
    private Id<GameSession> id;
    private GameField gameField;
    private Status sessionStatus;
    private RemotePointService remotePointService;
    private final GameSessionsController gameSessionsController;
    private long gameTime;
    private long timePlaying;

    public GameSession(RemotePointService remotePointService, GameSessionsController gameSessionsController,
                       int fieldSize, long gameTime) {
        this.gameSessionsController = gameSessionsController;
        id = Id.of(ID_GENERATOR.getAndIncrement());
        gameField = new GameField(fieldSize);
        sessionStatus = Status.CREATED;
        this.remotePointService = remotePointService;
        this.gameTime = gameTime * 1000; //milisseconds
        this.timePlaying = 0;
    }


    // no need enums its on level lobb
    public enum Status {
        CREATED,
        FILLED,
        PLAYING,
        FINISHED
    }

    public void resetSession() {
        this.timePlaying = 0;

    }

    public List<Id<UserEntity>> getUsers() {
        return users;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void subTime(double time) {
        this.timePlaying += time;
//        gameTime -= time;
//        if (gameTime <= 0) {
//            sessionStatus = Status.FINISHED;
//        }
    }

    public boolean isFinised() {
        return gameTime < timePlaying;
        //sessionStatus == Status.FINISHED;
    }

    public void setStatus(Status status) {
        sessionStatus = status;
    }

    public void addUser(Id<UserEntity> userId) {
        users.add(userId);
    }

    public void startSession() {
        long playerId = 1;
        for (Id<UserEntity> user: users) {
            final Point startPoint = new Point(playerId == 1 || playerId == 4  ?  0 : gameField.getRank() - 1 ,
                    playerId < 3 ? 0 : gameField.getRank() - 1);
            final Player player = new Player(user.getAdditionalInfo(), Id.of(playerId), startPoint);
            playerId += 1;
            playersMap.put(user, player);
            players.add(player);
        }
    }

    public List<Bonus> getBonuses() {
        return new ArrayList<>();
    }

    public GameField getGameField() {
        return gameField;
    }


    public void removeUser(Id<UserEntity> uId) {
        users.remove(uId);
        final Player player = playersMap.get(uId);
        playersMap.remove(uId);
        players.remove(player);
    }

    public void changeDirection(Id<UserEntity> uId, Direction direction) {
        final Player player = playersMap.get(uId);
        player.setDirection(direction);
    }

    public void terminateSession() {
        gameSessionsController.terminateSession(this, true);
    }

    public int getFieldSize() {
        return gameField.getRank();
    }

    public long getGameTime() {
        return gameTime;
    }

    public void movePlayers(long delay) {
        players.forEach(player -> {
            if (player.move((double) delay, MIN_BORDER, gameField.getRank() - 1)) {
                gameField.markCell(player.getPosition(), player.getPlayerId().getId());
                if (player.isAddScore()) {
                    player.setScore(gameField.countScoresForPlayer((int) player.getPlayerId().getId()));
                    player.setAddScore(false);
                } else {
                    player.setAddScore(gameField.checkArea(player.getPosition(), (int)player.getPlayerId().getId()));
                }
            }
        });
    }

    public boolean isFullParty() {
        return users.size() == FULL_PARTY;
    }
}
