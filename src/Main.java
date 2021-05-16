import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;


public class Main extends Application {
    Stage window;
    Scene rootScreen;

    int WIDTH = 600;
    int HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        window = primaryStage;
        window.setTitle("AWESOME GAME");

        Parent rootRegistration = FXMLLoader.load(getClass().getResource("resources/home.fxml"));
        rootScreen = new Scene(rootRegistration, WIDTH, HEIGHT);

        // render registration scene
        window.setScene(rootScreen);
        window.show();
    }
}
