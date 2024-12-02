package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class InterviewQuestions extends Application {

    private QuestionManager questionManager;
    private ListView<Question> questionListView;
    private TextField newQuestionField;
    private ComboBox<String> questionTypeComboBox;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Interview Questions Manager");

        questionManager = new QuestionManager();

        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        questionListView = new ListView<>();
        newQuestionField = new TextField();
        questionTypeComboBox = new ComboBox<>();
        questionTypeComboBox.getItems().addAll("General", "Technical", "Behavioral");
        questionTypeComboBox.setValue("General");

        Button addButton = new Button("Add Question");
        Button saveButton = new Button("Save Questions");
        Button loadButton = new Button("Load Questions");

        addButton.setOnAction(e -> addQuestion());
        saveButton.setOnAction(e -> saveQuestions());
        loadButton.setOnAction(e -> loadQuestions());

        root.getChildren().addAll(questionListView, newQuestionField, questionTypeComboBox, addButton, saveButton, loadButton);

        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addQuestion() {
        String newQuestionText = newQuestionField.getText().trim();
        String questionType = questionTypeComboBox.getValue();
        if (!newQuestionText.isEmpty()) {
            Question question;
            switch (questionType) {
                case "Technical":
                    question = new TechnicalQuestion(newQuestionText);
                    break;
                case "Behavioral":
                    question = new BehavioralQuestion(newQuestionText);
                    break;
                default:
                    question = new GeneralQuestion(newQuestionText);
            }
            questionManager.addQuestion(question);
            updateListView();
            newQuestionField.clear();
        }
    }

    private void updateListView() {
        questionListView.getItems().setAll(questionManager.getQuestions());
    }

    private void saveQuestions() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("interview_questions.txt"))) {
            for (Question question : questionManager.getQuestions()) {
                writer.println(question.getQuestionType() + "|" + question.getQuestionText());
            }
            System.out.println("Questions saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadQuestions() {
        questionManager.clearQuestions();
        try (BufferedReader reader = new BufferedReader(new FileReader("interview_questions.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 2);
                if (parts.length == 2) {
                    Question question;
                    switch (parts[0]) {
                        case "Technical":
                            question = new TechnicalQuestion(parts[1]);
                            break;
                        case "Behavioral":
                            question = new BehavioralQuestion(parts[1]);
                            break;
                        default:
                            question = new GeneralQuestion(parts[1]);
                    }
                    questionManager.addQuestion(question);
                }
            }
            updateListView();
            System.out.println("Questions loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }}


    public static void main(String[] args) {
        launch(args);
    }
}

abstract class Question implements Serializable {
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

class QuestionManager {
    private List<Question> questions;
    private Set<String> uniqueQuestions;
    private Map<String, List<String>> questionResponses;

    public QuestionManager() {
        questions = new ArrayList<>();
        uniqueQuestions = new HashSet<>();
        questionResponses = new HashMap<>();
    }

    public void addQuestion(Question question) {
        if (uniqueQuestions.add(question.getQuestionText())) {
            questions.add(question);
            questionResponses.put(question.getQuestionText(), new ArrayList<>());
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void clearQuestions() {
        questions.clear();
        uniqueQuestions.clear();
        questionResponses.clear();
    }

    public void addResponse(String questionText, String response) {
        questionResponses.get(questionText).add(response);
    }

    public List<String> getResponses(String questionText) {
        return questionResponses.get(questionText);
    }
}