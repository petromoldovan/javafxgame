package server.model;

public class Position {
    static String x;
    static String y;

    public Position(String x, String y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return x + ";" + y;
    }
    public void setX(String v) {x = v;}
    public void setY(String v) {y = v;}
}
