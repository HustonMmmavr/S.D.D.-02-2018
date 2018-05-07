package com.colorit.backend.game.messages.input;

import com.colorit.backend.game.lobby.LobbySettings;
import com.colorit.backend.websocket.Message;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LobbyMessage extends Message {
    private LobbySettings settings;
    private Action action;

    public enum Action {
        @JsonProperty("CREATE")
        CREATE,
        @JsonProperty("CONNECT")
        CONNCECT,
        @JsonProperty("DISCONNECT")
        DISCONNECT,
        @JsonProperty("CHAT")
        CHAT
    }


    @JsonProperty("settings")
    public LobbySettings getSettings() {
        return settings;
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
