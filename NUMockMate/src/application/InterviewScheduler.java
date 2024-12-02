package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.*;

public class InterviewScheduler extends Application {

    private TextArea scheduledInterviewsArea;
    private Label messageLabel; // Label for showing feedback messages
    private static final String DB_URL = "jdbc:sqlite:interviews.db";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mock Interview Scheduler");

        // Initialize SQLite database
        initializeDatabase();

        // Heading
        Text heading = new Text("Interview Scheduler");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 36));
//        heading.setUnderline(true);

        // Message Label for Feedback
        messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", 14));
        messageLabel.setWrapText(true);

        // GridPane layout for form
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setAlignment(Pos.CENTER);

        // Scheduled interviews display area
        scheduledInterviewsArea = new TextArea();
        scheduledInterviewsArea.setEditable(false);
        scheduledInterviewsArea.setPrefHeight(300);
        loadScheduledInterviews(); // Load existing interviews at startup

        // Form Fields
        Label interviewerLabel = new Label("Interviewer Name:");
        TextField interviewerField = new TextField();
        interviewerField.setPromptText("Enter interviewer name");

        Label intervieweeLabel = new Label("Interviewee Name:");
        TextField intervieweeField = new TextField();
        intervieweeField.setPromptText("Enter interviewee name");

        Label typeLabel = new Label("Interview Type:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Behavioral", "Technical", "Role-Specific");
        typeComboBox.setPromptText("Select Interview Type");

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker();

        Label timeLabel = new Label("Time:");
        TextField timeField = new TextField();
        timeField.setPromptText("e.g., 10:00 AM");

        Label topicLabel = new Label("Topic:");
        TextField topicField = new TextField();
        topicField.setPromptText("Enter topic of the interview");

        Label roleLabel = new Label("Role:");
        TextField roleField = new TextField();
        roleField.setPromptText("e.g., Peer, Mentor, Professional");

        Label durationLabel = new Label("Duration:");
        TextField durationField = new TextField();
        durationField.setPromptText("e.g., 30 minutes");

        Label notesLabel = new Label("Notes:");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Add any notes or preparation details...");
        notesArea.setPrefHeight(100);

        // Schedule button
        Button scheduleButton = new Button("Schedule Interview");
        scheduleButton.setOnAction(e -> {
            String interviewer = interviewerField.getText();
            String interviewee = intervieweeField.getText();
            String type = typeComboBox.getValue();
            String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "Not selected";
            String time = timeField.getText();
            String topic = topicField.getText();
            String role = roleField.getText();
            String duration = durationField.getText();
            String notes = notesArea.getText();

            if (interviewer.isEmpty() || interviewee.isEmpty() || type == null || time.isEmpty() || topic.isEmpty() || role.isEmpty() || duration.isEmpty()) {
                setMessage("Please fill out all required fields.", "error");
            } else {
                saveInterview(interviewer, interviewee, type, date, time, topic, role, duration, notes);
                loadScheduledInterviews();
                setMessage("Interview scheduled successfully!", "success");
            }
        });

        // Adding elements to GridPane
        gridPane.add(interviewerLabel, 0, 0);
        gridPane.add(interviewerField, 1, 0);
        gridPane.add(intervieweeLabel, 0, 1);
        gridPane.add(intervieweeField, 1, 1);
        gridPane.add(typeLabel, 0, 2);
        gridPane.add(typeComboBox, 1, 2);
        gridPane.add(dateLabel, 0, 3);
        gridPane.add(datePicker, 1, 3);
        gridPane.add(timeLabel, 0, 4);
        gridPane.add(timeField, 1, 4);
        gridPane.add(topicLabel, 0, 5);
        gridPane.add(topicField, 1, 5);
        gridPane.add(roleLabel, 0, 6);
        gridPane.add(roleField, 1, 6);
        gridPane.add(durationLabel, 0, 7);
        gridPane.add(durationField, 1, 7);
        gridPane.add(notesLabel, 0, 8);
        gridPane.add(notesArea, 1, 8);

        // Layout with heading, button, feedback message, and scheduled interviews display
        VBox layout = new VBox(20, heading, gridPane, scheduleButton, messageLabel, new Label("Scheduled Interviews:"), scheduledInterviewsArea);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Automatically set scene to screen size
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(layout, screenBounds.getWidth(), screenBounds.getHeight());

        // Full-screen settings
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true); // Enable full-screen mode
        primaryStage.show();
    }

    private void initializeDatabase() {
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
                    notes TEXT
                );
            """;
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveInterview(String interviewer, String interviewee, String type, String date, String time,
                               String topic, String role, String duration, String notes) {
        String insertSQL = """
            INSERT INTO interviews (interviewer, interviewee, type, date, time, topic, role, duration, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
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
            pstmt.setString(9, notes);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadScheduledInterviews() {
        String selectSQL = "SELECT * FROM interviews";
        StringBuilder content = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                content.append("Interviewer: ").append(rs.getString("interviewer")).append("\n")
                       .append("Interviewee: ").append(rs.getString("interviewee")).append("\n")
                       .append("Type: ").append(rs.getString("type")).append("\n")
                       .append("Date: ").append(rs.getString("date")).append("\n")
                       .append("Time: ").append(rs.getString("time")).append("\n")
                       .append("Topic: ").append(rs.getString("topic")).append("\n")
                       .append("Role: ").append(rs.getString("role")).append("\n")
                       .append("Duration: ").append(rs.getString("duration")).append("\n")
                       .append("Notes: ").append(rs.getString("notes")).append("\n")
                       .append("--------------------------------------------------\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        scheduledInterviewsArea.setText(content.toString());
    }

    private void setMessage(String message, String type) {
        messageLabel.setText(message);
        if (type.equals("success")) {
            messageLabel.setStyle("-fx-text-fill: green;");
        } else {
            messageLabel.setStyle("-fx-text-fill: red;");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
