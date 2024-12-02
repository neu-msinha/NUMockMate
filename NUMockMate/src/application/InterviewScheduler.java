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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class InterviewScheduler extends Application {

    private TextArea scheduledInterviewsArea;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mock Interview Scheduler");

        // Heading
        Text heading = new Text("Interview Scheduler");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        heading.setUnderline(true);

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
                showAlert(Alert.AlertType.ERROR, "Form Error", "Please fill out all required fields.");
            } else {
                String details = "Mock Interview Scheduled Successfully:\n" +
                        "Interviewer: " + interviewer + "\n" +
                        "Interviewee: " + interviewee + "\n" +
                        "Type: " + type + "\n" +
                        "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Topic: " + topic + "\n" +
                        "Role: " + role + "\n" +
                        "Duration: " + duration + "\n" +
                        "Notes: " + notes + "\n";

                // Write details to a file
                try (BufferedWriter writer = new BufferedWriter(new FileWriter("ScheduledInterviews.txt", true))) {
                    writer.write(details);
                    writer.newLine();
                    writer.write("--------------------------------------------------");
                    writer.newLine();
                } catch (IOException ex) {
                    showAlert(Alert.AlertType.ERROR, "File Error", "Error writing to file: " + ex.getMessage());
                    return;
                }

                // Update the scheduled interviews display
                loadScheduledInterviews();

                // Show success alert
                showAlert(Alert.AlertType.INFORMATION, "Success", details);
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

        // Layout with heading, button, and scheduled interviews display
        VBox layout = new VBox(20, heading, gridPane, scheduleButton, new Label("Scheduled Interviews:"), scheduledInterviewsArea);
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

    private void loadScheduledInterviews() {
        try {
            String content = Files.lines(Paths.get("ScheduledInterviews.txt"))
                    .collect(Collectors.joining("\n"));
            scheduledInterviewsArea.setText(content);
        } catch (IOException e) {
            scheduledInterviewsArea.setText("No scheduled interviews found.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
