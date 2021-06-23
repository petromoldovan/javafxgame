package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class BoardController {
    @FXML
    private void handleButtonAction (ActionEvent event) throws Exception {
        next();
    }

    private void next() throws Exception {
        ScreenController screenController = ScreenController.getInstance();
        screenController.activate("playground");
    }
}
