package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.*;
import java.util.*;

public class InterviewQuestions extends Application {

    private static final String DB_URL = "jdbc:sqlite:numockmate.db";
    private ListView<Question> questionListView;
    private TextField newQuestionField;
    private ComboBox<String> questionTypeComboBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Interview Questions Manager");

        // Initialize SQLite database
        initializeDatabase();

        // Full-Screen Setup
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        // Heading
        Text heading = new Text("Interview Questions");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        heading.setStyle("-fx-fill: linear-gradient(from 0% 0% to 100% 100%, #0073e6, #00c4ff);");

        questionListView = new ListView<>();
        loadQuestionsFromDatabase(); // Load questions from database

        newQuestionField = new TextField();
        newQuestionField.setPromptText("Enter a new question...");
        questionTypeComboBox = new ComboBox<>();
        questionTypeComboBox.getItems().addAll("General", "Technical", "Behavioral");
        questionTypeComboBox.setValue("General");

        // Buttons
        Button addButton = new Button("Add Question");
        Button homeButton = new Button("Back to Home");

        // Style Buttons
        Button[] buttons = {addButton, homeButton};
        for (Button button : buttons) {
            button.setStyle("-fx-background-color: #0073e6; -fx-text-fill: white; -fx-font-size: 14px;");
            button.setMinHeight(40);
        }

        // Button Actions
        addButton.setOnAction(e -> addQuestionToDatabase());
        homeButton.setOnAction(e -> new HomePage().start(primaryStage));

        // Layout for buttons
        HBox buttonBox = new HBox(10, addButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Layout for adding new question
        HBox addQuestionBox = new HBox(10, newQuestionField, questionTypeComboBox, addButton);
        addQuestionBox.setAlignment(Pos.CENTER);
        addQuestionBox.setPadding(new Insets(10));

        // Main Layout
        VBox centerLayout = new VBox(20, questionListView, addQuestionBox, buttonBox);
        centerLayout.setPadding(new Insets(20));
        centerLayout.setAlignment(Pos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setTop(new VBox(20, heading, homeButton));
        layout.setCenter(centerLayout);

        // Scene Setup
        Scene scene = new Scene(layout, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true); // Enable full-screen mode
        primaryStage.show();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    question_text TEXT,
                    question_type TEXT
                );
            """;
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addQuestionToDatabase() {
        String newQuestionText = newQuestionField.getText().trim();
        String questionType = questionTypeComboBox.getValue();
        if (!newQuestionText.isEmpty()) {
            String insertSQL = "INSERT INTO questions (question_text, question_type) VALUES (?, ?)";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setString(1, newQuestionText);
                pstmt.setString(2, questionType);
                pstmt.executeUpdate();
                loadQuestionsFromDatabase(); // Reload questions
                newQuestionField.clear();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Question added successfully!", ButtonType.OK);
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a question.", ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void loadQuestionsFromDatabase() {
        questionListView.getItems().clear();
        String selectSQL = "SELECT question_text, question_type FROM questions";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            while (rs.next()) {
                String questionText = rs.getString("question_text");
                String questionType = rs.getString("question_type");
                Question question;
                switch (questionType) {
                    case "Technical":
                        question = new TechnicalQuestion(questionText);
                        break;
                    case "Behavioral":
                        question = new BehavioralQuestion(questionText);
                        break;
                    default:
                        question = new GeneralQuestion(questionText);
                }
                questionListView.getItems().add(question);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

abstract class Question {
    protected String questionText;

    public Question(String questionText) {
        this.questionText = questionText;
    }

    public abstract String getQuestionType();

    public String getQuestionText() {
        return questionText;
    }

    @Override
    public String toString() {
        return getQuestionType() + ": " + questionText;
    }
}

class GeneralQuestion extends Question {
    public GeneralQuestion(String questionText) {
        super(questionText);
    }

    @Override
    public String getQuestionType() {
        return "General";
    }
}

class TechnicalQuestion extends Question {
    public TechnicalQuestion(String questionText) {
        super(questionText);
    }

    @Override
    public String getQuestionType() {
        return "Technical";
    }
}

class BehavioralQuestion extends Question {
    public BehavioralQuestion(String questionText) {
        super(questionText);
    }

    @Override
    public String getQuestionType() {
        return "Behavioral";
    }
}
