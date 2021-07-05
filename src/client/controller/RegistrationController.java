package client.controller;

import client.StartClient;
import client.logic.Client;
import client.screen.AppScreen;
import client.utils.Run;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import network.entity.LoginResponse;
import network.entity.RegistrationResponse;

public class RegistrationController {

    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField passwordField;

    @FXML
    private Button register;
    
    @FXML
    private Button cancel;

    @FXML
    private void register() {
        disable(true);
        Run.safe(() -> 
                StartClient.getSocketManager().registerUser(
                        usernameField.getText(), 
                        passwordField.getText(), 
                        this::onRegister
                ), 
                this::onError
        );
    }

    private void onRegister(final RegistrationResponse response) {
        switch (response.getCode()) {
            case SUCCESS:
                StartClient.getSocketManager().login(
                        usernameField.getText(),
                        passwordField.getText(),
                        RegistrationController.class,
                        this::onLogin
                );
                break;
            case ERROR:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error");
                    alert.setContentText("Registration failed: " + response.getError());
                    alert.showAndWait();
                });
                break;
        }
        disable(false);
    }

    private void onLogin(final LoginResponse response) {
        Client.getAppLogic().processLoginResponse(response);
    }

    private void onError() {
        Platform.runLater(() -> disable(false));
    }

    private void disable(final boolean disable) {
        usernameField.setDisable(disable);
        passwordField.setDisable(disable);
        register.setDisable(disable);
        cancel.setDisable(disable);
    }

    @FXML
    public void cancel() {
        AppScreen.back();
    }
}
