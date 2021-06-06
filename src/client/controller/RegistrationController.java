package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class RegistrationController {
    @FXML
    private Button submitBtn;

    @FXML
    private void handleButtonAction (ActionEvent event) throws Exception {
        next();
    }

    private void next() throws Exception {
        ScreenController screenController = ScreenController.getInstance();
        screenController.activate("playground");
    }
}
