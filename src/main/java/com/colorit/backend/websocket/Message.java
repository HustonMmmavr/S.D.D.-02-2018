package com.colorit.backend.websocket;

import com.colorit.backend.game.messages.input.ShowLobbies;
import com.colorit.backend.game.messages.output.*;
import com.colorit.backend.game.messages.input.ClientSnapshot;
import com.colorit.backend.game.messages.input.LobbyMessage;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @Type(ShowLobbies.Request.class),
        @Type(Connected.class),
        @Type(ClientSnapshot.class),
        @Type(LobbyMessage.class),
        @Type(LobbyError.class),
        @Type(LobbyUsers.class),
        @Type(ServerSnapshot.class)
})
public abstract class Message {
}
