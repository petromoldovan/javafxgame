package network.entity;

import network.entity.enums.FrogMove;

public class FrogMovementRequest {
    
    private final String roomId;
    private final FrogMove move;

    public FrogMovementRequest(final String roomId, final FrogMove move) {
        this.roomId = roomId;
        this.move = move;
    }

    public String getRoomId() {
        return roomId;
    }

    public FrogMove getMove() {
        return move;
    }
}
