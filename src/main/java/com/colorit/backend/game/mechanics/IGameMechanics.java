package com.colorit.backend.game.mechanics;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.Direction;
import com.colorit.backend.game.messages.ClientSnapshot;

import javax.validation.constraints.NotNull;

public interface IGameMechanics {
    void addClientSnapshot(@NotNull Id<UserEntity> userId, @NotNull ClientSnapshot clientSnap);

    void changeDirection(@NotNull Id<UserEntity> userId, @NotNull Direction direction);

    void gameStep(long frameTime);

    void reset();
}
