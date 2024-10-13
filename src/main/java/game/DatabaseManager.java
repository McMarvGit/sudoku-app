package game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    public DatabaseManager() {
        createGameTable();
    }

    private Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection("jdbc:sqlite:./DatabaseSudoku.db");
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void selectAll() {
        String sql = "SELECT * FROM GameStatistics";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String difficulty = rs.getString("difficulty");
                double duration = rs.getDouble("duration");
                int mistakes = rs.getInt("mistakes");
                String result = rs.getString("result");

                System.out.println("ID: " + id + ", Difficulty: " + difficulty
                        + ", Duration: " + duration + ", Mistakes: " + mistakes + ", result: " + result);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getGamesPlayed(String difficulty) {
        String sql;
        if (difficulty.equals("All")) {
            sql = "SELECT COUNT(*) FROM GameStatistics;";
        } else {
            sql = "SELECT COUNT(*) FROM GameStatistics WHERE difficulty = ?;";
        }
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!difficulty.equals("All")) {
                pstmt.setString(1, difficulty);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getWinRate(String difficulty) {
        String sql;
        if (difficulty.equals("All")) {
            sql = "SELECT COUNT(*) * 100.0/(SELECT COUNT(*) FROM GameStatistics) FROM GameStatistics WHERE result = 'Won'";
        } else {
            sql = "SELECT COUNT(*) * 100.0/(SELECT COUNT(*) FROM GameStatistics WHERE difficulty = ?) FROM GameStatistics WHERE result = 'Won' AND difficulty = ?";
        }
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!difficulty.equals("All")) {
                pstmt.setString(1, difficulty);
                pstmt.setString(2, difficulty);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                return ((int) (10 * rs.getDouble(1))) / 10.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public double getAvgMistakes(String difficulty) {
        String sql;
        if (difficulty.equals("All")) {
            sql = "SELECT AVG(mistakes) FROM GameStatistics";
        } else {
            sql = "SELECT AVG(mistakes) FROM GameStatistics WHERE difficulty = ?";
        }
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!difficulty.equals("All")) {
                pstmt.setString(1, difficulty);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                return ((int) (10 * rs.getDouble(1))) / 10.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    public String getAvgDuration(String difficulty) {
        String sql;
        if (difficulty.equals("All")) {
            sql = "SELECT AVG(duration) FROM GameStatistics";
        } else {
            sql = "SELECT AVG(duration) FROM GameStatistics WHERE difficulty = ?";
        }
        try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!difficulty.equals("All")) {
                pstmt.setString(1, difficulty);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                double duration = rs.getDouble(1);
                int hours = (int) (duration / 3600);
                int minutes = (int) ((duration % 3600) / 60);  
                double seconds = duration % 60;
                String formattedTime = String.format("%d:%02d:%04.1f", hours, minutes, seconds);
                return formattedTime;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void addGame(String difficulty, int mistakes, double secondsElapsed,
            boolean solved) {
        String result = solved ? "Won" : "Lost";
        String sql = "INSERT INTO GameStatistics (difficulty,mistakes,duration,result) VALUES (?,?,?,?)";
        try (Connection con = connect();
                PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, difficulty);
            pstmt.setInt(2, mistakes);
            pstmt.setDouble(3, secondsElapsed);
            pstmt.setString(4, result);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createGameTable() {
        String sql = "CREATE TABLE IF NOT EXISTS GameStatistics ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "difficulty TEXT NOT NULL,"
                + "mistakes INTEGER NOT NULL,"
                + "duration REAL NOT NULL,"
                + "result TEXT NOT NULL"
                + ");";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteStats() {
        String sql = "DELETE FROM GameStatistics;";
        try (Connection conn = connect();
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
