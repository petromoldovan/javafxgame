package client.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.util.Optional;

public final class Run {
    
    public static Optional<Throwable> safe(UnsafeTask task) {
        return safe(task, () -> {});
    }    
    
    public static Optional<Throwable> safe(UnsafeTask task, Runnable onError) {
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
            onError.run();
            return Optional.of(t);
        }
    }

}
