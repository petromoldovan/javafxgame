package network.entity;

public class Score {
    
    private int pos;
    private String user;
    private int score;

    public Score(final int pos, final String user, final int score) {
        this.pos = pos;
        this.user = user;
        this.score = score;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public int getScore() {
        return score;
    }

    public void setScore(final int score) {
        this.score = score;
    }

    public int getPos() {
        return pos;
    }
}
