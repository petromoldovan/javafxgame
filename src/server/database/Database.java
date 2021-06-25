package server.database;

import network.entity.Score;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SqlResolve")
public class Database {

    private static final String CREATE_SCORES = 
            "create table if not exists scores (" +
                    "id integer primary key autoincrement, " +
                    "username varchar(200) not null, " +
                    "score integer not null" +
            ")";

    private static final String CREATE_USERS = 
            "create table if not exists users (" +
                    "id integer primary key autoincrement, " +
                    "username varchar(200) not null unique, " +
                    "password varchar(30) not null" +
            ")";
    
    private static final String SELECT_SCORES = "select username, score from scores";
    private static final String ADD_SCORES = "insert into scores (username, score) values ('Joe', 500), ('John', 200), ('Nick', 400)";
    private static final String COUNT_SCORES = "select count(id) from scores;";
    private static final String REGISTER = "insert into users (username, password) values (?,?)";
    private static final String LOGIN = "select id from users where username = ? and password = ?";

    private Connection con;

    public void create() throws SQLException {
        String url = "jdbc:sqlite:./db";
        con = DriverManager.getConnection(url);
        if (con == null) throw new RuntimeException("Error creating database!");
        System.out.println("The driver name is " + con.getMetaData().getDriverName());
        createTables(con);
        final boolean isNew = isNewDatabase();
        if (isNew) {
            addData(con);
        }
        System.out.println("A new database has been created.");
    }

    private boolean isNewDatabase() throws SQLException {
        return Db.query(con, stmt -> {
            try (ResultSet rs = stmt.executeQuery(COUNT_SCORES)) {
                if (rs.next()) {
                    final int count = rs.getInt(1);
                    return count == 0;
                } else {
                    return true;
                }
            }
        });
    }

    private void createTables(final Connection con) throws SQLException {
        Db.execute(con, stmt -> {
            stmt.execute(CREATE_SCORES);
            stmt.execute(CREATE_USERS);
        });
    }

    private void addData(final Connection con) throws SQLException {
        Db.execute(con, stmt -> stmt.execute(ADD_SCORES));
    }
    
    public List<Score> getScores() throws SQLException {
        return Db.query(con, stmt -> {
            List<Score> result = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery(SELECT_SCORES)) {
                while (rs.next()) {
                    int i = 0;
                    final String username = rs.getString(++i);
                    final int score = rs.getInt(++i);
                    result.add(new Score(username, score));
                }
            }
            return result;
        });
    }

    public boolean register(final String username, final String password) throws SQLException {
        return Db.prepQuery(con, REGISTER, ps -> {
            int i = 0;
            ps.setString(++i, username);
            ps.setString(++i, password);
            ps.execute();
            return true;
        });
    }

    public Integer login(final String username, final String password) throws SQLException {
        return Db.prepQuery(con, LOGIN, ps -> {
            int i = 0;
            ps.setString(++i, username);
            ps.setString(++i, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : -1;    
            }
        });
    }

    private static class Db {
        
        static <T> T query(Connection con, DatabaseQuery<T> query) throws SQLException {
            try (Statement stmt = con.createStatement()) {
                return query.query(stmt);
            }
        }
        
        static void execute(Connection con, DatabaseQueryVoid query) throws SQLException {
            query(con, stmt -> {
                query.execute(stmt);
                return null;
            });
        }
        
        static <T> T prepQuery(Connection con, String sql, DatabasePreparedQueryVoid<T> query) throws SQLException {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                return query.execute(ps);
            }
        }
        
    }
    
    private interface DatabaseQuery<T> {
        T query(Statement stmt) throws SQLException;
    }
    
    private interface DatabaseQueryVoid {
        void execute(Statement stmt) throws SQLException;
    }
    
    private interface DatabasePreparedQueryVoid<T> {
        T execute(PreparedStatement ps) throws SQLException;
    }
}
