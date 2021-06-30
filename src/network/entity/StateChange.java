package network.entity;


import network.model.Car;
import network.model.Frog;

import java.util.ArrayList;
import java.util.List;

public class StateChange {
    
    private final List<Car> cars;
    private final List<Frog> frogs;
    private int time = -1;
    private int frog1deaths = -1;
    private int frog2deaths = -1;

    public StateChange() {
        this.cars = new ArrayList<>();
        this.frogs = new ArrayList<>();
    }

    public int getFrog1deaths() {
        return frog1deaths;
    }

    public void setFrog1deaths(final int frog1deaths) {
        this.frog1deaths = frog1deaths;
    }

    public int getFrog2deaths() {
        return frog2deaths;
    }

    public void setFrog2deaths(final int frog2deaths) {
        this.frog2deaths = frog2deaths;
    }

    public List<Car> getCars() {
        return cars;
    }

    public List<Frog> getFrogs() {
        return frogs;
    }

    public int getTime() {
        return time;
    }
    
    public boolean hasTime() {
        return time > 0;
    }

    public void setTime(final int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "StateChange{" +
                "cars=" + cars +
                ", frogs=" + frogs +
                ", time=" + time +
                '}';
    }
}
