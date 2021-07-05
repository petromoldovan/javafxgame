package client.game.model;

import common.constants.AssetData;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Car extends Rectangle {
    
    private double x;
    private double y;

    public Car(AssetData data) {
        super(data.getWidth(), data.getHeight());
        setFill(new ImagePattern(new Image(data.getUrl())));
        setStyle("-fx-background-color: red; -fx-border-style: solid; -fx-border-width: 5; -fx-border-color: black; -fx-min-width: 20; -fx-min-height:20; -fx-max-width:20; -fx-max-height: 20;");
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
