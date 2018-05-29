package com.colorit.backend.websocket;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.services.IUserService;
import com.colorit.backend.services.responses.UserServiceResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.validation.constraints.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.web.socket.CloseStatus.SERVER_ERROR;


@Component
public class GameSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSocketHandler.class);
    private static final CloseStatus ACCESS_DENIED = new CloseStatus(4500, "Not logged in. Access denied");
    private static final String SESSION_KEY = "nickname";
    private final @NotNull MessageHandlerContainer messageHandlerContainer;
    private final @NotNull RemotePointService remotePointService;
    private final @NotNull IUserService userService;
    private final @NotNull ObjectMapper objectMapper;
    private final @NotNull Map<String, Id<UserEntity>> idMap = new ConcurrentHashMap<>();


    public GameSocketHandler(@NotNull ObjectMapper objectMapper,
                             @NotNull IUserService userService,
                             @NotNull MessageHandlerContainer messageHandlerContainer,
                             @NotNull RemotePointService remotePointService) {
        this.messageHandlerContainer = messageHandlerContainer;
        this.remotePointService = remotePointService;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        final String nickname = (String) webSocketSession.getAttributes().get(SESSION_KEY);
        final UserServiceResponse userServiceResponse = userService.getUserEntity(nickname);
        if (nickname == null || !userServiceResponse.isValid()) {
            LOGGER.warn("User requested websocket is not registred or not logged in. Openning websocket session is denied.");
            closeSessionSilently(webSocketSession, ACCESS_DENIED);
            return;
        }
        final UserEntity userEntity = (UserEntity) userServiceResponse.getData();
        final Id<UserEntity> uId = Id.of(userEntity.getId());
        uId.setAdditionalInfo(nickname);
        idMap.put(nickname, uId);
        remotePointService.registerUser(uId, webSocketSession);
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) throws Exception {
        if (!webSocketSession.isOpen()) {
            return;
        }
        final String nickname = (String) webSocketSession.getAttributes().get(SESSION_KEY);

        if (nickname != null) {
            final UserServiceResponse userServiceResponse = userService.getUserEntity(nickname);
            final Id<UserEntity> userId = idMap.get(nickname);

            if (!userServiceResponse.isValid() || userId == null) {
                closeSessionSilently(webSocketSession, ACCESS_DENIED);
                return;
            }
            handleMessage(userId, message);
        }
    }

    private void handleMessage(Id<UserEntity> userId, TextMessage text) {
        final Message message;
        try {
            message = objectMapper.readValue(text.getPayload(), Message.class);
        } catch (IOException ex) {
            LOGGER.error("wrong json format at game response", ex);
            return;
        }
        try {
            messageHandlerContainer.handle(message, userId);
        } catch (HandleException e) {
            LOGGER.error("Can't handle message of type " + message.getClass().getName() + " with content: " + text, e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        LOGGER.warn("Websocket transport problem", throwable);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) {
        final String nickname = (String) webSocketSession.getAttributes().get(SESSION_KEY);
        if (nickname == null) {
            LOGGER.warn("User disconnected but his session was not found (closeStatus=" + closeStatus + ')');
            return;
        }
        remotePointService.removeUser(idMap.get(nickname));
        idMap.remove(nickname);
    }

    private void closeSessionSilently(@NotNull WebSocketSession session, @Nullable CloseStatus closeStatus) {
        final CloseStatus status = closeStatus == null ? SERVER_ERROR : closeStatus;
        try {
            session.close(status);
        } catch (Exception ignore) {
            LOGGER.warn("Error close session silently");
        }

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
