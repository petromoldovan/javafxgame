package client.controller;

import client.StartClient;
import client.model.Score;
import client.screen.AppScreen;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ScoresController {

    @FXML
    private TableView<Score> table;
    
    @FXML
    public void initialize() {
        table.getColumns().clear();
        table.getColumns().add(getUserColumn());
        table.getColumns().add(getScoreColumn());
        
        StartClient.getSocketManager().showScores(scores -> 
                Platform.runLater(() -> 
                        table.getItems().addAll(scores.getScores())
                )
        );
    }

    private TableColumn<Score, String> getScoreColumn() {
        TableColumn<Score, String> scoreColumn  = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getScore())));
        return scoreColumn;
    }

    private TableColumn<Score, String> getUserColumn() {
        TableColumn<Score, String> userColumn  = new TableColumn<>("Username");
        userColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUser()));
        return userColumn;
    }

    @FXML
    public void back() {
        AppScreen.back();
    }
}
