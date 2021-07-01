package network.model;

public class Frog {

    private final boolean first;
    private double x;
    private double y;
    private boolean dead;
    
    public Frog(final boolean first, double x, double y) {
        this.first = first;
        this.x = x;
        this.y = y;
    }

    public boolean isFirst() {
        return first;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
    
    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(final boolean dead) {
        this.dead = dead;
    }

    @Override
    public String toString() {
        return "Frog{" +
                "x=" + x +
                ", y=" + y +
                ", dead=" + dead +
                '}';
    }
}
