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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
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

    private static boolean firstFrog;
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
    public static void isControllingFirstFrog(boolean firstFrog) {
        GameController.firstFrog = firstFrog;
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
        updateFrog(change.getFrog1());
        updateFrog(change.getFrog2());
        if (change.hasFrog1Deaths()) {
            onFrogDeath(true, change);
        }
        if (change.hasFrog2Deaths()) {
            onFrogDeath(false, change);
        }
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
        if (change.hasFrog1Scores()) {
            frog1score.setValue(change.getFrog1scores());
            if (firstFrog && change.getFrog2deaths() < FROG_LIVES) showMessage("WELL DONE!");
        } else if (change.hasFrog2Scores()) {
            frog2score.setValue(change.getFrog2scores());
            if (!firstFrog && change.getFrog1deaths() < FROG_LIVES) showMessage("WELL DONE!");
        }
    }

    private void updateFrog(network.model.Frog newFrog) {
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
            rightLives.setVisible(frogs[1] != null);
        } else {
            if (newFrog.isDead()) {
                frog.set(x, y);
                frog.setDead();
            } else {
                if (frog.isDead()) {
                    frog.setAlive();
                    frog.reset(x, y);
                } else {
                    if (newFrog.isReset()) {
                        frog.reset(x, y);
                    } else {
                        frog.move(x, y);
                    }
                    if (newFrog.isFirst() == firstFrog) {
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
    }

    private void onFrogDeath(final boolean first, final StateChange change) {
        HBox hBox;
        int index;
        boolean tryAgain;
        if (first) {
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
        if (tryAgain) {
            if (firstFrog == first) showMessage("TRY AGAIN!");
        }
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
        AppScreen.show(stage);
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
        //node.setLeft(scores1);
        node.setCenter(time);
        //node.setRight(scores2);
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
        Platform.runLater(() -> {
            AnchorPane pane = new AnchorPane();
            pane.getStylesheets().add(ASSETS + "/main.css");
            pane.setPrefWidth(WIDTH);
            pane.setPrefHeight(HEIGHT);
            pane.setLayoutX( (WIDTH - 600) / 2d);
            pane.setLayoutY( (HEIGHT - 350) / 2d);
            DropShadow shadow = new DropShadow(30, Color.GREY);
            shadow.setOffsetX(-15.0);
            shadow.setOffsetY(15.0);
            pane.setEffect(shadow);
            
            Label label = new Label(win ? "YOU WIN!" : "YOU LOSE!");
            label.setStyle("-fx-font-size:48px");
            label.setLayoutX(200);
            label.setLayoutY(52);


            String bg = win ? ASSETS + "/winner.png" : ASSETS + "/loser.png";
//            final Parent gameOver = AppScreen.GAME_OVER.getParent();
            Rectangle rect = new Rectangle(600, 350);
            rect.setFill(new ImagePattern(new Image(bg)));
            
//            Button playAgain = new Button("Play again");
//            playAgain.setMaxWidth(Double.MAX_VALUE);
//            playAgain.setOnAction(e -> {
//                stage.hide();
//                AppScreen.DASHBOARD.goFrom(GameController.class);
//            });
//            Button scores = new Button("Scores");
//            scores.setMaxWidth(Double.MAX_VALUE);
//            scores.setOnAction(e -> AppScreen.SCORES.goFrom(GameController.class));
            Button exit = new Button("Exit");
            exit.setMaxWidth(Double.MAX_VALUE);
            exit.setOnAction(e -> Platform.exit());
            exit.setPrefWidth(150);
            
//            VBox vBox = new VBox(playAgain, scores, exit);
            VBox vBox = new VBox(exit);
            vBox.setLayoutX(400);
            vBox.setLayoutY(255);
            vBox.setFillWidth(true);
            vBox.setSpacing(20);

            pane.getChildren().addAll(rect, vBox, label);
            
            root.getChildren().add(pane);
        });
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
