package com.colorit.backend.game.gameobjects;

import com.colorit.backend.entities.Id;

import javax.validation.constraints.NotNull;
import java.util.concurrent.atomic.AtomicLong;

public class GameObject {
    private static final AtomicLong ID_GENERATOR = new AtomicLong();
    @NotNull
    protected final Id<GameObject> id;

    public GameObject() {
        this.id = Id.of(ID_GENERATOR.incrementAndGet());
    }

    public Id<?> getId() {
        return id;
    }
}
