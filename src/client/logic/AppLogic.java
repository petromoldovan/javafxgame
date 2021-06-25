package client.logic;

import network.entity.LoginResponse;
import common.constants.ActionTypes;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class AppLogic {

    public void processLoginResponse(final LoginResponse response) {
        if (response.getCode() == ActionTypes.Code.ERROR) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText(response.getError());
                alert.showAndWait();
            });
            System.out.println("Error login " + response.getError());
        }
    }

}
