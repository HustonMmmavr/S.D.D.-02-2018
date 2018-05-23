package com.colorit.backend.game.gameobjects.bonus;

import com.colorit.backend.game.gameobjects.GameObject;
import com.colorit.backend.game.gameobjects.Snap;
import com.colorit.backend.game.gameobjects.math.Point;

public class Bonus extends GameObject {

    public enum BonusType {
        SLOW(0, 3),
        STOP_ENEMIES(1, 3),
        FILL_LINE(2, 0);

        private int id;
        private int lifeTime;

        BonusType(int id, int lifeTime) {
            this.id = id;
            this.lifeTime = lifeTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getLifeTime() {
            return lifeTime;
        }

        public void setLifeTime(int lifeTime) {
            this.lifeTime = lifeTime;
        }
    }

    private Point point;
    private final long expiresIn;
    private final BonusType type;

    public Bonus() {
        point = new Point(0,0);
        expiresIn = 0;
        type = BonusType.FILL_LINE;
    }


    @Override
    public BonusSnap getSnap() {
        return null;
    }

    public static class BonusSnap implements Snap<Bonus> {

    }
}
