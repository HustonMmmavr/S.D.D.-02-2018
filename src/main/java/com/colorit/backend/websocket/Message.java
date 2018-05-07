package com.colorit.backend.websocket;

import com.colorit.backend.game.messages.output.Connected;
import com.colorit.backend.game.messages.input.ClientSnapshot;
import com.colorit.backend.game.messages.output.GameStart;
import com.colorit.backend.game.messages.JoinGame;
import com.colorit.backend.game.messages.output.Position;
import com.colorit.backend.game.messages.input.LobbyMessage;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @Type(JoinGame.Request.class),
        @Type(Connected.class),
        @Type(GameStart.class),
        @Type(Position.class),
        @Type(GameStart.class),
        @Type(ClientSnapshot.class),
        @Type(LobbyMessage.class)
})
public abstract class Message {
}
