package controllers;

import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class RegistrationController {
    @FXML
    private Button submitBtn;

    @FXML
    private void handleButtonAction (ActionEvent event) throws Exception {
        navigateToPlayground();
    }

    private void navigateToPlayground() throws Exception {
        Stage stage;
        Parent root;

        stage = (Stage) submitBtn.getScene().getWindow();
        root = FXMLLoader.load(getClass().getResource("../resources/playground.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();


//        ScreenController screenController = ScreenController.getInstance();
//        screenController.activate("playground");
    }
}
