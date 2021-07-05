package client.controller;

import client.StartClient;
import client.screen.AppScreen;
import client.utils.Run;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ServerConnectionController {
    
    @FXML
    public TextField hostField;
    
    @FXML
    public TextField portField;

    @FXML
    private void handleButtonAction (ActionEvent unused) {
        Run.safe(this::connect);
    }

    private void connect() {
        String host;
        int port;
        // validate entries
        try {
            host = hostField.getText();
            port = Integer.parseInt(portField.getText());
            if (host.isBlank()) {
                throw new Exception("invalid host");
            }

            if (port < 1 || port > 655535) {
                throw new Exception("invalid port");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        System.out.println("port" + port);
        System.out.println("host " + host);
        // try to connect to server
        connectToServer(host, port);
    }

    private void connectToServer(String host, int port) {
        new Thread(() -> Run.safe(() -> {
            System.out.println("trying to connect...");
            StartClient.getSocketManager().connect(host, port);
            System.out.println("SUCCESS");
            AppScreen.HOME.goFrom(ServerConnectionController.class);
        }))
        .start();
    }
}
