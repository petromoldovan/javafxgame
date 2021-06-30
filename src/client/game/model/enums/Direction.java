package client.game.model.enums;

public enum Direction {
    
    // UP DOWN LEFT RIGHT
    UP(0, 180, -90, 90), 
    DOWN(180, 0, 90, -90), 
    LEFT(90, -90, 0, 180), 
    RIGHT(-90, 90, 180, 0);

    private final int up;
    private final int down;
    private final int left;
    private final int right;
    Direction(final int up, final int down, final int left, final int right) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }

    public static Direction find(final double fromX, final double fromY, final double toX, final double toY) {
        if (fromX != toX) return (fromX > toX) ? LEFT : RIGHT;
        if (fromY != toY) return (fromY > toY) ? UP   : DOWN;
        return UP;
    }

    public int findAngle(final Direction newDirection) {
        switch (newDirection) {
            case UP: return up;
            case DOWN: return down;
            case LEFT: return left;
            case RIGHT: return right;
            default: return 0;
        }
    }
}
