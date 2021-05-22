import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import controllers.ScreenController;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.util.Duration;


public class Main extends Application {
    Stage window;
    Scene rootScreen;

    int WIDTH = 800;
    int HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    private AnimationTimer timer;

    private Pane root;
    private List<Node> cars = new ArrayList<>();
    private Node frog;
    //private int frogSize = 39;
    private int startPosition = HEIGHT - 39;


    private Parent createContent() {
        root = new Pane();
        root.setPrefSize(WIDTH, HEIGHT);

        frog = initFrog();
        root.getChildren().add(frog);

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                onUpdate();
            }
        };
        timer.start();

        return root;
    }

    private void onUpdate() {
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

    private void checkState() {
        for (Node car : cars) {
            if (car.getBoundsInParent().intersects(frog.getBoundsInParent())) {
                // game over. reset frog
                //frog.setTranslateX(0);
                frog.setTranslateX((int)(WIDTH/2));
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

    private Node initFrog() {
        Image image = new Image("./resources/assets/textures/frog.png");
        Rectangle rect = new Rectangle(38,38, Color.TRANSPARENT);
        rect.setTranslateY(startPosition);
        rect.setTranslateX((int)(WIDTH/2));
        ImagePattern imagePattern = new ImagePattern(image);
        rect.setFill(imagePattern);
        return rect;
    }

    private Node initCar() {
        Rectangle rect = new Rectangle(40,40, Color.RED);
                                                //14 rows
        rect.setTranslateY((int)(Math.random() * 14) * 40);
        root.getChildren().add(rect);
        return rect;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("AWESOME GAME");

        Parent rootRegistration = FXMLLoader.load(getClass().getResource("resources/home.fxml"));
        rootScreen = new Scene(rootRegistration, WIDTH, HEIGHT);

        ScreenController screenController = new ScreenController(rootScreen);
        screenController.add("home", FXMLLoader.load(getClass().getResource( "./resources/home.fxml" )));
        screenController.add("playground", FXMLLoader.load(getClass().getResource( "./resources/playground.fxml" )));
        screenController.add("board", FXMLLoader.load(getClass().getResource( "./resources/board.fxml" )));
        screenController.activate("home");


        //Scene gameScreen = new Scene(createContent(), WIDTH, HEIGHT);

        // render registration scene
        window.setScene(rootScreen);

//        window.getScene().setOnKeyPressed(event -> {
//            double newPosition;
//            switch (event.getCode()) {
//                case W:
//                    frog.setTranslateY(frog.getTranslateY() - 40);
//                    break;
//                case S:
//                    newPosition = frog.getTranslateY() + 40;
//                    if (newPosition > HEIGHT) return;
//                    frog.setTranslateY(newPosition);
//                    break;
//                case A:
//                    newPosition = frog.getTranslateX() - 40;
//                    if (newPosition < 0) return;
//                    frog.setTranslateX(newPosition);
//
//                    break;
//                case D:
//                    newPosition = frog.getTranslateX() + 40;
//                    if (newPosition >= WIDTH) return;
//                    frog.setTranslateX(newPosition);
//                    break;
//                default:
//                    break;
//            }
//        });

        window.show();
    }
}
