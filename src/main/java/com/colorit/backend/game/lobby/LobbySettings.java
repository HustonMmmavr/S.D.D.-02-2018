package com.colorit.backend.game.lobby;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LobbySettings {
    private Integer gameTime;
    private Integer fieldSize;
    private Boolean isMultiplayer;
    private String name;

    @JsonProperty("gameTime")
    public Integer getGameTime() {
        return gameTime;
    }


    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setGameTime(Integer gameTime) {
        this.gameTime = gameTime;
    }

    @JsonProperty("fieldSize")
    public Integer getFieldSize() {
        return fieldSize;
    }

    @JsonProperty("isMultiplayer")
    public Boolean getMultiplayer() {
        return isMultiplayer;
    }

    public void setMultiplayer(Boolean multiplayer) {
        isMultiplayer = multiplayer;
    }

    public void setFieldSize(Integer fieldSize) {
            this.fieldSize = fieldSize;
        }
}
