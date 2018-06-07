package com.colorit.backend.websocket;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;

import javax.validation.constraints.NotNull;

public interface MessageHandlerContainer {

    void handle(@NotNull Message message, @NotNull Id<UserEntity> forUser) throws HandleException;

    <T extends Message> void registerHandler(@NotNull Class<T> clazz, MessageHandler<T> handler);
}
