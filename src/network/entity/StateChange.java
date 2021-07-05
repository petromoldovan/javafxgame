package network.entity;


import network.model.Car;
import network.model.Frog;

import java.util.ArrayList;
import java.util.List;

public class StateChange {
    
    private List<Car> cars;
    private List<Car> carRemoval;
    private Frog frog1;
    private Frog frog2;
    private int time = -1;
    private int frog1deaths = -1;
    private int frog2deaths = -1;
    private int frog1scores = -1;
    private int frog2scores = -1;

    public StateChange() {
        cars = new ArrayList<>();
        carRemoval = new ArrayList<>();
    }

    public int getFrog1scores() {
        return frog1scores;
    }

    public void setFrog1scores(final int frog1scores) {
        this.frog1scores = frog1scores;
    }

    public void setFrog2scores(final int frog2scores) {
        this.frog2scores = frog2scores;
    }

    public int getFrog2scores() {
        return frog2scores;
    }

    public Frog getFrog1() {
        return frog1;
    }

    public void setFrog1(final Frog frog1) {
        this.frog1 = frog1;
    }

    public Frog getFrog2() {
        return frog2;
    }

    public void setFrog2(final Frog frog2) {
        this.frog2 = frog2;
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

    public int getTime() {
        return time;
    }
    
    public boolean hasTime() {
        return time >= 0;
    }

    public void setTime(final int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "StateChange{" +
                "cars=" + cars +
                ", frog1=" + frog1 +
                ", frog2=" + frog2 +
                ", time=" + time +
                '}';
    }

    public List<Car> getCarRemoval() {
        return carRemoval;
    }

    public boolean hasFrog1Deaths() {
        return frog1deaths >= 0;
    }
    
    public boolean hasFrog2Deaths() {
        return frog2deaths >= 0;
    }

    public boolean hasFrog1Scores() {
        return frog1scores >= 0;
    }
    
    public boolean hasFrog2Scores() {
        return frog2scores >= 0;
    }
    
    public boolean hasFrog1() {
        return frog1 != null;
    }
    
    public boolean hasFrog2() {
        return frog2 != null;
    }
}
