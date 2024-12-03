package application.InterviewScheduler;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScheduledInterviews {

    private static VBox scheduledInterviewsContainer = new VBox(10);

    public static ScrollPane createScheduledInterviews() {
        scheduledInterviewsContainer.setPadding(new Insets(20));
        refreshScheduledInterviews();
        ScrollPane scrollPane = new ScrollPane(scheduledInterviewsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-border-color: #0073e6; -fx-border-width: 2px;");
        return scrollPane;
    }

    public static void refreshScheduledInterviews() {
        scheduledInterviewsContainer.getChildren().clear();
        try {
            ResultSet rs = DatabaseManager.fetchScheduledInterviews();
            while (rs.next()) {
                VBox interviewBox = new VBox(5);
                interviewBox.setStyle("-fx-background-color: #f0f8ff; -fx-padding: 10; -fx-border-color: #0073e6;");

                Label interviewer = new Label("Interviewer: " + rs.getString("interviewer"));
                Label interviewee = new Label("Interviewee: " + rs.getString("interviewee"));
                Label type = new Label("Type: " + rs.getString("type"));
                Label date = new Label("Date: " + rs.getString("date"));
                Label time = new Label("Time: " + rs.getString("time"));
                Label topic = new Label("Topic: " + rs.getString("topic"));
                Label role = new Label("Role: " + rs.getString("role"));
                Label duration = new Label("Duration: " + rs.getString("duration"));
                Label meetingLink = new Label("Meeting Link: " + rs.getString("meeting_link"));
                Label notes = new Label("Notes: " + rs.getString("notes"));

                Font labelFont = Font.font("Arial", FontWeight.BOLD, 14);
                for (Label label : new Label[]{interviewer, interviewee, type, date, time, topic, role, duration, meetingLink, notes}) {
                    label.setFont(labelFont);
                }

                Button deleteButton = new Button("Delete");
                deleteButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white;");
                int interviewId = rs.getInt("id");
                deleteButton.setOnAction(e -> {
                    DatabaseManager.deleteInterview(interviewId);
                    refreshScheduledInterviews();
                });

                interviewBox.getChildren().addAll(interviewer, interviewee, type, date, time, topic, role, duration, meetingLink, notes, deleteButton);
                VBox.setMargin(interviewBox, new Insets(10));
                scheduledInterviewsContainer.getChildren().add(interviewBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
