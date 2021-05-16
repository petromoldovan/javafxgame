package controllers;

import javafx.scene.Scene;

import java.util.HashMap;
import javafx.scene.layout.Pane;

/**
 * ScreenController is a singleton class to manege screen changes.
 */
public final class ScreenController {
    private static ScreenController INSTANCE;
    private HashMap<String, Pane> screenMap = new HashMap<>();
    private Scene main;

    private ScreenController() {}

    public ScreenController(Scene main) {
        this.main = main;
        INSTANCE = this;
    }

    public static ScreenController getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ScreenController();
        }
        return INSTANCE;
    }

    public void add(String name, Pane pane){
        screenMap.put(name, pane);
    }

    public void remove(String name){
        screenMap.remove(name);
    }

    public void activate(String name){
        main.setRoot( screenMap.get(name) );
    }
}
