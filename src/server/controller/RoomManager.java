package server.controller;

import java.util.ArrayList;
import java.util.UUID;

public class RoomManager {
    ArrayList<Room> allRooms;

    public RoomManager() {
        allRooms = new ArrayList<>();
    }

    public Room newRoom() {
        Room room = new Room(UUID.randomUUID().toString());
        allRooms.add(room);
        return room;
    }

    public Room findRoomByID(String id) {
        for (Room room : allRooms) {
            if (room.getID().equals(id)) {
                return room;
            }
        }
        return null;
    }
}
