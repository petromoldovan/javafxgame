package client.controller;

import client.StartClient;
import client.game.SocketManager;
import client.game.model.Car;
import client.game.model.Frog;
import client.game.model.Player;
import client.screen.AppScreen;
import common.constants.Assets;
import common.constants.Constants;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import network.entity.StateChange;
import network.entity.enums.FrogMove;

import java.util.*;

import static common.constants.Constants.*;

public class GameController {

    private Stage stage;
    private final Frog[] frogs = new Frog[] {null, null};
    private final Map<Integer, Car> carMap = new HashMap<>();
    private final SocketManager socketManager = StartClient.getSocketManager();
    private Pane root;
    private final DoubleProperty timeLeft = new SimpleDoubleProperty(GAME_TIME);
    private final IntegerProperty frog1score = new SimpleIntegerProperty(0);
    private final IntegerProperty frog2score = new SimpleIntegerProperty(0);
    private HBox rightLives;
    private HBox leftLives;
    private volatile boolean moving = false;

    public static void setPlayer1(Player p) {
    }
    public static void setPlayer2(Player p) {
    }
    public static void isControllingFirstFrog(boolean b) {
    }

    public void onChangeState(final StateChange change) {
        Platform.runLater(() -> {
            try {
                onChange(change);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void onChange(final StateChange change) {
        updateFrog(change.getFrog1(), change);
        updateFrog(change.getFrog2(), change);
        List<Car> list = new ArrayList<>(20);
        change.getCars().forEach(each -> {
            final int id = each.getId();
            final double x = each.getX();
            final double y = each.getY();
            Car car = carMap.get(id);
            if (car == null) {
                car = new Car(Assets.CARS.getCarsData(each.getType(), each.leftToRight()));
                carMap.put(id, car);
                car.set(x, y);
                car.draw();
                list.add(car);
            } else {
                car.move(x, y);
            }
        });
        root.getChildren().addAll(list);
        list.clear();
        change.getCarRemoval().forEach(car -> {
            final Car remove = carMap.remove(car.getId());
            list.add(remove);
        });
        root.getChildren().removeAll(list);
        if (change.hasTime()) setTimeLeft((double) change.getTime() / (GAME_TIME * 1000));
//        if (change.hasFrog1Scores()) {
//            frog1score.setValue(change.getFrog1scores());
//            showMessage(String.format("YOUR SCORE: %d", change.getFrog1scores()));
//        } else if (change.hasFrog2Scores()) {
//            frog2score.setValue(change.getFrog2scores());
//            showMessage(String.format("YOUR SCORE: %d", change.getFrog2scores()));
//        }
    }

    private void updateFrog(network.model.Frog newFrog, StateChange change) {
        if (null == newFrog) return;
        int i = newFrog.isFirst() ? 0 : 1;
        final double x = newFrog.getX();
        final double y = newFrog.getY();
        Frog frog = frogs[i];
        if (null == frog) {
            frog = new Frog(Assets.FROG.getFrogData(newFrog.isFirst()), Assets.FROG.getDeadFrogData());
            frogs[i] = frog;
            root.getChildren().add(frog);
            frog.reset(x, y);
        } else {
            if (newFrog.isDead()) {
                frog.move(x, y);
                frog.setDead();
                onFrogDeath(newFrog, change);
            } else {
                if (frog.isDead()) {
                    frog.setAlive();
                    frog.reset(x, y);
                } else {
                    frog.move(x, y);
                    moving = true;
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            moving = false;
                        }
                    }, Constants.FROG_MOVE_TIME);
                }
            }
        }
    }

    private void onFrogDeath(final network.model.Frog each, final StateChange change) {
        HBox hBox;
        int index;
        boolean tryAgain;
        if (each.isFirst()) {
            hBox = leftLives;
            int deaths = change.getFrog1deaths();
            index = Constants.FROG_LIVES - deaths;
            tryAgain = deaths < FROG_LIVES;
        } else {
            hBox = rightLives;
            int deaths = change.getFrog2deaths();
            index = change.getFrog2deaths() - 1;
            tryAgain = deaths < FROG_LIVES;
        }
        hBox.getChildren().remove(index);
        hBox.getChildren().add(index, newEmptyLive());
        if (tryAgain) showMessage("TRY AGAIN!");
    }

    private Node newEmptyLive() {
        Rectangle rectangle = new Rectangle(LIVE_WIDTH, LIVE_HEIGHT);
        rectangle.setFill(new ImagePattern(new Image("/client/resources/assets/liveEmpty.png")));
        return rectangle;
    }

    public void onWinEvent() {
        showGameOverMessage(true);
        socketManager.stop();
    }

    public void onLoseEvent() {
        showGameOverMessage(false);
        socketManager.stop();
    }

    public void onTimeout() {
        showMessage("TIME IS OUT!");
    }

    public void show() {
        Scene scene = new Scene(new Pane(), WIDTH, HEIGHT);
        scene.getStylesheets().add(ASSETS + "/main.css");
        stage = new Stage();
        stage.setScene(scene);
        stage.show();
        AppScreen.hide();
    }
    
    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);
        
