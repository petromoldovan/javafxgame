package client.controller;

import client.StartClient;
import client.screen.AppScreen;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import network.entity.Score;

public class ScoresController {

    @FXML
    private TableView<Score> table;
    
    @FXML
    public void initialize() {
        table.getColumns().clear();
        table.getColumns().add(getPosColumn());
        table.getColumns().add(getUserColumn());
        table.getColumns().add(getScoreColumn());
        
        StartClient.getSocketManager().showScores(scores -> 
                Platform.runLater(() -> 
                        table.getItems().addAll(scores.getScores())
                )
        );
    }

    private TableColumn<Score, String> getScoreColumn() {
        TableColumn<Score, String> column  = new TableColumn<>("Score");
        column.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getScore())));
        column.setSortable(false);
        return column;
    }
    
    private TableColumn<Score, String> getPosColumn() {
        TableColumn<Score, String> column  = new TableColumn<>("Position");
        column.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getPos())));
        column.setSortable(false);
        return column;
    }

    private TableColumn<Score, String> getUserColumn() {
        TableColumn<Score, String> column  = new TableColumn<>("Username");
        column.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUser()));
        column.setSortable(false);
        return column;
    }

    @FXML
    public void back() {
        AppScreen.back();
    }
}
