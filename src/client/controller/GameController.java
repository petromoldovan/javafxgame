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

import java.io.File;
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

    // FROG objects
    private static Node frog;
    private static Node frog2;
    private static Node controlledFrog;
    private static Node opponentFrog;

    private static Text timeLeftContainer = null;
    private static int timeLeft;

    // SIZES
    private static int frogSize = 38;
    private static int startPosition = StartClient.HEIGHT - 39;
    private static int carHeight = 80;
    private static int carWidth = 80;

    private static Player player1;
    private static Player player2;
    private static boolean isControllingFirstFrog;

    private static int x1;
    private static int y1;

    public static void setPlayer1(Player p) {
        player1 = p;
    }
    public static void setPlayer2(Player p) {
        player2 = p;
    }
    public static void isControllingFirstFrog(boolean b) {
        isControllingFirstFrog = b;
    }

    private static File carIcon1 = new File("src/client/resources/assets/car.png");

    private static Parent createContent() {
        root = new Pane();
        root.setPrefSize(StartClient.WIDTH, StartClient.HEIGHT);

        // textures
        File file = new File("src/client/resources/assets/sand.jpeg");
        root.setStyle("-fx-background-image: url('file:"+file.getAbsolutePath()+"');");

        // init terrain
        initRoads();

        frog = initFrog(false);
        root.getChildren().add(frog);

        if (player2 != null) {
            frog2 = initFrog(true);
            root.getChildren().add(frog2);
        }

        Node clockContainer = initTimeLeftContainer();
        root.getChildren().add(clockContainer);

        Runnable runnable = () -> {
            try {
                timer = new AnimationTimer() {
                    @Override
                    public void handle(long now) {
                        onUpdate();
                    }
                };
                timer.start();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        };
        Thread updateThread = new Thread(runnable);
        updateThread.start();

        return root;
    }

    private static void initRoads() {
        int skipped = 0;
        for (int i = 0; i < 12; i++) {
            if (i % 2 == 0) {
                skipped++;
                continue;
            }

            Rectangle road = new Rectangle(StartClient.WIDTH, carHeight, Color.RED);
            Image image = new Image("/client/resources/assets/road.jpeg");
            road.setFill(new ImagePattern(image));

            // set Y location
            road.setTranslateY(i * carHeight - skipped * (carHeight/2));

            root.getChildren().add(road);
        }
    }

    private static void onUpdate() {
        // update position of all cars
        for (Node car : cars)
            car.setTranslateX(car.getTranslateX() + 5);

        // check for collision
        checkState();
    }

    private static void checkState() {
        for (Node car : cars) {
            if (car.getBoundsInParent().intersects(frog.getBoundsInParent())) {
                // game over. reset frog
                frog.setTranslateX((int)(StartClient.WIDTH/2));
                frog.setTranslateY(startPosition);
            }
            if (frog2 != null) {
                if (car.getBoundsInParent().intersects(frog2.getBoundsInParent())) {
                    // game over. reset frog
                    frog2.setTranslateX((int)(StartClient.WIDTH/2));
                    frog2.setTranslateY(startPosition + frogSize);
                }
            }
        }

        // check if the other end is reached
        checkWinCondition(frog);
        if (frog2 != null) {
            checkWinCondition(frog2);
        }
    }

    private static boolean checkWinCondition(Node fr) {
        if (fr.getTranslateY() <= 10) {
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
            return true;
        }
        return false;
    }

    private static Node initFrog(Boolean isSecond) {
        Image image = new Image("/client/resources/assets/frog.png");
        if (isSecond) {
            image = new Image("/client/resources/assets/frog2.png");
        }
        Rectangle rect = new Rectangle(frogSize, frogSize, Color.TRANSPARENT);
        rect.setTranslateY(startPosition);
        rect.setTranslateX(40);
        rect.setFill(new ImagePattern(image));
        return rect;
    }

    private static Node initTimeLeftContainer() {
        HBox hBox = new HBox();
        hBox.setTranslateX(10);
        hBox.setTranslateY(10);
        hBox.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        timeLeftContainer = new Text(String.valueOf(timeLeft));

        timeLeftContainer.setFont(Font.font(30));
        hBox.getChildren().add(timeLeftContainer);
        return hBox;
    }

    private static void initCar() {
        //14 rows
        int skipped = 0;
        for (int i = 0; i < 12; i++) {
            Rectangle rect = new Rectangle(carWidth, carHeight, Color.RED);
            //place car for every second row
            if (i % 2 == 0) {
                skipped++;
                continue;
            }

            Image image = new Image("/client/resources/assets/car.png");
            ImagePattern imagePattern = new ImagePattern(image);
            rect.setFill(imagePattern);

            // set Y location
            rect.setTranslateY(i * carHeight - skipped * (carHeight/2));

            root.getChildren().add(rect);
            cars.add(rect);
        }

        return;
    }

    public static void setTimeLeft(int v) {
        if (v < 0) {
            return;
        }
        if (timeLeftContainer != null) {
            // create cars only when needed
            if (timeLeft != v) {
                if (v %3 == 0) {
                    initCar();
                }

                timeLeft = v;
            }

            timeLeftContainer.setText(String.valueOf(v));
        }
    }
    public static void setX1(String v) {
        frog.setTranslateX(Integer.parseInt(v));
    }
    public static void setY1(String v) {
        frog.setTranslateY(Integer.parseInt(v));
    }

    public static void setX2(String v) {
        frog2.setTranslateX(Integer.parseInt(v));
    }
    public static void setY2(String v) {
        frog2.setTranslateY(Integer.parseInt(v));
    }

    private static boolean isMultiplayer() {
        return player2 != null && player1 != null;
    }

    private static boolean arePlayersColliding(int newX, int newY) {
        // early exit if there is no other player
        if (opponentFrog == null) {
            return false;
        }

        // create theoretical rect of your next position
        Rectangle nextPosition = new Rectangle(frogSize, frogSize, Color.TRANSPARENT);
        nextPosition.setTranslateY(newY);
        nextPosition.setTranslateX(newX);

        return nextPosition.getBoundsInParent().intersects(opponentFrog.getBoundsInParent());
    }

    public static void startGame() throws Exception{
        Scene gameScreen = new Scene(createContent(), StartClient.WIDTH, StartClient.HEIGHT);

        // render registration scene
        StartClient.window.setScene(gameScreen);

        // set the frog that the client is controlling
        controlledFrog = frog;
        opponentFrog = frog2;
        if (!isControllingFirstFrog) {
            controlledFrog = frog2;
            opponentFrog = frog;
        }

        StartClient.window.getScene().setOnKeyPressed(event -> {
            int newX;
            int newY;
            switch (event.getCode()) {
                case W:
                    newX = (int)controlledFrog.getTranslateX();
                    newY = (int)(controlledFrog.getTranslateY() - 40);

                    // return if next step will collide with opponent
                    if (arePlayersColliding(newX, newY)) {
                        return;
                    }
                    if (newY < 0) return;
                    // set new position
                    StartClient.socketManager.updateGamePosition(newX, newY);
                    break;
                case S:
                    newX = (int)controlledFrog.getTranslateX();
                    newY = (int)(controlledFrog.getTranslateY() + 40);
                    // return if next step will collide with opponent
                    if (arePlayersColliding(newX, newY)) {
                        return;
                    }

                    if (newY > StartClient.HEIGHT) return;
                    StartClient.socketManager.updateGamePosition(newX, newY);
                    break;
                case A:
                    newX = (int)controlledFrog.getTranslateX() - 40;
                    newY = (int)(controlledFrog.getTranslateY());
                    // return if next step will collide with opponent
                    if (arePlayersColliding(newX, newY)) {
                        return;
                    }
                    if (newX < 0) return;
                    StartClient.socketManager.updateGamePosition(newX, newY);
                    break;
                case D:
                    newX = (int)controlledFrog.getTranslateX() + 40;
                    newY = (int)(controlledFrog.getTranslateY());
                    // return if next step will collide with opponent
                    if (arePlayersColliding(newX, newY)) {
                        return;
                    }
                    if (newX >= StartClient.WIDTH) return;
                    StartClient.socketManager.updateGamePosition(newX, newY);
                    break;
                default:
                    break;
            }
        });
    }
}
