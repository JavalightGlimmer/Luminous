package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import util.PaneShow;

import java.awt.*;
import java.net.URI;

public class ThankUController {

    @FXML
    private Hyperlink hyperlink;

    @FXML
    private Hyperlink downloadPath1;

    @FXML
    private Hyperlink downloadPath2;

    private final String wrongWinPath = "/fxml/wrongWinSimple.fxml";

    @FXML
    void openWeb(ActionEvent event) {
        open(hyperlink);
    }

    @FXML
    void openGitee(ActionEvent event) {
        open(downloadPath1);
    }

    @FXML
    void openGithub(ActionEvent event) {
        open(downloadPath2);
    }

    private void open(Hyperlink hyperlink){
        String url = hyperlink.getText().replace("\n", "");
        try {
            // 使用java.awt.Desktop类打开默认浏览器
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            PaneShow.initWrongPane(wrongWinPath, "打开网页失败,天不遂我愿呐呜呜").show();
            e.printStackTrace();
        }
    }
}
