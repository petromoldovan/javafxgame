package network.entity;

import java.util.List;

public class Scores {
    
    private final List<Score> scores;

    public Scores(final List<Score> scores) {
        this.scores = scores;
    }

    public List<Score> getScores() {
        return scores;
    }
}
