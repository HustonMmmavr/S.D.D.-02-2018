package com.colorit.backend.websocket;

import com.colorit.backend.game.Connected;
import com.colorit.backend.game.messages.GameStart;
import com.colorit.backend.game.messages.JoinGame;
import com.colorit.backend.game.messages.Position;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @Type(JoinGame.Request.class),
        @Type(Connected.class),
        @Type(GameStart.class),
        @Type(Position.class),
        @Type(GameStart.class)
//        @Type(FinishGame.class),
})
public abstract class Message {
}
