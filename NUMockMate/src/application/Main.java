package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		// Create a pane to hold the circle
		Pane pane = new Pane();
		// Create a circle and set its properties
		Circle circle = new Circle();
		
		circle.centerXProperty().bind(pane.widthProperty().divide(2));
		circle.centerYProperty().bind(pane.heightProperty().divide(2));
		
//		circle.setCenterX(100);
//		circle.setCenterY(100);
		circle.setRadius(50);
		circle.setStroke(Color.
		BLACK);
		circle.setFill(Color.
		WHITE);
		pane.getChildren().add(circle);
		// Create a scene and place it in the stage
		Scene scene = new Scene(pane, 200, 200);
		primaryStage.setTitle("Show Circle");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}