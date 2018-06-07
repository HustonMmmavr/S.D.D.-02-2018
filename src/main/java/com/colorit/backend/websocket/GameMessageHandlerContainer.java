package com.colorit.backend.websocket;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Service
public class GameMessageHandlerContainer implements MessageHandlerContainer {
    private static final @NotNull Logger LOGGER = LoggerFactory.getLogger(GameMessageHandlerContainer.class);
    private final Map<Class<?>, MessageHandler<?>> handlerMap = new HashMap<>();

    @Override
    public void handle(@NotNull Message message, @NotNull Id<UserEntity> forUser) throws HandleException {

        final MessageHandler<?> messageHandler = handlerMap.get(message.getClass());
        if (messageHandler == null) {
            throw new HandleException("no handler for message of " + message.getClass().getName() + " type");
        }
        messageHandler.handleMessage(message, forUser);
        LOGGER.trace("message handled: type =[" + message.getClass().getName() + ']');
    }

    @Override
    public <T extends Message> void registerHandler(@NotNull Class<T> clazz, MessageHandler<T> handler) {
        handlerMap.put(clazz, handler);
    }
}
