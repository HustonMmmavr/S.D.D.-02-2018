package com.colorit.backend.game.gameobjects.bonus;

import com.colorit.backend.game.gameobjects.GameObject;
import com.colorit.backend.game.gameobjects.Snap;

public class Bonus extends GameObject {

    public BonusSnap getSnap() {
        return null;
    }

    public static class BonusSnap implements Snap<Bonus> {

    }
}
