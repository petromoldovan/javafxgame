package server.model;

public class Position {
    String x;
    String y;

    public Position(String x, String y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return this.x + ";" + this.y;
    }
    public void setX(String v) {this.x = v;}
    public void setY(String v) {this.y = v;}
}
