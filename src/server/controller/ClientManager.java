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

//    public void broadCastToAllClients() {
//        for (Client c : allConnectedClients) {
//
//        }
//    }
}
