package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import util.PaneShow;
import util.XlsxHandler;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SrController {

    @FXML
    private Button start_Bu;

    @FXML
    private TextArea textArea;

    private final String wrongWinPath = "/fxml/wrongWindow.fxml";

    private final String timeWinPath = "/fxml/time.fxml";

    private final String processWinPath = "/fxml/process.fxml";

    private final Set<String> xlsList = new HashSet<>();

    @FXML
    void srHandle(ActionEvent event) {
        Stage timeStage = PaneShow.initPane(timeWinPath, "正在执行……请耐心");
        try {
            timeStage.show();
            long beginTime = System.currentTimeMillis();
            String url = textArea.getText();
            if ("".equals(url) || !new File(url).exists()){
                PaneShow.initWrongPane(wrongWinPath, "文件路径有误", "null").show();
                timeStage.close();
                return;
            }
            scanningFiles(new File(url));//add all xls to xlsList
            Stage tmpStage = PaneShow.initPane(processWinPath, "执行反馈窗口");
            for (String path : xlsList) {
                tmpStage.show();
                double baseModel;
                int baseI = 0;//base index to find baseModel
                Map<String, List<Double>> resMap = XlsxHandler.readRangeData(path, 1, 3, new int[]{2, 6});
                List<Double> timeList = resMap.get("time");
                List<Double> modelList = resMap.get("model");
                for (int i = 0; i < timeList.size(); i++) {
                    if (timeList.get(i) == 1) {
                        baseI = i;
                    } else if (timeList.get(i) > 1) {
                        double a = 1 - timeList.get(i - 1);
                        double b = timeList.get(i) - 1;
                        baseI = a > b ? i : i - 1;
                        break;
                    }
                }
                baseModel = modelList.get(baseI);
                List<Double> resList = modelList.stream().map(num -> num / baseModel).collect(Collectors.toList());
                resMap.put("modelRes", resList);
                XlsxHandler.writeRangeData(resMap, 1, path, 3, new int[]{7, 8}, baseI);
                tmpStage.close();
            }
            long endtime = System.currentTimeMillis();
            double execuTime = (endtime - beginTime) / 1000;
            timeStage.setTitle("已处理完毕");
            TextField timeField = (TextField) timeStage.getScene().lookup("#timeField");
            timeField.setText("" + execuTime + "s");
        } catch (Exception e) {
            timeStage.close();
            String msg = e.toString();
            if (msg.indexOf("[") < 0){
                PaneShow.initWrongPane(wrongWinPath, msg + "在SrController流程出现问题", null);
            }else {
                PaneShow.initWrongPane(wrongWinPath, msg, msg.substring(msg.indexOf("[") + 1, msg.indexOf("]"))).show();
            }
        }
    }


//    scanning all the xls files add to a list
    private void scanningFiles(File file) {
        if (file.isFile()) {
            if (file.getName().contains(".xls") && !file.getName().contains(".xlsx") && !file.getName().contains("~$")){
                xlsList.add(file.getAbsolutePath());
            }
        } else {
            File[] files = file.listFiles();
            if (null != files) {
                for (File f : files) {
                    scanningFiles(f);
                }
            }
        }
    }

    //Extract file path
    @FXML
    void dragOver(DragEvent event) {
        if (event.getGestureSource() != textArea){
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    //Extract file path
    @FXML
    void dragDrop(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()){
            File file = dragboard.getFiles().get(0);
            if (file != null){
                textArea.setText(file.getAbsolutePath());
            }
        }
    }
}
