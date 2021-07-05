package client.game.model;

public class Player {
    String id;
    String username;

    public Player(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getID() {
        return id;
    }
    public String getUsername() {
        return username;
    }
}
