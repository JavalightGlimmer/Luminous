package util;

import java.io.*;
import java.util.ArrayList;

public class Csv_or_Txt_Hanlder {

    private static String wrongWinPath = "/fxml/wrongWindow.fxml";

//    Reading data for specific columns in CSV files
    public static ArrayList<Double> read(String path, int beginRow, int aimCol) throws Exception {
        ArrayList<Double> dataCol = new ArrayList<>();
        File csv = new File(path);
        BufferedReader br = null;
        String separator = path.contains(".csv") ? "," : "\t";
        try {
            FileReader fr = new FileReader(csv);
            br = new BufferedReader(fr);
            String line = null;
            int index=0;
            //skip begin rows
            while (index++ < beginRow){
                line = br.readLine();
            }

            while((line=br.readLine())!=null) {
                String[] item = line.split(separator);
                String aimCell = item[aimCol].replace("\"","");
                dataCol.add(Double.valueOf(aimCell));
            }
            fr.close();
        } catch (FileNotFoundException e) {
            throw new Exception("\n[" + path + "]文件未找到：" + e);
        } catch (IOException e) {
            throw new Exception("\n[" + path + "]文件读取或写入出错：" + e);
        } catch (Exception e){
            throw new Exception("\n[" + path + "]文件出错：" + e);
        }

        return dataCol;
    }
//    append column
    public static void write(String path, int beginRow, ArrayList<Double> dataList, String separator) throws Exception {
        String output = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = null;
            int index = 0;
            while ((index++ < beginRow) && (line = reader.readLine()) != null){
                output += line + "\r\n";
            }
            int i = 0;
            while ((line = reader.readLine()) != null) {
                output += (line == null ? "" : line) + separator + dataList.get(i++) + "\r\n";
            }
            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(output);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            throw new Exception("\n[" + path + "]文件未找到：" + e);
        } catch (IOException e) {
            throw new Exception("\n[" + path + "]文件读取或写入出错：" + e);
        } catch (Exception e){
            throw new Exception("\n[" + path + "]文件出错：" + e);
        }
    }

    public static void buildRes(String path, int beginRow,int locationCol, ArrayList<Double> dataList, String separator) throws Exception {
        String output = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = null;
            int index = 0;
            while ((index++ < beginRow) && (line = reader.readLine()) != null){
                output += line + "\r\n";
            }
            String separators = "";
            for(int i = 0; i <= locationCol; i++){
                separators += separator;
            }
            int i = 0;
            while (i < dataList.size()) {
                line = reader.readLine();
                output += (line == null ? ""+separators : line + separator) + dataList.get(i++) + "\r\n";
            }
            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(output);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            throw new Exception("\n[" + path + "]文件未找到：" + e);
        } catch (IOException e) {
            throw new Exception("\n[" + path + "]文件读取或写入出错：" + e);
        } catch (Exception e){
            throw new Exception("\n[" + path + "]文件出错：" + e);
        }
    }
//    append cell
    public static void writeCell(String path, int beginRow, String content, String separator) throws Exception {
        String output = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            int index = 0;
            String line = "";
            while ((line = reader.readLine()) != null && index++ < beginRow){
                output += line + "\r\n";
            }
//          append cell
            output += (line == null ? "" : line) + separator + content + "\r\n";
//          supply other data
            while ((line = reader.readLine()) != null){
                output += line + "\r\n";
            }
            reader.close();
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path));
            writer.write(output);
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            throw new Exception("\n[" + path + "]文件未找到：" + e);
        } catch (IOException e) {
            throw new Exception("\n[" + path + "]文件读取或写入出错：" + e);
        } catch (Exception e){
            throw new Exception("\n[" + path + "]文件出错：" + e);
        }
    }
}
