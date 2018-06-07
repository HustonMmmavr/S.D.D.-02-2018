package com.colorit.backend.game.messages.input;

import com.colorit.backend.game.lobby.LobbySettings;
import com.colorit.backend.websocket.Message;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LobbyMessage extends Message {
    private LobbySettings settings;
    private Action action;
    private Long lobbyId;
    private Long userId;

    public enum Action {
        @JsonProperty("CREATE")
        CREATE,
        @JsonProperty("CONNECT")
        CONNCECT,
        @JsonProperty("DISCONNECT")
        DISCONNECT,
        @JsonProperty("CHAT")
        CHAT,
        @JsonProperty("START")
        START
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @JsonProperty("settings")
    public LobbySettings getSettings() {
        return settings;
    }

    // its need when we try connect to lobby
    @JsonProperty("id")
    public Long getLobbyId() {
        return lobbyId;
    }

    public void setSettings(LobbySettings settings) {
        this.settings = settings;
    }

    @JsonProperty("action")
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
