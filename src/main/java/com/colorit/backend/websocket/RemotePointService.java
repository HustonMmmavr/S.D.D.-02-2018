package com.colorit.backend.websocket;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RemotePointService {
    private final Map<Id<UserEntity>, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RemotePointService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void registerUser(@NotNull Id<UserEntity> uId, @NotNull WebSocketSession webSocketSession) {
        sessions.put(uId, webSocketSession);
    }

    public boolean isConnected(@NotNull Id<UserEntity> uId) {//String nickname) {
        return sessions.containsKey(uId) && sessions.get(uId).isOpen();
    }

    public void removeUser(@NotNull Id<UserEntity> uId) {
        sessions.remove(uId);
    }

    public void cutDownConnection(@NotNull Id<UserEntity> uId, @NotNull CloseStatus closeStatus) {
        final WebSocketSession webSocketSession = sessions.get(uId);
        if (webSocketSession != null && webSocketSession.isOpen()) {
            try {
                webSocketSession.close(closeStatus);
            } catch (IOException ignore) {
            }
        }
    }

    public void sendMessageToUser(@NotNull Id<UserEntity> uId, @NotNull Message message) throws IOException {
        final WebSocketSession webSocketSession = sessions.get(uId);
        if (webSocketSession == null) {
            throw new IOException("no game websocket for user " + uId);
        }
        if (!webSocketSession.isOpen()) {
            throw new IOException("session is closed or not exsists");
        }
        try {
            webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            throw new IOException("Unnable to send message", e);
        }
    }
}
