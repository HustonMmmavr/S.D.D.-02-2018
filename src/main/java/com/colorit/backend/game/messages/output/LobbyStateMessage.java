package com.colorit.backend.game.messages.output;

import com.colorit.backend.websocket.Message;
//statee
public class LobbyStateMessage extends Message {
    private Long lobbyId;
    private Long userId;
    private Action action;
    private OneLobbyInfo info;
    public enum Action {
        CONNECTED,
        DISCONNECTED
    }



    public LobbyStateMessage(Long lobbyId, Long userId, Action action) {
        this.lobbyId = lobbyId;
        this.userId = userId;
        this.action = action;
    }

    public Long getLobbyId() {
        return lobbyId;
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

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
