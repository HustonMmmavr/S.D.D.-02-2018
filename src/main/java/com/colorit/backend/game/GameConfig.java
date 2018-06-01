package com.colorit.backend.game;

public class GameConfig {
    public static final Integer DEFAULT_VELOCITY = 10; // velocity for one server cycle
    public static final int DISTANCE = 100;
    public static final int MIN_BORDER = 0;
    // tacts - distance / velocity
    public static final int ONE_TIME_STEP = 50; //1000 / (DISTANCE / DEFAULT_VELOCITY); //40 ms one cycle
    public static final int SCORED = 5;
    public static final int BAD_AREA = 6;
    public static final int DEFAULT_FIELD_VALUE = 0;
    public static final int FULL_PARTY = 2;
    //public static final int MILISECONDS = 1000;
}
