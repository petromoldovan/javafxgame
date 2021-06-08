package server.controller;

import common.constants.ActionTypes;
import javafx.scene.layout.Pane;
import server.model.Position;
import java.util.Timer;
import java.util.TimerTask;

import java.util.ArrayList;

public class Room {
    String id;
    Client c1 = null;
    Client c2 = null;

    // game related data
    Position c1Position = new Position(1, 1);
    Position c2Position = new Position(40, 1);
    public int remainingGameTime = 60;
    public Timer gameTimer;

    int currTick = 0;
    int oneTick = 100;

    ArrayList<Client> participants = new ArrayList<>();

    public Room(String id) {
        this.id = id;
        this.gameTimer = new Timer();
    }

    // trigger game start with constant updates
    public void startGame() {
        // broadcast game info
        this.gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // reduce remaining time
                if (currTick >= 1000) {
                    remainingGameTime --;
                    currTick = 0;
                } else {
                    currTick += oneTick;
                }

                // send game data to all participants in the room
                broadcast(ActionTypes.ActionType.CURRENT_GAME_DATA_RESPONSE.name() + ";" + getData());

                if (remainingGameTime < 0) {
                    gameTimer.cancel();
                    gameTimer.purge();
                }
            }
        }, 0, oneTick);

        // TODO: close room
        // TODO: send end game
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

    public String getData() {
        String data = "";

        // get player info
        data += getClientMetaInformation();

        // get timer
        data += ";" + remainingGameTime;

        // get position info
        data += getPositionInformation();

        return data;
    }

    private String getClientMetaInformation() {
        String data = "";
        if (c1 != null) {
            data += c1.getClientData();
        } else {
            data += c1.getEmptyClientData();
        }

        data += ";";
        if (c2 != null) {
            data += c2.getClientData();
        } else {
            data += c1.getEmptyClientData();
        }
        return data;
    }

    private String getPositionInformation() {
        String data = ";";
        data += c1Position.toString();
        if (c2 != null) {
            data += ";" + c2Position.toString();
        }

        return data;
    }
}
