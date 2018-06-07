package com.colorit.backend.websocket;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;

import javax.validation.constraints.NotNull;


public abstract class MessageHandler<T extends Message> {
    private final @NotNull Class<T> clazz;

    public MessageHandler(@NotNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public void handleMessage(@NotNull Message message, @NotNull Id<UserEntity> forUser) throws HandleException {
        try {
            handle(clazz.cast(message), forUser);
        } catch (ClassCastException ex) {
            throw new HandleException("Can't read incoming message of type " + message.getClass(), ex);
        }
    }

    public abstract void handle(@NotNull T message, @NotNull Id<UserEntity> forUser) throws HandleException;
}
