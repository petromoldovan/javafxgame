package client.controller;

import client.StartClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RegistrationController {
    public TextField passwordField;
    public TextField usernameField;

    @FXML
    private Button submitBtn;

    @FXML
    private void handleButtonAction (ActionEvent event) {
        System.out.println("trying to login...");

        // todo: validate input

        StartClient.socketManager.login(usernameField.getText(), passwordField.getText());

//        if ()) {
//            System.out.println("SUCCESS");
//            next();
//        } else {
//            System.out.println("FAILURE");
//        }
//
//        next();
    }

//    private void next() {
//        ScreenController screenController = ScreenController.getInstance();
//        screenController.activate("playgroundScreen");
//    }
}
