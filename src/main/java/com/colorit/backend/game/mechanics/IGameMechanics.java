package com.colorit.backend.game.mechanics;

import com.colorit.backend.entities.Id;
import com.colorit.backend.entities.db.UserEntity;
import com.colorit.backend.game.gameobjects.Direction;

import javax.validation.constraints.NotNull;

public interface IGameMechanics {
    void changeDirection(@NotNull Id<UserEntity> userId, @NotNull Direction direction);

    void gmStep(long frameTime);

    void reset();
}
