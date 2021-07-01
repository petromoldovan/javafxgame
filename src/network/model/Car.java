package network.model;

import common.constants.AssetData;
import common.constants.Assets;
import network.model.enums.CarType;

import static common.constants.Constants.FROG_SIZE;
import static common.constants.Constants.FROG_HIT_BOX_CORRECTION;

public class Car {

    private final int id;
    private final double width;
    private final double height;
    private final CarType carType;

    private double speed;
    private double x;
    private double y;
    private boolean spawn;

    public Car(final int id) {
        carType = Math.random() > 0.5d ? CarType.SEDAN : CarType.WAGON;
        final AssetData data = Assets.CARS.getCarsData(carType, speed > 0);
        width = data.getWidth();
        height = data.getHeight() - 2;
        this.id = id;
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
        return x < frog.getX() + FROG_SIZE - FROG_HIT_BOX_CORRECTION
                && x + width > frog.getX() + FROG_HIT_BOX_CORRECTION
                && y < frog.getY()
                && y + height > frog.getY(); // simplify y
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
    
    public boolean leftToRight() {
        return speed > 0;
    }

    @Override
    public String toString() {
        return "Car{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public CarType getType() {
        return carType;
    }
}
