package application.InterviewScheduler;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:numockmate.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS interviews (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    interviewer TEXT,
                    interviewee TEXT,
                    type TEXT,
                    date TEXT,
                    time TEXT,
                    topic TEXT,
                    role TEXT,
                    duration TEXT,
                    meeting_link TEXT,
                    notes TEXT
                );
            """;
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveInterview(String interviewer, String interviewee, String type, String date, String time,
                                     String topic, String role, String duration, String meetingLink, String notes) {
        String insertSQL = """
            INSERT INTO interviews (interviewer, interviewee, type, date, time, topic, role, duration, meeting_link, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, interviewer);
            pstmt.setString(2, interviewee);
            pstmt.setString(3, type);
            pstmt.setString(4, date);
            pstmt.setString(5, time);
            pstmt.setString(6, topic);
            pstmt.setString(7, role);
            pstmt.setString(8, duration);
            pstmt.setString(9, meetingLink);
            pstmt.setString(10, notes);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteInterview(int id) {
        String deleteSQL = "DELETE FROM interviews WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ResultSet fetchScheduledInterviews() throws SQLException {
        String selectSQL = "SELECT * FROM interviews";
        Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(selectSQL);
    }
}
