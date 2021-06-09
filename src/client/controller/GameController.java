package client.controller;

import client.StartClient;
import client.controller.ScreenController;
import client.controller.SocketManager;
import client.model.Player;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.StartServer;

import java.util.ArrayList;
import java.util.List;

public class GameController {
    private Scene scene;

    public GameController() {
        try {
            Parent rootRegistration = FXMLLoader.load(getClass().getResource("/client/resources/playground.fxml"));
            scene = new Scene(rootRegistration, StartClient.WIDTH, StartClient.HEIGHT);
        } catch (Exception e) {
            // noop
        }
    }

    public void show() {
        StartClient.window.setScene(scene);
    }

    private static AnimationTimer timer;

    private static Pane root;
    private static List<Node> cars = new ArrayList<>();
    private static Node frog;
    private static Text timeLeftContainer = null;
    private static int timeLeft;
    //private int frogSize = 39;
    private static int startPosition = StartClient.HEIGHT - 39;

    private static Player player1;

    private static int x1;
    private static int y1;

    private static Parent createContent() {
        root = new Pane();
        root.setPrefSize(StartClient.WIDTH, StartClient.HEIGHT);

        frog = initFrog();
        root.getChildren().add(frog);

        Node t = initTimeLeftContainer();
        root.getChildren().add(t);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        return root;
    }

    private static void onUpdate() {
        // update positon of all cars
        for (Node car : cars)
            car.setTranslateX(car.getTranslateX() + Math.random() * 10);

        // add a car
        if (Math.random() < 0.08) {
            cars.add(initCar());
        }

        // check for collision
        checkState();
    }

    private static void checkState() {
        for (Node car : cars) {
            if (car.getBoundsInParent().intersects(frog.getBoundsInParent())) {
                // game over. reset frog
                //frog.setTranslateX(0);
                frog.setTranslateX((int)(StartClient.WIDTH/2));
                frog.setTranslateY(startPosition);
            }
        }

        // check if the other end is reached
        if (frog.getTranslateY() <= 10) {
            timer.stop();

            String win = "YOU WIN";
            HBox hBox = new HBox();
            hBox.setTranslateX(300);
            hBox.setTranslateY(250);
            root.getChildren().add(hBox);
            for (int i = 0; i < win.toCharArray().length; i++) {
                char letter = win.charAt(i);
                Text text = new Text(String.valueOf(letter));
                text.setFont(Font.font(50));
                text.setOpacity(0);

                hBox.getChildren().add(text);
                FadeTransition ft = new FadeTransition(Duration.seconds(0.66), text);
                ft.setToValue(1);
                ft.setDelay(Duration.seconds(i * 0.15));
                ft.play();
            }
        }
    }

    private static Node initFrog() {
        Image image = new Image("/client/resources/assets/textures/frog.png");
        Rectangle rect = new Rectangle(38,38, Color.TRANSPARENT);
        rect.setTranslateY(startPosition);
        rect.setTranslateX((int)(StartClient.WIDTH/2));
        ImagePattern imagePattern = new ImagePattern(image);
        rect.setFill(imagePattern);
        return rect;
    }

    private static Node initTimeLeftContainer() {
        HBox hBox = new HBox();
        hBox.setTranslateX(10);
        hBox.setTranslateY(10);
        hBox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        timeLeftContainer = new Text(String.valueOf(timeLeft));

        timeLeftContainer.setFont(Font.font(30));
        //t.setOpacity(0);
        hBox.getChildren().add(timeLeftContainer);
        return hBox;
    }

    private static Node initCar() {
        Rectangle rect = new Rectangle(40,40, Color.RED);
        //14 rows
        rect.setTranslateY((int)(Math.random() * 14) * 40);
        root.getChildren().add(rect);
        return rect;
    }

    public static void setPlayers(Player p1, Player p2) {
        player1 = p1;

        // TODO: add some player specific elements

    }

    public static void setTimeLeft(int v) {
        if (v < 0) {
            return;
        }
        if (timeLeftContainer != null) {
            timeLeftContainer.setText(String.valueOf(v));
        }
    }
    public static void setX1(String v) {
        //x1 = Integer.parseInt(v);
        frog.setTranslateX(Integer.parseInt(v));
    }
    public static void setY1(String v) {
        //y1 = Integer.parseInt(v);
        frog.setTranslateY(Integer.parseInt(v));
    }

    public static void startGame() throws Exception{
        Scene gameScreen = new Scene(createContent(), StartClient.WIDTH, StartClient.HEIGHT);

        // render registration scene
        StartClient.window.setScene(gameScreen);

        StartClient.window.getScene().setOnKeyPressed(event -> {
            double newPosition;
            switch (event.getCode()) {
                case W:
                    // set new position
                    StartClient.socketManager.updateGamePosition((int)frog.getTranslateX(), (int)(frog.getTranslateY() - 40));
                    //frog.setTranslateY(frog.getTranslateY() - 40);
                    break;
                case S:
                    newPosition = frog.getTranslateY() + 40;
                    if (newPosition > StartClient.HEIGHT) return;
                    StartClient.socketManager.updateGamePosition((int)frog.getTranslateX(), (int)newPosition);
                    //frog.setTranslateY(newPosition);
                    break;
                case A:
                    newPosition = frog.getTranslateX() - 40;
                    if (newPosition < 0) return;

                    StartClient.socketManager.updateGamePosition((int)newPosition, (int)frog.getTranslateY());
                    //frog.setTranslateX(newPosition);

                    break;
                case D:
                    newPosition = frog.getTranslateX() + 40;
                    if (newPosition >= StartClient.WIDTH) return;
                    StartClient.socketManager.updateGamePosition((int)newPosition, (int)frog.getTranslateY());
                    //frog.setTranslateX(newPosition);
                    break;
                default:
                    break;
            }
        });
    }
}
