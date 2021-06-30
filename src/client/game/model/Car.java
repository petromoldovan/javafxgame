package client.game.model;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Car extends Rectangle {
    
    private double x;
    private double y;

    public Car(int width, int height) {
        super(width, height);
        this.setFill(new ImagePattern(new Image("/client/resources/assets/car.png")));
    }
    
    public void draw() {
        this.setTranslateX(x);
        this.setTranslateY(y);
    }    
    
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getCarX() {
        return x;
    }

    public double getCarY() {
        return y;
    }

    public void move(final double x, final double y) {
        set(x, y);
        draw();
    }
}
