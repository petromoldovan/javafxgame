package server.game;

import network.entity.StateChange;

public interface StateChangeEvent {
    
    void onChange(StateChange change);
    
}
