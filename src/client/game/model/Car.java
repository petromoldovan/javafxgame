package client.game.model;

import common.constants.AssetData;
import common.constants.Assets;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import network.model.enums.CarType;

import static common.constants.Constants.ASSETS;

public class Car extends Rectangle {
    
    private double x;
    private double y;

    public Car(AssetData data) {
        super(data.getWidth(), data.getHeight());
        this.setFill(new ImagePattern(new Image(data.getUrl())));

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
