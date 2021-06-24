package client.controller;

import client.StartClient;
import client.screen.AppScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class RegistrationController {

    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField passwordField;

    @FXML
    private void register() {

//        StartClient.socketManager.login(usernameField.getText(), passwordField.getText());

    }
    
    @FXML
    public void cancel() {
        AppScreen.back();
    }
}
