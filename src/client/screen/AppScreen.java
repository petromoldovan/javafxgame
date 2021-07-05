package client.screen;

import client.controller.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public enum AppScreen {

    SERVER_CONNECTION("/client/resources/serverConnection.fxml", "Server connection", ServerConnectionController.class),
    HOME("/client/resources/home.fxml", "FROGGER GAME", HomeController.class),
    REGISTRATION("/client/resources/registration.fxml", "Registration", RegistrationController.class),
    LOGIN("/client/resources/login.fxml", "Login", LoginController.class),
    DASHBOARD("/client/resources/dashboard.fxml", "Dashboard", DashboardController.class),
    SCORES("/client/resources/scores.fxml", "Scores", ScoresController.class),
    ;

    private final static Map<AppScreen, Parent> SCREEN_MAP = new EnumMap<>(AppScreen.class);
    private final static Map<AppScreen, Stage> STAGE_MAP = new EnumMap<>(AppScreen.class);
    private final static Navigation NAVIGATION = new Navigation();
    private final static Map<Class<?>, AppScreen> CONTROLLER_MAP = new HashMap<>();
    
    static {
        for (AppScreen appScreen : values()) {
            CONTROLLER_MAP.put(appScreen.clazz, appScreen);
        }
    }
    
    private final String url;
    private final String title;
    private final Class<?> clazz;
    AppScreen(final String _url, final String _title, final Class<?> _clazz) {
        url = _url;
        title = _title;
        clazz = _clazz;
    }

    private Parent getParent() {
        Parent parent = SCREEN_MAP.get(this);
        if (null == parent) {
            parent = load(AppScreen.class.getResource(this.url));
            SCREEN_MAP.put(this, parent);
        }
        return parent;
    }
    
    private Stage getStage() {
        Stage stage = STAGE_MAP.get(this);
        if (null == stage) {
            stage = new Stage();
            stage.setTitle(title);
            Parent parent = getParent();
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            STAGE_MAP.put(this, stage);
        }
        return stage;
    }
    
    public void goFrom(final Class<?> controller) {
        Platform.runLater(() -> {
            final AppScreen screen = CONTROLLER_MAP.get(controller);
            final Stage stage = (screen == null) ? null : screen.getStage();
            NAVIGATION.show(this, stage);
            System.out.println("Go to " + this + " from " + screen);
        });
    }
    
    public static void hide() {
        Platform.runLater(NAVIGATION::hide);
    }
    
    public static void back() {
        Platform.runLater(NAVIGATION::back);
    }
    
    private static Parent load(final URL url) {
        try {
            return FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    private static class Navigation {
        
        private static final LinkedList<Stage> STAGES = new LinkedList<>();

        static {
            STAGES.add(null);
            STAGES.add(null);
        }
        
        public void update(Stage stage) {
            STAGES.addFirst(stage);
            STAGES.removeLast();
        }
        
        public void back() {
            Stage currScreen = STAGES.getFirst();
            Stage prevScreen = STAGES.getLast();
            STAGES.set(0, prevScreen);
            STAGES.set(1, currScreen);
            prevScreen.show();
            currScreen.hide();
        }
        


        public void show(final AppScreen screen, final Stage prevStage) {
            if (prevStage != null) Platform.runLater(prevStage::hide);
            Stage stage = screen.getStage();
            stage.show();
            update(stage);
        }

        public void hide() {
            STAGES.getFirst().hide();
        }

        
    }
    
}
