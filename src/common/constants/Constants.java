package common.constants;

public class Constants {
    
    public static final int WIDTH = 969;
    public static final int HEIGHT = 717;

    public static final int TOP_ROAD_START = 111;
    public static final int BOTTOM_ROAD_START = 411;

    public static final int BLOCK_SIZE = 51;
    public static final int FROG_SIZE = 45;
    public static final double FROG_HIT_BOX_CORRECTION = FROG_SIZE * 0.5;
    public static final int START_Y = HEIGHT - FROG_SIZE;
    public static final int START_X1 = 5 * BLOCK_SIZE;
    public static final int START_X2 = WIDTH - 5 * BLOCK_SIZE;

    public static final int FINISH = TOP_ROAD_START;
    
    public static final int GAME_TICK_TIMER = 25;

    public static final int FROG_LIVES = 5;
    public static final int GAME_TIME = 60;

    public static final int FROG_DEAD_TIME = 2000; //ms
    public static final double LIVE_WIDTH = 51 / 2d;
    public static final double LIVE_HEIGHT = 38 / 2d;
    public static final int FROG_TRANSITION_TIME = 30; //ms
    public static final int FROG_ROTATION_TIME = 70; //ms
    public static final int FROG_MOVE_TIME = FROG_ROTATION_TIME + FROG_TRANSITION_TIME; //ms
    
    public static final String ASSETS = "/client/resources/assets";
    public static final double CAR_SPEED_MULTIPLIER = 1000.0 / GAME_TICK_TIMER * 0.4;
    public static final int FINISH_SCORE = 100;
}
