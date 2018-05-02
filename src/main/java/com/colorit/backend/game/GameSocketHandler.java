package com.colorit.backend.game;

import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.services.IUserService;
import com.colorit.backend.services.UserServiceJpa;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.validation.constraints.NotNull;

import static org.springframework.web.socket.CloseStatus.SERVER_ERROR;


@Component
public class GameSocketHandler extends TextWebSocketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameSocketHandler.class);
    private static final CloseStatus ACCESS_DENIED = new CloseStatus(4500, "Not logged in. Access denied");
    private static final String SESSION_KEY = "nickname";
    //@NotNull
//    private final MessageHandlerContainer messageHandlerContainer;
//    @NotNull
//    private final RemotePointService remotePointService;

    @NotNull
    private final IUserService userService;
    @NotNull
    private final ObjectMapper objectMapper;


    public GameSocketHandler(@NotNull ObjectMapper objectMapper,
                             @NotNull IUserService userService)
    /*@NotNull MessageHandlerContainer messageHandlerContainer,
                             @NotNull AccountService authService,
                             @NotNull RemotePointService remotePointService,
                             ObjectMapper objectMapper) {*/
    {
     //   this.messageHandlerContainer = messageHandlerContainer;
       // this.accountService = authService;
        //this.remotePointService = remotePointService;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) {
        final String nickname = (String) webSocketSession.getAttributes().get(SESSION_KEY);
        LOGGER.info(nickname);
//        LOGGER.info(webSocketSession.getId());
        if (nickname == null || userService.getUser(nickname) == null) {
            LOGGER.warn("User requested websocket is not registred or not logged in. Openning websocket session is denied.");
            closeSessionSilently(webSocketSession, ACCESS_DENIED);
            return;
        }

        LOGGER.info(nickname);

//        final Id<UserProfile> userId = Id.of(id);
//        remotePointService.registerUser(userId, webSocketSession);
    }

    @Override
    protected void handleTextMessage(WebSocketSession webSocketSession, TextMessage message) throws Exception {
        if (!webSocketSession.isOpen()) {
            return;
        }
        //final Long userId = (Long) webSocketSession.getAttributes().get("userId");


        final Integer data = webSocketSession.getBinaryMessageSizeLimit();
        LOGGER.info(data.toString());
        final String data1 = (String) webSocketSession.getAttributes().get("a");
        LOGGER.info(webSocketSession.getAttributes().toString());
        LOGGER.info(message.toString());
        webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString("Hi")));
//        final UserProfile user;
//        if (userId == null || (user = accountService.getUserById(Id.of(userId))) == null) {
//            closeSessionSilently(webSocketSession, ACCESS_DENIED);
//            return;
//        }
//        handleMessage(user, message);
    }

//    @SuppressWarnings("OverlyBroadCatchBlock")
//    private void handleMessage(UserProfile userProfile, TextMessage text) {
//        final Message message;
//        try {
//            message = objectMapper.readValue(text.getPayload(), Message.class);
//        } catch (IOException ex) {
//            LOGGER.error("wrong json format at game response", ex);
//            return;
//        }
//        try {
//            noinspection ConstantConditions
//            messageHandlerContainer.handle(message, userProfile.getId());
//        } catch (HandleException e) {
//            LOGGER.error("Can't handle message of type " + message.getClass().getName() + " with content: " + text, e);
//        }
//    }
//
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
//        remotePointService.removeUser(Id.of(userId));
    }

    @SuppressWarnings("SameParameterValue")
    private void closeSessionSilently(@NotNull WebSocketSession session, @Nullable CloseStatus closeStatus) {
        final CloseStatus status = closeStatus == null ? SERVER_ERROR : closeStatus;
        //noinspection OverlyBroadCatchBlock
        try {
            session.close(status);
        } catch (Exception ignore) {
        }

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
