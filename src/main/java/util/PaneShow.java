package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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
}
