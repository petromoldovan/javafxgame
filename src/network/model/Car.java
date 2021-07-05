package network.model;

import common.constants.AssetData;
import common.constants.Assets;
import network.model.enums.CarType;

public class Car implements Model {

    private final int id;
    private final double width;
    private final double height;
    private final CarType type;
    
    private double speed;
    private double x;
    private double y;

    public Car(final int id, final CarType carType, final double speed) {
        this.id = id;
        this.speed = speed;
        this.type = carType;
        final AssetData data = Assets.CARS.getCarsData(carType, speed > 0);
        width = data.getWidth();
        height = data.getHeight() - 2;
    }

    public void update() {
        this.x = this.x + speed;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
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

    @Override
    public double getHeight() {
        return height;
    }

    public int getId() {
        return id;
    }

    public double getY() {
        return y;
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
        return type;
    }
    
    public boolean isMovingToTheRight() {
        return speed > 0;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(final double speed) {
        this.speed = speed;
    }
}
