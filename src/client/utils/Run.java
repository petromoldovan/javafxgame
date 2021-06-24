package client.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.Optional;

public final class Run {
    
    public static Optional<Throwable> safe(UnsafeTask task) {
        try {
            task.execute();
            return Optional.empty();
        } catch (Throwable t) {
            t.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText(t.getMessage());
                alert.showAndWait();
            });
            return Optional.of(t);
        }
    }

}
