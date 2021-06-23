package server.controller;

import java.util.ArrayList;

public class ClientManager {
    ArrayList<Client> allConnectedClients;

    public ClientManager() {
        allConnectedClients = new ArrayList<>();
    }

    public void addClient(Client c) {
        if (!allConnectedClients.contains(c)) {
            allConnectedClients.add(c);
        }
    }

    public Client findOpponent() {
        for (Client c : allConnectedClients) {
            if (c.isLookingForMatch()) {
                return c;
            }
        }

        return null;
    }
}
