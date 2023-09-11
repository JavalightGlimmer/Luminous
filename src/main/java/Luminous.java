import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import util.PaneShow;

import java.io.IOException;

public class Luminous extends Application {
    public static void main(String[] args) {
        PaneShow.flashScreen();
        Application.launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        initial(primaryStage);
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            primaryStage.show();
        }));
        timeline.setCycleCount(1);
        timeline.play();
    }
    //inital the primaryStage
    public void initial(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/app.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Luminous");
        primaryStage.getIcons().add(new Image("/img/小刘鸭角标.jpeg"));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }
}
