package application.InterviewScheduler;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class InterviewScheduler extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Interview Scheduler");
        
        DatabaseManager.initializeDatabase();

        // Full-Screen Setup
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        // Heading
        Text heading = InterviewForm.createHeading();

        // Form and Scheduled Interviews Sections
        VBox formPane = InterviewForm.createForm(primaryStage);
        ScrollPane scheduledInterviewsPane = ScheduledInterviews.createScheduledInterviews();

        // Main Layout
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(formPane);
        mainLayout.setCenter(scheduledInterviewsPane);
        mainLayout.setTop(new VBox(heading));

        // Margins
        BorderPane.setMargin(formPane, new Insets(20));
        BorderPane.setMargin(scheduledInterviewsPane, new Insets(20));

        Scene scene = new Scene(mainLayout, screenBounds.getWidth(), screenBounds.getHeight());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
