package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ApplicationTracker extends Application {

    // Data Model for Job Application
    public static class JobApplication {
        private String company;
        private String position;
        private String status;
        private String deadline;

        public JobApplication(String company, String position, String status, String deadline) {
            this.company = company;
            this.position = position;
            this.status = status;
            this.deadline = deadline;
        }

        public String getCompany() {
            return company;
        }

        public String getPosition() {
            return position;
        }

        public String getStatus() {
            return status;
        }

        public String getDeadline() {
            return deadline;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Application Tracker");

        // Observable list to hold job applications
        ObservableList<JobApplication> applications = FXCollections.observableArrayList(
                new JobApplication("Google", "Software Engineer", "Applied", "2024-12-01"),
                new JobApplication("Microsoft", "Backend Developer", "Interview Scheduled", "2024-12-10"),
                new JobApplication("Amazon", "Frontend Developer", "Rejected", "N/A")
        );

        // TableView to display applications
        TableView<JobApplication> tableView = new TableView<>();
        tableView.setItems(applications);

        // Columns for the TableView
        TableColumn<JobApplication, String> companyColumn = new TableColumn<>("Company");
        companyColumn.setCellValueFactory(new PropertyValueFactory<>("company"));

        TableColumn<JobApplication, String> positionColumn = new TableColumn<>("Position");
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        TableColumn<JobApplication, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<JobApplication, String> deadlineColumn = new TableColumn<>("Deadline");
        deadlineColumn.setCellValueFactory(new PropertyValueFactory<>("deadline"));

        tableView.getColumns().addAll(companyColumn, positionColumn, statusColumn, deadlineColumn);

        // Form for adding a new application
        TextField companyField = new TextField();
        companyField.setPromptText("Company");
        TextField positionField = new TextField();
        positionField.setPromptText("Position");
        TextField statusField = new TextField();
        statusField.setPromptText("Status");
        TextField deadlineField = new TextField();
        deadlineField.setPromptText("Deadline (YYYY-MM-DD)");

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String company = companyField.getText();
            String position = positionField.getText();
            String status = statusField.getText();
            String deadline = deadlineField.getText();

            if (!company.isEmpty() && !position.isEmpty() && !status.isEmpty()) {
                applications.add(new JobApplication(company, position, status, deadline));
                companyField.clear();
                positionField.clear();
                statusField.clear();
                deadlineField.clear();
            }
        });

        HBox form = new HBox(10, companyField, positionField, statusField, deadlineField, addButton);
        form.setPadding(new Insets(10));

        // Reminder Section
        Label reminderLabel = new Label("Set Follow-up Reminder: ");
        TextField reminderField = new TextField();
        reminderField.setPromptText("E.g., Google - Follow-up on 2024-12-03");
        Button reminderButton = new Button("Set Reminder");
        reminderButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reminder Set");
            alert.setHeaderText("Follow-up Reminder");
            alert.setContentText("Reminder: " + reminderField.getText());
            alert.show();
            reminderField.clear();
        });

        VBox reminderBox = new VBox(10, reminderLabel, reminderField, reminderButton);
        reminderBox.setPadding(new Insets(10));

        // Main Layout
        BorderPane layout = new BorderPane();
        layout.setCenter(tableView);
        layout.setBottom(form);
        layout.setRight(reminderBox);

        Scene scene = new Scene(layout, 800, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
