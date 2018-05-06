package com.colorit.backend.game.session;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.GameField;
import com.colorit.backend.game.gameobjects.GameObject;
import com.colorit.backend.game.gameobjects.math.Point;
import com.colorit.backend.game.gameobjects.players.Player;
import com.colorit.backend.game.messages.Position;
import com.colorit.backend.websocket.RemotePointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class GameSession {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSession.class);
    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    private static final int FULL_PARTY = 2;
    // TODO
    private List<Id<UserEntity>> users = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private Id<GameSession> id;
    private GameField gameField;
    private Status sessionStatus;
    RemotePointService remotePointService;

    public GameSession(RemotePointService remotePointService) {
        id = Id.of(ID_GENERATOR.getAndIncrement());
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

    public void setStatus(Status status) {
        sessionStatus = status;
    }

    public void addUser(Id<UserEntity> userId) {
        users.add(userId);
        players.add(new Player(userId.getAdditionalInfo()));
    }

    public void movePlayers(long delay) {
        players.forEach(player -> player.move((double) delay));
    }

    public void sendGameInfo() {
        List<Point> points = new ArrayList<>();

        players.forEach(player -> points.add(player.getPosition()));
        Position position = new Position(points);
        try {
            for (Id<UserEntity> user : users) {
                remotePointService.sendMessageToUser(user, position);
            }
        } catch (IOException ex) {
            LOGGER.error("error send info");
        }
    }

    public boolean isFullParty() {
        return users.size() == FULL_PARTY;
    }

}
