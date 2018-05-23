package com.colorit.backend.game;

public class GameConfig {
//    public static final Integer SQUARE_SIZE = 100;
    public static final Integer DEFAULT_VELOCITY = 4;
    public static final Integer UP_VELOCITY = 2;
    public static final Double LOW_VELOCITY = 0.5;
    public static final Integer DEFAULT_FILED_SIZE = 8;
    public static final int DISTANCE = 100;
    public static final  int MIN_BORDER = 0;
    // tacts - distance / velocity
    public static final int ONE_TIME_STEP = 1000 / (DISTANCE / DEFAULT_VELOCITY);
    public static final int SCORED = 5;
    public static final int BAD_AREA = 6;
    public static final int DEFAULT_FIELD_VALUE = 0;
    public static final int MILISECONDS = 1000;
}
