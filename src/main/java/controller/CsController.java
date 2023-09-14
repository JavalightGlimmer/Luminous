package controller;

import com.sun.org.apache.regexp.internal.RE;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import util.Csv_or_Txt_Hanlder;
import util.PaneShow;
import util.XlsxHandler;

import java.awt.image.Kernel;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CsController {
    @FXML
    private RadioButton bu_N;

    @FXML
    private RadioButton bu_kN;

    @FXML
    private RadioButton model1;

    @FXML
    private RadioButton model2;

    @FXML
    private RadioButton resultBu;

    @FXML
    private TextArea csTextArea;

    private final String wrongWinPath = "/fxml/wrongWindow.fxml";

    private final String timeWinPath = "/fxml/time.fxml";

    private final String processWinPath = "/fxml/process.fxml";

    private final HashMap<String, ArrayList<String>> fileMap = new HashMap<>();

    private String finFileName = "_result_.csv";

    @FXML
    void csHanlde(ActionEvent event) {
        if (!bu_N.isSelected() && !bu_kN.isSelected()) {
            PaneShow.initWrongPane(wrongWinPath, "没有勾选力单位的按钮！", "null").show();
            return;
        }
//      calculate execution time
        Stage tmpStage = PaneShow.initPane(processWinPath, "执行反馈窗口");
        Stage timeStage = PaneShow.initPane(timeWinPath, "正在执行……请耐心");
        String csvFile1 = "";
        String csvFile2 = "";
        try {
            timeStage.show();
            long beginTime = System.currentTimeMillis();
            String url = csTextArea.getText();
            int stressUnit = bu_kN.isSelected() ? 1000 : 1;

            if (model1.isSelected()) {
                handle1(url, tmpStage, csvFile1, csvFile2, stressUnit);
            } else if (model2.isSelected()) {
                handle2(url, tmpStage, csvFile1, csvFile2, stressUnit);
            }
            long endtime = System.currentTimeMillis();
            double execuTime = (endtime - beginTime) / 1000;
            timeStage.setTitle("已处理完毕");
            TextField timeField = (TextField) timeStage.getScene().lookup("#timeField");
            timeField.setText("" + execuTime + "s");
        } catch (Exception e) {
            timeStage.close();
            String msg = e.toString();

            if (msg.indexOf("[") < 0) {
                if (csvFile1 == null || "".equals(csvFile1)) {
                    PaneShow.initWrongPane(wrongWinPath, msg).show();
                } else {
                    PaneShow.initWrongPane(wrongWinPath, msg + "并非在读写过程中出错，在处理文件[" + csvFile1 + "]或文件[" + csvFile2 + "]时出错", csvFile1.substring(0, csvFile1.lastIndexOf("\\"))).show();
                }
            } else {
                PaneShow.initWrongPane(wrongWinPath, msg, msg.substring(msg.indexOf("[") + 1, msg.indexOf("]"))).show();
                e.printStackTrace();
            }
        }
    }

    //Extract file path
    @FXML
    void dragOver(DragEvent event) {
        if (event.getGestureSource() != csTextArea) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    //Extract file path
    @FXML
    void dragDrop(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            File file = dragboard.getFiles().get(0);
            if (file != null) {
                csTextArea.setText(file.getAbsolutePath());
            }
        }
    }

    //Scan and collect all folders related to circular stretching tables
    private HashMap<String, ArrayList<String>> scanningDirs(String url) throws Exception {
        File dir = new File(url);
        File[] dirs = null;
        if (!dir.exists()) {
            throw new Exception("输入的路径有误");
        } else if (dir.isDirectory()) {
            String dirPath = dir.getAbsolutePath();
            if (dirPath.contains("试验运行")) {
                ArrayList<String> fileList = new ArrayList<>();
                String parentDirPath = dir.getParent();
                File[] sonFiles = new File(parentDirPath).listFiles();
                for (File f : sonFiles) {
                    if (f.isDirectory() && f.getName().contains("试验运行")) {
                        fileList.add(f.getName());
                    }
                }
                fileMap.put(parentDirPath, fileList);
            } else {
                dirs = dir.listFiles();
                for (File sonDir : dirs) {
                    if (sonDir.isDirectory()) {
                        String sonDirPath = sonDir.getAbsolutePath();
                        scanningDirs(sonDirPath);
                    }
                }
            }
        }
        return fileMap;
    }

    //Scan and sort CsvFiles of one Diretory
    private HashMap<String, ArrayList<String>> sortCsvFiles(String url) {
        File[] files = new File(url).listFiles();
        ArrayList<String> pair;
        HashMap<String, ArrayList<String>> pairs = new HashMap<>();
        for (File file : files) {
            String fileName = file.getName();
            String key = fileName.substring(fileName.indexOf(')'));
            pair = pairs.containsKey(key) ? pairs.get(key) : new ArrayList<>();
            pair.add(file.getAbsolutePath());
            pairs.put(key, pair);
        }
        return pairs;
    }

    //calculate stress
    private ArrayList<Double> calStress(ArrayList<Double> data, int stressUnit, double width, double thick) {
        for (int i = 0; i < data.size(); i++) {
            data.set(i, data.get(i) * stressUnit / width / thick);
        }
        return data;
    }

    //calculate strain
    private ArrayList<Double> calStrain(ArrayList<Double> data, double gauge) {
        for (int i = 0; i < data.size(); i++) {
            data.set(i, data.get(i) * 100 / gauge);
        }
        return data;
    }

    //模式1处理
    private void handle1(String url, Stage tmpStage, String csvFile1, String csvFile2, int stressUnit) throws Exception {
        HashMap<String, ArrayList<String>> dirMap = scanningDirs(url);
        for (Map.Entry<String, ArrayList<String>> dirEntry : dirMap.entrySet()) {
            String key = dirEntry.getKey();
            ArrayList<String> vals = dirEntry.getValue();
//              generate xlsx tables in the key directory and read files in the val directory
            String propFile = findPropFile(key);
            //get wid&height&gauge
            double width = 0;
            double thick = 0;
            double gauge = 0;
            //read all need arguement from own xlsx
            if (propFile != null) {
                width = XlsxHandler.readCell(propFile, 31, 3);
                thick = XlsxHandler.readCell(propFile, 31, 4);
                gauge = XlsxHandler.readCell(propFile, 56, 1);
            }
            //help to write in file "_result_.xlsx",aim to write dataLists in correct column
            String resultPath = checkAndCreateResultFile(key);

            for (String val : vals) {
                HashMap<String, ArrayList<String>> pairs = sortCsvFiles(key + "\\" + val);
                String separator = "";//reset for different directories
                for (ArrayList<String> pair : pairs.values()) {
                    tmpStage.show();
                    ArrayList<Double> stress1 = null;
                    ArrayList<Double> stress2 = null;
                    ArrayList<Double> strain1 = null;
                    ArrayList<Double> strain2 = null;

                    //to store the calculated _result_, stress for 1st file & strain for 2nd file
                    ArrayList<Double> stress41 = null;
                    ArrayList<Double> strain41 = null;
                    ArrayList<Double> stress42 = null;
                    ArrayList<Double> strain42 = null;
                    ArrayList<Double> finStress = new ArrayList<>();
                    ArrayList<Double> finStrain = new ArrayList<>();

                    csvFile1 = pair.get(0);
                    csvFile2 = pair.size() == 2 ? pair.get(1) : null;
                    if ("".equals(separator)) {
                        separator = csvFile1.contains(".csv") ? "," : "\t";
                    }

                    //let file1 contains("横梁")
                    if (csvFile2 != null) {
                        if (csvFile1.contains("力") && csvFile2.contains("横梁")) {
                            String tmp = csvFile1;
                            csvFile1 = csvFile2;
                            csvFile2 = tmp;
                        }
                        stress2 = Csv_or_Txt_Hanlder.read(csvFile2, 8, 0);
                        strain2 = Csv_or_Txt_Hanlder.read(csvFile2, 8, 1);
                    }
                    stress1 = Csv_or_Txt_Hanlder.read(csvFile1, 8, 1);
                    strain1 = Csv_or_Txt_Hanlder.read(csvFile1, 8, 0);

                    //calculate
                    if (stress1 != null) {
                        //have two csv, connect all data in one new xlsxFile
                        strain41 = calStrain(strain1, gauge);
                        stress41 = calStress(stress1, stressUnit, width, thick);
                        //write data in file1
                        Csv_or_Txt_Hanlder.writeCell(csvFile1, 7, "strain (%)", separator);
                        Csv_or_Txt_Hanlder.writeCell(csvFile1, 7, "stress (MPa)", separator);
                        Csv_or_Txt_Hanlder.write(csvFile1, 8, strain41, separator);
                        Csv_or_Txt_Hanlder.write(csvFile1, 8, stress41, separator);
                        if (stress2 != null) {
                            //write data in file2
                            strain42 = calStrain(strain2, gauge);
                            stress42 = calStress(stress2, stressUnit, width, thick);
                            Csv_or_Txt_Hanlder.writeCell(csvFile2, 7, "strain (%)", separator);
                            Csv_or_Txt_Hanlder.writeCell(csvFile2, 7, "stress (MPa)", separator);
                            Csv_or_Txt_Hanlder.write(csvFile2, 8, strain42, separator);
                            Csv_or_Txt_Hanlder.write(csvFile2, 8, stress42, separator);
                        }
                        if (resultBu.isSelected()) {
                            finStrain.addAll(strain41);
                            if (strain2 != null) finStrain.addAll(strain42);
                            finStress.addAll(stress41);
                            if (stress2 != null) finStress.addAll(stress42);
                            //write united data in new xlsxFile
                            String head = csvFile1.substring(csvFile1.indexOf(')'));
                            Csv_or_Txt_Hanlder.writeCell(resultPath, 0, "," + head, ",");
                            Csv_or_Txt_Hanlder.writeCell(resultPath, 1, "strain", ",");
                            Csv_or_Txt_Hanlder.writeCell(resultPath, 1, "stress", ",");
                            Csv_or_Txt_Hanlder.buildRes(resultPath, 2, finStrain, ",");
                            Csv_or_Txt_Hanlder.buildRes(resultPath, 2, finStress, ",");
                        }
                    }
                    tmpStage.close();
                }
            }
        }
    }

    private void handle2(String url, Stage tmpStage, String csvFile1, String csvFile2, int stressUnit) throws Exception{
        HashMap<String, ArrayList<String>> dirMap = scanningDirs(url);
        for (Map.Entry<String, ArrayList<String>> dirEntry : dirMap.entrySet()) {
            //help to write in file "_result_.xlsx",aim to write dataLists in correct column
            String key = dirEntry.getKey();
            String resultPath = checkAndCreateResultFile(key);

            ArrayList<String> vals = dirEntry.getValue();//含有试验运行文件夹路径的List
//              generate xlsx tables in the key directory and read files in the val directory
            String propFile = findPropFile(key);
            //get wid&height&gauge
            double width = 0;
            double thick = 0;
            double gauge = 0;
            int testSize = vals.size();
            //read all need arguement from own xlsx
            if (propFile != null) {
                width = XlsxHandler.readCell(propFile, 30, 3);
                thick = XlsxHandler.readCell(propFile, 30, 4);
                gauge = XlsxHandler.readCell(propFile, 54 + (testSize - 1) * 6, 1);
            }
            for (String val : vals) {
                File[] files = new File(key + "\\" + val).listFiles();
                String separator = "";//reset for different directories
                tmpStage.show();
                ArrayList<Double> stress1 = null;
                ArrayList<Double> stress2 = null;
                ArrayList<Double> strain1 = null;
                ArrayList<Double> strain2 = null;

                //to store the calculated _result_, stress for 1st file & strain for 2nd file
                ArrayList<Double> stress41 = null;
                ArrayList<Double> strain41 = null;
                ArrayList<Double> stress42 = null;
                ArrayList<Double> strain42 = null;
                ArrayList<Double> finStress = new ArrayList<>();
                ArrayList<Double> finStrain = new ArrayList<>();

                if (files.length == 1){
                    csvFile1 = files[0].getAbsolutePath();
                }else if (files.length == 2){
                    for (File file : files){
                        if (file.getName().contains("横梁")){
                            csvFile1 = file.getAbsolutePath();
                        }
                        if (file.getName().contains("力")){
                            csvFile2 = file.getAbsolutePath();
                        }
                    }
                }

                if ("".equals(separator)) {
                    separator = csvFile1.contains(".csv") ? "," : "\t";
                }
                stress1 = Csv_or_Txt_Hanlder.read(csvFile1, 8, 1);
                strain1 = Csv_or_Txt_Hanlder.read(csvFile1, 8, 0);
                if (csvFile2 != null) {
                    stress2 = Csv_or_Txt_Hanlder.read(csvFile2, 8, 0);
                    strain2 = Csv_or_Txt_Hanlder.read(csvFile2, 8, 1);
                }
                //calculate
                if (stress1 != null) {
                    //have two csv, connect all data in one new xlsxFile
                    strain41 = calStrain(strain1, gauge);
                    stress41 = calStress(stress1, stressUnit, width, thick);
                    //write data in file1
                    Csv_or_Txt_Hanlder.writeCell(csvFile1, 7, "strain (%)", separator);
                    Csv_or_Txt_Hanlder.writeCell(csvFile1, 7, "stress (MPa)", separator);
                    Csv_or_Txt_Hanlder.write(csvFile1, 8, strain41, separator);
                    Csv_or_Txt_Hanlder.write(csvFile1, 8, stress41, separator);
                    if (stress2 != null) {
                        //write data in file2
                        strain42 = calStrain(strain2, gauge);
                        stress42 = calStress(stress2, stressUnit, width, thick);
                        Csv_or_Txt_Hanlder.writeCell(csvFile2, 7, "strain (%)", separator);
                        Csv_or_Txt_Hanlder.writeCell(csvFile2, 7, "stress (MPa)", separator);
                        Csv_or_Txt_Hanlder.write(csvFile2, 8, strain42, separator);
                        Csv_or_Txt_Hanlder.write(csvFile2, 8, stress42, separator);
                    }
                    if (resultBu.isSelected()) {
                        finStrain.addAll(strain41);
                        if (strain2 != null) finStrain.addAll(strain42);
                        finStress.addAll(stress41);
                        if (stress2 != null) finStress.addAll(stress42);
                        //write united data in new xlsxFile
                        int headIdx = val.indexOf("试验运行");
                        String head = val.substring(headIdx + 5, headIdx + 6);
                        Csv_or_Txt_Hanlder.writeCell(resultPath, 0, "," + head, ",");
                        Csv_or_Txt_Hanlder.writeCell(resultPath, 1, "strain", ",");
                        Csv_or_Txt_Hanlder.writeCell(resultPath, 1, "stress", ",");
                        Csv_or_Txt_Hanlder.buildRes(resultPath, 2, finStrain, ",");
                        Csv_or_Txt_Hanlder.buildRes(resultPath, 2, finStress, ",");
                    }
                }
                tmpStage.close();
            }
        }
    }

    private String checkAndCreateResultFile(String key) throws Exception{
        String resultPath = "";
        if (resultBu.isSelected()) {
            resultPath = key + "\\" + finFileName;
            File _result_ = new File(resultPath);
            if (_result_.exists()) {
                _result_.delete();
            }
            _result_.createNewFile();
        }
        return resultPath;
    }

    private String findPropFile(String key) {
        String propFile = null;
        File[] files = new File(key).listFiles();
        for (File file : files) {
            if (file.getName().contains(".xlsx") && !file.getName().contains(finFileName)
                    && !file.getName().contains("~$")) {
                propFile = file.getAbsolutePath();
            }
        }
        return propFile;
    }
}
