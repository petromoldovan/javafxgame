package network.model;

import static common.constants.Constants.UNIT_SIZE;

public class Car {

    private final int id;
    private final double width;
    private final double height;

    private double speed;
    private double x;
    private double y;
    private boolean spawn;

    public Car(final int id, final double width, final double height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void update() {
        this.x = this.x + speed;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean hits(final Frog frog) {
        //    return x < r.x + r.width && x + width > r.x && y < r.y + r.height && y + height > r.y;
        return x < frog.getX() + UNIT_SIZE 
                && x + width > frog.getX() 
                && y < frog.getY() + UNIT_SIZE 
                && y + height > frog.getY();
    }
    
    public void setX(final int x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }

    public double getY() {
        return y;
    }

    public void setSpawn(final boolean spawn) {
        this.spawn = spawn;
    }

    public boolean isSpawn() {
        return spawn;
    }

    @Override
    public String toString() {
        return "Car{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
