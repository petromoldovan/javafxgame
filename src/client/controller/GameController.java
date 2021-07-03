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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import network.entity.StateChange;
import network.entity.enums.FrogMove;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static common.constants.Constants.*;

public class GameController {

    private Stage stage;
    private final Frog[] frogs = new Frog[] {null, null};
    private final Map<Integer, Car> carMap = new HashMap<>();
    private final SocketManager socketManager = StartClient.getSocketManager();
    private Pane root;
    private final DoubleProperty timeLeft = new SimpleDoubleProperty(GAME_TIME);
    private HBox rightLives;
    private HBox leftLives;
    private volatile boolean moving = false;
    private HBox hBox;

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
        change.getCars().forEach(each -> {
            final int id = each.getId();
            final double x = each.getX();
            final double y = each.getY();
            Car car = carMap.get(id);
            if (car == null) {
                car = new Car(Assets.CARS.getCarsData(each.getType(), each.leftToRight()));
                root.getChildren().add(car);
                carMap.put(id, car);
                car.set(x, y);
                car.draw();
            } else {
                car.move(x, y);
            }
        });
        if (change.hasTime()) setTimeLeft((double) change.getTime() / (GAME_TIME * 1000));    
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
        
        BorderPane pane = new BorderPane();
        pane.setPrefWidth(WIDTH);
        pane.setPadding(new Insets(10));
        pane.setCenter(time);

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
            hBox = new HBox();
            hBox.setTranslateX( (WIDTH / 2d) - 130 );
            hBox.setTranslateY(HEIGHT / 2d);
            final VBox vBox = new VBox(hBox);
            vBox.setAlignment(Pos.CENTER);
            vBox.setPrefWidth(WIDTH);
            root.getChildren().add(vBox);
            
            for (int i = 0; i < message.length(); i++) {
                char letter = message.charAt(i);
                Text text = new Text(String.valueOf(letter));
                text.setOpacity(0);
                text.setStyle("-fx-dark-text-color: #ffffff;");
                text.setFont(Font.font("Letter Magic", 60));
                DropShadow shadow = new DropShadow(40, Color.BLACK);
                text.setEffect(shadow);
                hBox.getChildren().add(text);
                FadeTransition ft = new FadeTransition(Duration.seconds(0.66), text);
                ft.setToValue(1);
                ft.setDelay(Duration.seconds(i * 0.15));
                ft.play();
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (hBox != null) hBox.setVisible(false);
                    });
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