        Rectangle road = new Rectangle(WIDTH, HEIGHT, Color.WHITE);
        road.setFill(new ImagePattern(new Image("/client/resources/assets/bg.png")));
        root.getChildren().add(road);        
        
        ProgressBar time = new ProgressBar(1);
        time.progressProperty().bindBidirectional(timeLeft);

        Label scores1 = new Label();
        Label scores2 = new Label();
        StringConverter<Number> converter = new StringConverter<>() {
            @Override
            public String toString(final Number number) {
                return String.valueOf(number);
            }

            @Override
            public Number fromString(final String s) {
                return Integer.parseInt(s);
            }
        };
        scores1.textProperty().bindBidirectional(frog1score, converter);
        scores2.textProperty().bindBidirectional(frog2score, converter);

        BorderPane pane = new BorderPane();
        pane.setPrefWidth(WIDTH);
        pane.setPadding(new Insets(10));
        final BorderPane node = new BorderPane();
        node.setLeft(scores1);
        node.setCenter(time);
        node.setRight(scores2);
        pane.setCenter(node);

        leftLives = new HBox(newLives("/client/resources/assets/live.png"));
        leftLives.setPrefWidth(LIVE_WIDTH);
        pane.setLeft(leftLives);

        rightLives = new HBox(newLives("/client/resources/assets/live2.png"));
        rightLives.setPrefWidth(LIVE_WIDTH);
        pane.setRight(rightLives);

        root.getChildren().add(pane);

        return root;
    }

    private Rectangle[] newLives(final String image) {
        final Rectangle[] result = new Rectangle[FROG_LIVES];
        for (int i = 0; i < result.length; i++) {
            Rectangle rectangle = new Rectangle(LIVE_WIDTH, LIVE_HEIGHT);
            rectangle.setFill(new ImagePattern(new Image(image)));
            result[i] = rectangle;
        }
        return result;
    }

    public void showGameOverMessage(boolean win) {
        String message = win ? "YOU WIN!" : "YOU LOSE!";
        showMessage(message);
    }
    
    public void showMessage(String message) {
        Platform.runLater(() -> {
            final VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setPrefWidth(WIDTH);
            vBox.setTranslateY(HEIGHT / 2d);
            root.getChildren().add(vBox);
            Label label = new Label(message);
            label.setOpacity(0);
            label.setFont(Font.font("Showcard Gothic", 60));
            label.setTextFill(Color.WHITE);
            DropShadow shadow = new DropShadow(30, Color.GREY);
            shadow.setOffsetX(-15.0);
            shadow.setOffsetY(15.0);
            label.setEffect(shadow);
            vBox.getChildren().add(label);
            FadeTransition ft = new FadeTransition(Duration.seconds(0.66), label);
            ft.setToValue(1);
            ft.play();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() ->root.getChildren().remove(vBox));
                }
            }, Constants.FROG_DEAD_TIME);
        });
    }

    public void setTimeLeft(double time) {
        Platform.runLater(() -> timeLeft.setValue(time));
    }

    public void startGame() {
        Scene scene = new Scene(createContent(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.getScene().setOnKeyPressed(event -> {
            if (moving) return;
            FrogMove move;
            switch (event.getCode()) {
                case W:
                case UP:
                    move = FrogMove.UP;
                    break;
                case S:
                case DOWN:
                    move = FrogMove.DOWN;
                    break;
                case A:
                case LEFT:
                    move = FrogMove.LEFT;
                    break;
                case D:
                case RIGHT:
                    move = FrogMove.RIGHT;
                    break;
                default:
                    return;
            }
            socketManager.frogMove(move);
        });
    }
}
