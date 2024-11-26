package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;

public class InterviewScheduler extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mock Interview Scheduler");

        // GridPane layout for form
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        // Interview type selection
        Label typeLabel = new Label("Interview Type:");
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Behavioral", "Technical", "Role-Specific");
        typeComboBox.setPromptText("Select Interview Type");

        // Date and time selection
        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker();

        Label timeLabel = new Label("Time:");
        TextField timeField = new TextField();
        timeField.setPromptText("e.g., 10:00 AM");

        // Participants
        Label participantLabel = new Label("Participant:");
        TextField participantField = new TextField();
        participantField.setPromptText("Enter participant's name");

        Label roleLabel = new Label("Role:");
        TextField roleField = new TextField();
        roleField.setPromptText("e.g., Peer, Mentor, Professional");

        // Notes
        Label notesLabel = new Label("Notes:");
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Add any notes or preparation details...");
        notesArea.setPrefHeight(100);

        // Schedule button
        Button scheduleButton = new Button("Schedule Interview");
        scheduleButton.setOnAction(e -> {
            String type = typeComboBox.getValue();
            String date = datePicker.getValue() != null ? datePicker.getValue().toString() : "Not selected";
            String time = timeField.getText();
            String participant = participantField.getText();
            String role = roleField.getText();
            String notes = notesArea.getText();

            if (type == null || time.isEmpty() || participant.isEmpty() || role.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Form Error", "Please fill out all required fields.");
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "Mock Interview Scheduled Successfully:\n" +
                        "Type: " + type + "\n" +
                        "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Participant: " + participant + " (" + role + ")\n" +
                        "Notes: " + notes);
            }
        });

        // Adding elements to GridPane
        gridPane.add(typeLabel, 0, 0);
        gridPane.add(typeComboBox, 1, 0);
        gridPane.add(dateLabel, 0, 1);
        gridPane.add(datePicker, 1, 1);
        gridPane.add(timeLabel, 0, 2);
        gridPane.add(timeField, 1, 2);
        gridPane.add(participantLabel, 0, 3);
        gridPane.add(participantField, 1, 3);
        gridPane.add(roleLabel, 0, 4);
        gridPane.add(roleField, 1, 4);
        gridPane.add(notesLabel, 0, 5);
        gridPane.add(notesArea, 1, 5);

        // Layout with button
        VBox layout = new VBox(10, gridPane, scheduleButton);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Scene setup
        Scene scene = new Scene(layout, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sample Title");
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
