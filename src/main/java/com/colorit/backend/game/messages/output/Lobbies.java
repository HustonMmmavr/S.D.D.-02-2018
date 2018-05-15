package com.colorit.backend.game.messages.output;

import com.colorit.backend.websocket.Message;

import java.util.List;

public class Lobbies extends Message {
    public static class OneLobby {
        String owner;
        long id;
        String name;
        long countPlayers;

        public OneLobby(long id, String name, long countPlayers, String owner) {
            this.countPlayers = countPlayers;
            this.name = name;
            this.owner = owner;
            this.id = id;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getCountPlayers() {
            return countPlayers;
        }

        public void setCountPlayers(long countPlayers) {
            this.countPlayers = countPlayers;
        }
    }

    private final List<OneLobby> lobbies;

    public Lobbies(List<OneLobby> lobbies) {
        this.lobbies = lobbies;
    }

    public List<OneLobby> getLobbies() {
        return lobbies;
    }
}