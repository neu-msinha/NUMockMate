package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.time.LocalDate;

public class TaskTracker extends Application {
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
        primaryStage.setTitle("Interview Preparation Task Tracker");

        // Create main layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));

        // Create left panel for task list
        VBox leftPanel = createLeftPanel();
        mainLayout.setLeft(leftPanel);

        // Create right panel for task details and form
        VBox rightPanel = createRightPanel();
        mainLayout.setCenter(rightPanel);

        // Create scene
        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(400);

        // Create task list
        taskListView = new ListView<>();
        taskListView.setPrefHeight(600);
        taskListView.setItems(tasks);
        taskListView.setCellFactory(createTaskCellFactory());

        // Add selection listener
        taskListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateFormWithTask(newValue);
                }
            }
        );

        Label titleLabel = new Label("Interview Preparation Tasks");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        leftPanel.getChildren().addAll(titleLabel, taskListView);
        return leftPanel;
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setPrefWidth(400);

        // Form fields
        Label formTitle = new Label("Add/Edit Task");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

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
        categoryComboBox.setPromptText("Select Category");

        // Buttons
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("Add Task");
        Button updateButton = new Button("Update Task");
        Button deleteButton = new Button("Delete Task");
        buttonBox.getChildren().addAll(addButton, updateButton, deleteButton);

        // Add button event handlers
        addButton.setOnAction(e -> addTask());
        updateButton.setOnAction(e -> updateTask());
        deleteButton.setOnAction(e -> deleteTask());

        rightPanel.getChildren().addAll(
            formTitle,
            new Label("Company:"),
            companyField,
            new Label("Task Name:"),
            taskNameField,
            new Label("Description:"),
            taskDescriptionArea,
            new Label("Deadline:"),
            deadlinePicker,
            new Label("Category:"),
            categoryComboBox,
            buttonBox
        );

        return rightPanel;
    }

    private void addTask() {
        PrepTask task = createTaskFromForm();
        if (task != null) {
            tasks.add(task);
            clearForm();
        }
    }

    private void updateTask() {
        PrepTask selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            PrepTask updatedTask = createTaskFromForm();
            if (updatedTask != null) {
                int index = tasks.indexOf(selectedTask);
                tasks.set(index, updatedTask);
                clearForm();
            }
        }
    }

    private void deleteTask() {
        PrepTask selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            tasks.remove(selectedTask);
            clearForm();
        }
    }

    private PrepTask createTaskFromForm() {
        if (companyField.getText().isEmpty() || taskNameField.getText().isEmpty() || 
            deadlinePicker.getValue() == null || categoryComboBox.getValue() == null) {
            showAlert("Please fill in all required fields");
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

    private void updateFormWithTask(PrepTask task) {
        companyField.setText(task.getCompany());
        taskNameField.setText(task.getName());
        taskDescriptionArea.setText(task.getDescription());
        deadlinePicker.setValue(task.getDeadline());
        categoryComboBox.setValue(task.getCategory());
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

    private Callback<ListView<PrepTask>, ListCell<PrepTask>> createTaskCellFactory() {
        return new Callback<>() {
            @Override
            public ListCell<PrepTask> call(ListView<PrepTask> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(PrepTask task, boolean empty) {
                        super.updateItem(task, empty);
                        if (empty || task == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            HBox container = new HBox(10);
                            CheckBox checkBox = new CheckBox();
                            checkBox.setSelected(task.isCompleted());
                            checkBox.setOnAction(e -> task.setCompleted(checkBox.isSelected()));

                            VBox details = new VBox(2);
                            Label nameLabel = new Label(task.getName());
                            nameLabel.setStyle("-fx-font-weight: bold;");
                            Label companyLabel = new Label(task.getCompany());
                            Label deadlineLabel = new Label("Due: " + task.getDeadline());
                            details.getChildren().addAll(nameLabel, companyLabel, deadlineLabel);

                            container.getChildren().addAll(checkBox, details);
                            setGraphic(container);
                        }
                    }
                };
            }
        };
    }
}

class PrepTask {
    private String company;
    private String name;
    private String description;
    private LocalDate deadline;
    private TaskCategory category;
    private boolean completed;

    public PrepTask(String company, String name, String description, 
                   LocalDate deadline, TaskCategory category, boolean completed) {
        this.company = company;
        this.name = name;
        this.description = description;
        this.deadline = deadline;
        this.category = category;
        this.completed = completed;
    }

    // Getters and setters
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public TaskCategory getCategory() { return category; }
    public void setCategory(TaskCategory category) { this.category = category; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}

enum TaskCategory {
    COMPANY_RESEARCH("Company Research"),
    ROLE_PREPARATION("Role Preparation"),
    TECHNICAL_PRACTICE("Technical Practice"),
    BEHAVIORAL_PRACTICE("Behavioral Practice"),
    DOCUMENTATION("Documentation");

    private final String displayName;

    TaskCategory(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}