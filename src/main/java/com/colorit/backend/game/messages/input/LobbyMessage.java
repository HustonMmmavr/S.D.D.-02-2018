package com.colorit.backend.game.messages.input;

import com.colorit.backend.websocket.Message;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LobbyMessage extends Message {
    public static class LobbyCreate {
        Integer fieldSize;
        Boolean isMultiplayer;

        @JsonProperty("fieldSize")
        public Integer getFieldSize() {
            return fieldSize;
        }

        public void setFieldSize(Integer fieldSize) {
            this.fieldSize = fieldSize;
        }

        @JsonProperty("isMultiplayer")
        public Boolean getMultiplayer() {
            return isMultiplayer;
        }

        public void setMultiplayer(Boolean multiplayer) {
            isMultiplayer = multiplayer;
        }
    }

    public static class LobbyStart {
    }


}
