package client.game.model;

import client.game.model.enums.Direction;
import common.constants.AssetData;
import common.constants.Assets;
import common.constants.Constants;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static common.constants.Constants.FROG_TRANSITION_TIME;
import static common.constants.Constants.FROG_ROTATION_TIME;

public class Frog extends Rectangle {

    private final ImagePattern frog;
    private final ImagePattern dead;
    private Direction direction;

    public Frog(AssetData data, AssetData deadData) {
        super(data.getWidth(), data.getHeight());
        frog = new ImagePattern(new Image(data.getUrl()));
        dead = new ImagePattern(new Image(deadData.getUrl()));
        setFill(frog);
        direction = Direction.UP;
    }

    public void move(double x, double y) {
        final double fromX = getTranslateX();
        final double fromY = getTranslateY();
        System.out.printf("from (%f %f) to (%f %f)\n", fromX, fromY, x, y);
        Direction newDirection = Direction.find(fromX, fromY, x, y);
        rotateTo(newDirection);
        TranslateTransition translate = new TranslateTransition(Duration.millis(FROG_TRANSITION_TIME), this);
        translate.setFromX(fromX);
        translate.setToX(x);
        translate.setFromY(fromY);
        translate.setToY(y);
        translate.play();
        setTranslateX(x);
        setTranslateY(y);
    }

    private void rotateTo(final Direction direction) {
        rotateTo(direction, FROG_ROTATION_TIME);
    }
    
    private void rotateTo(final Direction newDirection, final int duration) {
        final int angle = direction.findAngle(newDirection);
        if (angle == 0) return;
        RotateTransition rt = new RotateTransition(Duration.millis(duration), this);
        rt.setByAngle(angle);
        rt.play();    
        direction = newDirection;
    }

    public void reset(final double x, final double y) {
        setTranslateX(x);
        setTranslateY(y);
        rotateTo(Direction.UP, 1);
    }

    public void setAlive() {
        setFill(frog);
    }
    
    public void setDead() {
        setFill(dead);
    }
    
    public boolean isDead() {
        return getFill() == dead;
    }
}
