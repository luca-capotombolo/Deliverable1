package org.capotombolo.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ExcelTools {

    private final String sheetName;
    private final String cell1;
    private final String cell2;
    private final String cell3;
    private final String cell4;
    private  Workbook wb;

    public ExcelTools(String sheetName, String cell1, String cell2, String cell3, String cell4) {
        this.sheetName = sheetName;
        this.cell1 = cell1;
        this.cell2 = cell2;
        this.cell3 = cell3;
        this.cell4 = cell4;
    }

    public boolean createTable() {
        this.wb = new HSSFWorkbook();

        try (OutputStream fileOut = Files.newOutputStream(Paths.get("ISW2.csv"))) {
            Sheet sheet = wb.createSheet(this.sheetName);
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);
            cell.setCellValue(this.cell1);
            cell = titleRow.createCell(1);
            cell.setCellValue(this.cell2);
            cell = titleRow.createCell(2);
            cell.setCellValue(this.cell3);
            cell = titleRow.createCell(3);
            cell.setCellValue(this.cell4);
            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 8000);
            sheet.setColumnWidth(2, 35000);
            sheet.setColumnWidth(3, 3000);
            this.wb.write(fileOut);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean writeData(List<String> filenames, String release){

        int length = filenames.size();
        System.out.println(length);
        if(length==0){
            return false;
        }


        try (OutputStream fileOut = Files.newOutputStream(Paths.get("ISW2.csv"))) {
            Sheet sheet = this.wb.getSheet(this.sheetName);
            int start = sheet.getLastRowNum();
            Row row;
            Cell cell;
            for (int count = 0; count < length; count++) {
                row = sheet.createRow(start + 1);
                cell = row.createCell(0);
                cell.setCellValue(this.cell1);
                cell = row.createCell(1);
                cell.setCellValue(release);
                cell = row.createCell(2);
                cell.setCellValue(filenames.get(count));
                System.out.println(filenames.get(count));
                start += 1;
            }
            this.wb.write(fileOut);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }


    public static void main(String[] args) {
        ExcelTools excelTools = new ExcelTools("ISW2", "ZOOKEEPER", "Release", "Name of the class", "Bugginess");
        boolean ret = excelTools.createTable();

        if (!ret) {
            System.out.println("Si è verificato un errore nella creazione della tabella...");
        }else{
            System.out.println("La tabella è stata creata con successo...");
        }

        //FoundAllJavaFiles foundAllJavaFiles = new FoundAllJavaFiles("C:\\Users\\lucac\\maven\\Zookkeeper\\zookeeper");
        //System.out.println("File contenuti nella cartella: " + foundAllJavaFiles.localPath);
        //List<String> filenames = foundAllJavaFiles.foundAllFiles();

        //Costruisco la tabella excel
        //ret = excelTools.writeData(filenames);

        //if (ret){
        //    System.out.println("modifica file excel avvenuta con successo...");
        //}
    }

}