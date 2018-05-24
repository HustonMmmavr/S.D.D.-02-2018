package com.colorit.backend.game.messages.output;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.lobby.Lobby;
import com.colorit.backend.websocket.Message;
//statee
public class LobbyStateMessage extends Message {
    private Long lobbyId;
    private String nickname;
    private Long userId;
    private Action action;
    private OneLobbyInfo info;

    public enum Action {
        CONNECTED,
        DISCONNECTED,
        READY
    }

    public LobbyStateMessage(Id<Lobby> lobbyId, Id<UserEntity> userId, Action action) {
        this.lobbyId = lobbyId.getId();
        if (userId != null) {
            this.userId = userId.getId();
            this.nickname = userId.getAdditionalInfo();
        }
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

    public String getNickname() {
        return nickname;
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
