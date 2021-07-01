package server.logic;

import network.entity.Score;
import network.entity.Scores;
import server.StartServer;
import server.database.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static common.constants.Constants.FROG_LIVES;

public class AppLogic {

    private final Database db = StartServer.getDatabase();

    public Scores getScores() {
        List<Score> scoreList = new ArrayList<>();
        Scores result = new Scores(scoreList);
        try {
            scoreList.addAll(db.getScores());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Optional<String> register(final String username, final String password) {
        return run(db -> {
            boolean success = db.register(username, password);
            return success ? Optional.empty() : Optional.of("Registration failed!");
        });
    }

    public Optional<String> login(final String username, final String password) {
        return run(db -> {
            final Integer id = db.login(username, password);
            return id >= 0 ? Optional.empty() : Optional.of("Username or password is wrong!");
        });
    }

    public void saveScores(final String username, final int scores) {
        System.out.printf("Saving scores: %s - %d \n", username, scores);
        run(db -> {
            boolean success = db.saveScore(username, scores);
            String err = String.format("Error saving scores username=[%s] score=[%d]!", username, scores);
            return success ? Optional.empty() : Optional.of(err);
        });
    }

    private static Optional<String> run(Task task) {
        try {
            return task.run(StartServer.getDatabase());
        } catch (Throwable t) {
            t.printStackTrace();
            return Optional.of(t.getMessage());
        }
    }

    public int calculateScores(final int time, final int deaths) {
        return (time * 5) + (FROG_LIVES - deaths) * 50;
    }

    private interface Task {
        Optional<String> run(Database db) throws Exception;
    }
}
