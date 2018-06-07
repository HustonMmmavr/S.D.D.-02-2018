package com.colorit.backend.game.messages.output;

import com.colorit.backend.entities.Id;
import com.colorit.backend.game.gameobjects.players.Player;

import java.util.List;


// возможно timestamo
// sample 3.1 пример для бонуса
public class GameStart extends LobbyOutMessage {
    private List<Id<Player>> players;

    public GameStart(List<Id<Player>> players) {
        this.players = players;
    }

    public List<Id<Player>> getPlayers() {
        return players;
    }

    public void setPlayers(List<Id<Player>> players) {
        this.players = players;
    }
}
