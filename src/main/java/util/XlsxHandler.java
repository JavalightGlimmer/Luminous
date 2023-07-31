package util;

import jxl.NumberCell;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XlsxHandler {

    //read
    public static double readCell(String path, int row, int col) throws Exception {
        double cellVal = 0;
        try {
            FileInputStream in = new FileInputStream(path);
            Workbook xlsx = new XSSFWorkbook(in);
            Sheet sheet = xlsx.getSheetAt(0);
            Cell aimCell = sheet.getRow(row).getCell(col);
            cellVal = aimCell.getNumericCellValue();
            in.close();
        } catch (Exception e) {
            throw new Exception("\n[" + path + "]文件读取或写入出错：" + e);
        }
        return cellVal;
    }

    //useful for xls which version higher than BIFF5.0
    /*public static List<Double> readRangeRow(String path, int startRow, int col){
        List<Double> list = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(path);
            HSSFWorkbook xls = new HSSFWorkbook(file);
            HSSFSheet sheet = xls.getSheetAt(1);
            Cell cell = null;
            for (int i = startRow;null != (cell = sheet.getRow(i).getCell(col));i++){
                list.add(cell.getNumericCellValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }*/

    //use ancient package jxl
    //this can handle BIFF5 files
    //for convenience， return a map not a list
    public static Map<String, List<Double>> readRangeData(String path, int sheetId, int startNumber, int[] col) throws Exception {
        List<Double> timeList = new ArrayList<>();
        List<Double> modelList = new ArrayList<>();
        Map<String, List<Double>> resMap = new HashMap<>();
        try {
//            System.out.println("==============" + path);
            jxl.Workbook workbook = jxl.Workbook.getWorkbook(new File(path));
            jxl.Sheet sheet = workbook.getSheet(sheetId); // get 2nd sheet
            int rows = sheet.getRows(); // get rows number of this sheet
            //get timeList
            for (int i = startNumber; i < rows; i++){
                jxl.Cell cell = sheet.getCell(col[0], i);
                NumberCell nc = (NumberCell) cell;//if don't Cast to NC, The decimal precision is only 3 digits.
                double value = nc.getValue();
                timeList.add(value);
            }
            //get modelList
            for (int i = startNumber; i < rows; i++) {
                jxl.Cell cell = sheet.getCell(col[1], i);
                NumberCell nc = (NumberCell) cell;//if don't Cast to NC, The decimal precision is only 3 digits.
                double value = nc.getValue();
                modelList.add(value);
            }
            workbook.close();
            resMap.put("time", timeList);
            resMap.put("model", modelList);
        } catch (IOException | BiffException e) {
            throw new Exception("\n[" + path + "]文件读写或者版本出错：" + e);
        } catch (Exception e){
            throw new Exception("\n[" + path + "]文件出错：" + e);
        }
        return resMap;
    }

    //jxl write
    public static void writeRangeData(Map<String, List<Double>> data, int sheetId, String path, int startNum, int[] col, int markCellIdx) throws Exception {
        try {
            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("GBK");
            // 打开已有的文件
            jxl.Workbook workbook = jxl.Workbook.getWorkbook(new File(path), settings);
            // 创建可写入的副本
            WritableWorkbook writableWorkbook = jxl.Workbook.createWorkbook(new File(path), workbook);
            // 获取第一个工作表
            WritableSheet sheet = writableWorkbook.getSheet(sheetId);
            List<Double> timeResList = data.get("time");
            List<Double> modelResList = data.get("modelRes");
            // 写入数据
            for (int i = 0; i < timeResList.size(); i++) {
                Label label = new Label(col[0], i + startNum, String.valueOf(timeResList.get(i)));
                if (i == markCellIdx) {
                    Colour color = Colour.RED;
                    WritableCellFormat cellFormat = new WritableCellFormat();
                    cellFormat.setBackground(color);
                    label.setCellFormat(cellFormat);
                }
                sheet.addCell(label);
            }
            for (int i = 0; i < modelResList.size(); i++) {
                Label label = new Label(col[1], i + startNum, String.valueOf(modelResList.get(i)));
                if (i == markCellIdx) {
                    Colour color = Colour.RED;
                    WritableCellFormat cellFormat = new WritableCellFormat();
                    cellFormat.setBackground(color);
                    label.setCellFormat(cellFormat);
                }
                sheet.addCell(label);
            }
            // 保存并关闭工作簿
            writableWorkbook.write();
            writableWorkbook.close();
            workbook.close();
        } catch (IOException | BiffException | WriteException e) {
            throw new Exception("\n[" + path + "]文件读取或写入出错：" + e);
        } catch (Exception e){
            throw new Exception("\n[" + path + "]文件出错：" + e);
        }
    }

}