package common.constants;

public class Constants {
    
//    public static final int WIDTH = 1920;
//    public static final int HEIGHT = 1532;
    public static final int WIDTH = 600;
    public static final int HEIGHT = 500;
//    public static final int WIDTH = 1024;
//    public static final int HEIGHT = 768;

    public static final int TOP_ROAD_START = 81;
    public static final int BOTTOM_ROAD_START = 321;
//    public static final int TOP_ROAD_START = 250;
//    public static final int BOTTOM_ROAD_START = 893;
    
    public static final double CAR_SPEED_MODIFIER = 6;
    
    public static final int BLOCK_SIZE = 30;
    public static final int UNIT_SIZE = BLOCK_SIZE - 2;
    public static final int START_Y = HEIGHT - UNIT_SIZE;
    public static final int START_X1 = UNIT_SIZE;
    public static final int START_X2 = HEIGHT - UNIT_SIZE;
    
    public static final int GAME_TICK_TIMER = 25;

    public static final int FROG_LIVES = 5;
    public static final int GAME_TIME = 60;

    public static final int FROG_DEAD_TIME = 1500; //ms
    public static final double LIVE_WIDTH = 51 / 2d;
    public static final double LIVE_HEIGHT = 38 / 2d;
    public static final int FROG_TRANSITION_TIME = 50; //ms
    public static final int FROG_ROTATION_TIME = 50; //ms
    public static final int FROG_MOVE_TIME = FROG_ROTATION_TIME + FROG_TRANSITION_TIME; //ms
}
