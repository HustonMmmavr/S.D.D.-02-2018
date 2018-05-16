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
    private static final int FULL_PARTY = 1;

    // TODO
    private List<Id<UserEntity>> users = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private HashMap<Id<UserEntity>, Player> playersMap = new HashMap<>();
    private Id<GameSession> id;
    private GameField gameField;
    private Status sessionStatus;
    private RemotePointService remotePointService;

    public GameSession(RemotePointService remotePointService, int fieldSize) {
        id = Id.of(ID_GENERATOR.getAndIncrement());
        gameField = new GameField(fieldSize);
        sessionStatus = Status.CREATED;
        this.remotePointService = remotePointService;
    }

    public enum Status {
        CREATED,
        FILLED,
        PLAYING,
        FINISHED
    }

    public List<Id<UserEntity>> getUsers() {
        return users;
    }

    public List<Player> getPlayers() {
        return players;
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
            final Point startPoint = new Point(playerId == 1 || playerId == 4  ?  0 : gameField.getRank() ,
                    playerId < 3 ? 0 : gameField.getRank());
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

    public void movePlayers(long delay) {
        players.forEach(player ->
            gameField.markCell(player.move((double) delay, MIN_BORDER, gameField.getRank() - 1), player.getPlayerId().getId())
        );
    }

//    public void sendGameInfo() {
//        List<Point> points = new ArrayList<>();
//
//        players.forEach(player -> points.add(player.getPosition()));
//        Position position = new Position(points);
//        try {
//            for (Id<UserEntity> user : users) {
//                remotePointService.sendMessageToUser(user, position);
//            }
//        } catch (IOException ex) {
//            LOGGER.error("error send info");
//        }
//    }

    public boolean isFullParty() {
        return users.size() == FULL_PARTY;
    }
}
