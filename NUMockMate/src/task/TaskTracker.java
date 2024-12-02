package task;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;

import application.HomePage;

public class TaskTracker extends Application {

    private static final String DB_URL = "jdbc:sqlite:numockmate.db";
    private ObservableList<PrepTask> tasks = FXCollections.observableArrayList();
    private ListView<PrepTask> taskListView;
    private TextField companyField;
    private TextField taskNameField;
    private TextArea taskDescriptionArea;
    private DatePicker deadlinePicker;
    private ComboBox<TaskCategory> categoryComboBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Tracker");

        // Initialize SQLite database
        initializeDatabase();
        loadTasksFromDatabase();

        // Full-Screen Setup
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        // Heading
        Text heading = new Text("Task Tracker");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        heading.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #0073e6, #00c4ff);");

        // Back to Home Button
        Button homeButton = new Button("Back to Home");
        homeButton.setStyle("-fx-background-color: #0073e6; -fx-text-fill: white; -fx-font-size: 16px;");
        homeButton.setOnAction(e -> new HomePage().start(primaryStage));

        // Main Layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        VBox leftPanel = createLeftPanel();
        VBox rightPanel = createRightPanel();
        mainLayout.setTop(new VBox(20, heading, homeButton));
        mainLayout.setLeft(leftPanel);
        mainLayout.setCenter(rightPanel);

        Scene scene = new Scene(mainLayout, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true); // Enable full-screen mode
        primaryStage.show();
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(400);

        taskListView = new ListView<>();
        taskListView.setPrefHeight(600);
        taskListView.setItems(tasks);
        taskListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PrepTask task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox details = new VBox(2);
                    Label nameLabel = new Label(task.getName());
                    nameLabel.setStyle("-fx-font-weight: bold;");
                    Label companyLabel = new Label(task.getCompany());
                    Label deadlineLabel = new Label("Due: " + task.getDeadline());
                    details.getChildren().addAll(nameLabel, companyLabel, deadlineLabel);
                    setGraphic(details);
                }
            }
        });

        taskListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateFormWithTask(newValue);
            }
        });

        Label titleLabel = new Label("Tasks");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        leftPanel.getChildren().addAll(titleLabel, taskListView);
        return leftPanel;
    }
    
    private void updateFormWithTask(PrepTask task) {
        if (task != null) {
            companyField.setText(task.getCompany());
            taskNameField.setText(task.getName());
            taskDescriptionArea.setText(task.getDescription());
            deadlinePicker.setValue(task.getDeadline());
            categoryComboBox.setValue(task.getCategory());
        }
    }


    private VBox createRightPanel() {
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));

        Label formTitle = new Label("Add/Edit Task");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        companyField = new TextField();
        companyField.setPromptText("Company Name");

        taskNameField = new TextField();
        taskNameField.setPromptText("Task Name");

        taskDescriptionArea = new TextArea();
        taskDescriptionArea.setPromptText("Task Description");
        taskDescriptionArea.setPrefRowCount(3);

        deadlinePicker = new DatePicker();
        deadlinePicker.setPromptText("Deadline");

        categoryComboBox = new ComboBox<>(FXCollections.observableArrayList(TaskCategory.values()));
        categoryComboBox.setPromptText("Category");

        HBox buttonBox = new HBox(10);
        Button addButton = new Button("Add Task");
        Button updateButton = new Button("Update Task");
        Button deleteButton = new Button("Delete Task");

        addButton.setOnAction(e -> addTaskToDatabase());
        updateButton.setOnAction(e -> updateTaskInDatabase());
        deleteButton.setOnAction(e -> deleteTaskFromDatabase());

        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton);

        rightPanel.getChildren().addAll(
                formTitle,
                new Label("Company:"), companyField,
                new Label("Task Name:"), taskNameField,
                new Label("Description:"), taskDescriptionArea,
                new Label("Deadline:"), deadlinePicker,
                new Label("Category:"), categoryComboBox,
                buttonBox
        );
        return rightPanel;
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS tasks (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    company TEXT,
                    name TEXT,
                    description TEXT,
                    deadline TEXT,
                    category TEXT,
                    completed INTEGER
                );
            """;
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTasksFromDatabase() {
        tasks.clear();
        String selectSQL = "SELECT * FROM tasks";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                tasks.add(new PrepTask(
                        rs.getString("company"),
                        rs.getString("name"),
                        rs.getString("description"),
                        LocalDate.parse(rs.getString("deadline")),
                        TaskCategory.valueOf(rs.getString("category").toUpperCase().replace(" ", "_")),
                        rs.getInt("completed") == 1
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addTaskToDatabase() {
        PrepTask task = createTaskFromForm();
        if (task != null) {
            String insertSQL = """
                INSERT INTO tasks (company, name, description, deadline, category, completed)
                VALUES (?, ?, ?, ?, ?, ?);
            """;
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, task.getCompany());
                pstmt.setString(2, task.getName());
                pstmt.setString(3, task.getDescription());
                pstmt.setString(4, task.getDeadline().toString());
                pstmt.setString(5, task.getCategory().toString());
                pstmt.setInt(6, task.isCompleted() ? 1 : 0);
                pstmt.executeUpdate();
                loadTasksFromDatabase();
                clearForm();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTaskInDatabase() {
        PrepTask selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            PrepTask updatedTask = createTaskFromForm();
            if (updatedTask != null) {
                String updateSQL = """
                    UPDATE tasks SET company = ?, name = ?, description = ?, deadline = ?, category = ?, completed = ?
                    WHERE id = ?;
                """;
                try (Connection conn = DriverManager.getConnection(DB_URL);
                     PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
                    pstmt.setString(1, updatedTask.getCompany());
                    pstmt.setString(2, updatedTask.getName());
                    pstmt.setString(3, updatedTask.getDescription());
                    pstmt.setString(4, updatedTask.getDeadline().toString());
                    pstmt.setString(5, updatedTask.getCategory().toString());
                    pstmt.setInt(6, updatedTask.isCompleted() ? 1 : 0);
                    pstmt.setInt(7, getSelectedTaskId(selectedTask));
                    pstmt.executeUpdate();
                    loadTasksFromDatabase();
                    clearForm();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void deleteTaskFromDatabase() {
        PrepTask selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            String deleteSQL = "DELETE FROM tasks WHERE id = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
                pstmt.setInt(1, getSelectedTaskId(selectedTask));
                pstmt.executeUpdate();
                loadTasksFromDatabase();
                clearForm();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private int getSelectedTaskId(PrepTask task) {
        String selectSQL = "SELECT id FROM tasks WHERE company = ? AND name = ? AND deadline = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, task.getCompany());
            pstmt.setString(2, task.getName());
            pstmt.setString(3, task.getDeadline().toString());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private PrepTask createTaskFromForm() {
        if (companyField.getText().isEmpty() || taskNameField.getText().isEmpty() || deadlinePicker.getValue() == null || categoryComboBox.getValue() == null) {
            showAlert("Please fill in all required fields.");
            return null;
        }
        return new PrepTask(
                companyField.getText(),
                taskNameField.getText(),
                taskDescriptionArea.getText(),
                deadlinePicker.getValue(),
                categoryComboBox.getValue(),
                false
        );
    }

    private void clearForm() {
        companyField.clear();
        taskNameField.clear();
        taskDescriptionArea.clear();
        deadlinePicker.setValue(null);
        categoryComboBox.setValue(null);
        taskListView.getSelectionModel().clearSelection();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
