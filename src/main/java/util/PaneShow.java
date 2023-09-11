package util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class PaneShow {
//    initial all kind of windows
    public static Stage initPane(String path, String title){
        AnchorPane pane = null;
        System.out.println(path);
        URL url = PaneShow.class.getResource(path);
        try {
            pane = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setScene(new Scene(pane));
        stage.getIcons().add(new Image("/img/小刘鸭角标.jpeg"));
        stage.setTitle(title);
        return stage;
    }

//  show the wrong Information
    public static Stage initWrongPane(String path, String info, String wrongFilePath){
        AnchorPane pane = null;
        URL url = PaneShow.class.getResource(path);
        try {
            pane = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.getIcons().add(new Image("/img/小刘鸭角标.jpeg"));
//      get pane Node
        TextArea textArea = (TextArea) pane.lookup("#wrongInfo");
        Hyperlink hyperlink = (Hyperlink) pane.lookup("#hyperlink");
        textArea.setText(info);
        hyperlink.setText(wrongFilePath);
        hyperlink.setWrapText(true);
        textArea.setWrapText(true);
        hyperlink.setOnAction(event -> {
            File file = new File(wrongFilePath);
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if (!"".equals(info)){
            textArea.setText(info);
        }
        stage.setScene(new Scene(pane));
        return stage;
    }

    public static Stage initWrongPane(String path, String info){
        AnchorPane pane = null;
        URL url = PaneShow.class.getResource(path);
        try {
            pane = FXMLLoader.load(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.getIcons().add(new Image("/img/小刘鸭角标.jpeg"));
//      get pane Node
        TextArea textArea = (TextArea) pane.lookup("#wrongInfo");
        textArea.setText(info);
        textArea.setWrapText(true);
        if (!"".equals(info)){
            textArea.setText(info);
        }
        stage.setScene(new Scene(pane));
        return stage;
    }


    //坐标记录
    static double x1;
    static double y1;
    static double x_stage;
    static double y_stage;
    public static void setMove(Stage stage, Scene scene){
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent m) {
                //计算
                stage.setX(x_stage + m.getScreenX() - x1);
                stage.setY(y_stage + m.getScreenY() - y1);
            }
        });
        scene.setOnDragEntered(null);
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent m) {

                //按下鼠标后，记录当前鼠标的坐标
                x1 = m.getScreenX();
                y1 = m.getScreenY();
                x_stage = stage.getX();
                y_stage = stage.getY();
            }
        });
    }

    public static void flashScreen() {
        Platform.runLater(() -> {
            Stage ownerStage = new Stage();
            ownerStage.initStyle(StageStyle.UTILITY);
            ownerStage.setOpacity(0);
            ownerStage.setHeight(0);
            ownerStage.setWidth(0);
            ownerStage.show();
            Stage stage = new Stage();
            stage.initOwner(ownerStage);
            ImageView imageView = new ImageView(new Image("img/加油.gif"));
            // 创建一个FadeTransition对象，用于淡入淡出图像
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), imageView);
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
            fadeTransition.setCycleCount(2);
            fadeTransition.setAutoReverse(true);

            // 将ImageView对象添加到一个StackPane对象中
            StackPane stackPane = new StackPane();
            stackPane.getChildren().add(imageView);
            stackPane.setStyle("-fx-background-color:transparent");

            // 创建一个Scene对象，并将StackPane对象设置为场景根节点
            Scene scene = new Scene(stackPane, 400, 400);
            stage.initStyle(StageStyle.TRANSPARENT);
            scene.setFill(Color.TRANSPARENT);
            PaneShow.setMove(stage, scene);
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.show();
        });
    }

}
