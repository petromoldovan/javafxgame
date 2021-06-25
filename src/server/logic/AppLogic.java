package server.logic;

import network.entity.Score;
import network.entity.Scores;
import server.StartServer;
import server.database.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppLogic {
    
    public Scores getScores() {
        List<Score> scoreList = new ArrayList<>();
        Scores result = new Scores(scoreList);
        try {
            scoreList.addAll(StartServer.getDatabase().getScores());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Optional<String> register(final String username, final String password) {
        return run(db -> {
            final boolean success = db.register(username, password);
            return success ? Optional.empty() : Optional.of("Registration failed!");
        });
    }

    public Optional<String> login(final String username, final String password) {
        return run(db -> {
            final Integer id = db.login(username, password);
            return id >= 0 ? Optional.empty() : Optional.of("Username or password is wrong!");
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
    
    private interface Task {
        Optional<String> run(Database db) throws Exception;
    }
}
