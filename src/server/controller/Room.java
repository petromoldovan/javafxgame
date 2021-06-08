package server.controller;

import java.util.ArrayList;

public class Room {
    String id;
    Client c1 = null;
    Client c2 = null;
    ArrayList<Client> participants = new ArrayList<>();

    public Room(String id) {
        this.id = id;
    }

    public boolean addClient(Client c) {
        // check if room is full
        if (participants.size() > 2) {
            return false;
        }

        if (participants.contains(c)) {
            return false;
        }

        participants.add(c);
        if (c1 == null) {
            c1 = c;
        } else {
            c2 = c;
        }

        return true;
    }

    public void broadcast(String message) {
        for (Client c : participants) {
            c.sendDataToClient(message);
        }
    }

    public String getID() {
        return this.id;
    }
}
