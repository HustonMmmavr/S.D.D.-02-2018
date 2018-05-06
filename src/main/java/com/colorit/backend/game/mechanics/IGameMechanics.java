package com.colorit.backend.game.mechanics;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.Direction;

import javax.validation.constraints.NotNull;

public interface IGameMechanics {
    // TODO fix
    void addClientSnapshot(@NotNull Id<UserEntity> userId, @NotNull Object clientSnap);

    void changeDirection(@NotNull Id<UserEntity> userId, @NotNull Direction direction);

    void gameStep(long frameTime);

    void reset();
}
