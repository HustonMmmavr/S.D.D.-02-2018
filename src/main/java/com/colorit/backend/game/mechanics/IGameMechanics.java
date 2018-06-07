package com.colorit.backend.game.mechanics;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.messages.input.ClientSnapshot;

import javax.validation.constraints.NotNull;

public interface IGameMechanics {
    void addClientSnapshot(@NotNull Id<UserEntity> userId, @NotNull ClientSnapshot clientSnap);

    void gameStep(long frameTime);

    void reset();
}
