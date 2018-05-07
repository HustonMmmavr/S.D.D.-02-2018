package com.colorit.backend.game.messages.output;

import com.colorit.backend.game.gameobjects.math.Point;
import com.colorit.backend.game.session.GameSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServerSnapshot {
    private List<List<Integer>> gameField = new ArrayList<>();
    private List<Point> coordinates = new ArrayList<>();
    private HashMap<Long, Integer> scores = new HashMap<>();
    private Integer frameTime = 0;

    public static ServerSnapshot getSnapshot(GameSession gameSession) {
        ServerSnapshot snapshot = new ServerSnapshot();
        gameSession.getPlayers().forEach(player -> snapshot.scores.put(player.getId().getId(), player.getScore()));
        return snapshot;
    }
}
