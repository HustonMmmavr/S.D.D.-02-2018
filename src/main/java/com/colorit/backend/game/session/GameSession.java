package com.colorit.backend.game.session;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.gameobjects.GameField;
import com.colorit.backend.game.gameobjects.bonus.Bonus;
import com.colorit.backend.game.gameobjects.math.Point;
import com.colorit.backend.game.gameobjects.players.Player;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.colorit.backend.game.GameConfig.FULL_PARTY;
import static com.colorit.backend.game.GameConfig.MILISECONDS;
import static com.colorit.backend.game.GameConfig.MIN_BORDER;

public class GameSession {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private List<Id<UserEntity>> users = new CopyOnWriteArrayList<>();
    private List<Player> players = new ArrayList<>();
    private Map<Id<UserEntity>, Player> playersMap = new HashMap<>();
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
        this.gameTime = gameTime * MILISECONDS;
        this.timePlaying = 0;
        this.gameSessionsController = gameSessionsController;
    }

    public enum Status {
        WAITING,
        READY,
        PLAYING,
        FINISHED,
        DEAD
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

    public boolean isDead() {
        return this.sessionStatus == Status.DEAD;
    }

    public void setDead() {
        this.sessionStatus = Status.DEAD;
    }

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

    public void reset() {
        this.timePlaying = 0;
        this.sessionStatus = Status.WAITING;
        this.players.clear();
        gameField.clear();
        this.playersMap.clear();
    }

    public Map<Id<UserEntity>, GameResults> getScores() {
        final List<Player> sortedPlayers = players.stream()
                .sorted(Comparator.comparing(Player::getScore)
                        .reversed()).collect(Collectors.toList());

        final HashMap<Id<UserEntity>, GameResults> scoresMap = new HashMap<>();
        final Player winner = sortedPlayers.get(0);
        sortedPlayers.remove(winner);
        int score = 2;
        scoresMap.put(winner.getUserId(), new GameResults(true, score--));

        for (var player: sortedPlayers) {
            scoresMap.put(player.getUserId(), new GameResults(false, score--));
        }

        return scoresMap;
    }

    public List<Id<UserEntity>> getUsers() {
        return users;
    }

    public List<Player> getPlayers() {
        return players;
    }


    public void runMechanics(long time) {
        movePlayers(time);
    }

    public void subTime(long time) {
        this.timePlaying += time;
    }

    public boolean isFinised() {
        return gameTime < timePlaying;
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

        if (users.isEmpty()) {
            this.sessionStatus = Status.DEAD;
        }
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


    private void movePlayer(Player player, long time) {
        if (player.move(time, MIN_BORDER, gameField.getRank() - 1)) {
            gameField.markCell(player.getPosition(), player.getPlayerId().getId());
            if (player.isAddScore()) {
                player.setScore(player.getScore()
                        + gameField.countScoresForPlayer((int) player.getPlayerId().getId()));
                player.setAddScore(false);
            } else {
                player.setAddScore(gameField.checkArea(player.getPosition(), (int) player.getPlayerId().getId()));
            }
        }
    }

    public void movePlayer(Id<UserEntity> userId, long time, Direction direction) {
        movePlayer(playersMap.get(userId), time);
    }

    private void movePlayers(long time) {
        players.forEach(player -> movePlayer(player, time));
    }

    public void terminateSession() {
        gameSessionsController.forceTerminate(this, true);
    }

    public boolean isFullParty() {
        return users.size() == FULL_PARTY;
    }
}