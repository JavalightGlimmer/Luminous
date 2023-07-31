package controller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import util.PaneShow;

//aim to show the Panel
public class Controller {

    @FXML
    void cyclicStretching(ActionEvent event) {
        String path = "/fxml/csPane.fxml";
        String title = "循环拉伸";
        PaneShow.initPane(path,title).show();
    }

    @FXML
    void introduction(ActionEvent event) {
        String path = "/fxml/introPane.fxml";
        String title = "使用说明";
        PaneShow.initPane(path, title).show();
    }

    @FXML
    void stressRelaxation(ActionEvent event) {
        String path = "/fxml/srPane.fxml";
        String title = "应力松弛";
        PaneShow.initPane(path, title).show();
    }

    @FXML
    void thankU(ActionEvent event) {
        String path = "/fxml/tyPane.fxml";
        String title = "谢谢侬哇";
        PaneShow.initPane(path, title).show();
    }
    @FXML
    public void platformExitButtonOnMouseClicked() {
        Platform.exit();
    }

}
