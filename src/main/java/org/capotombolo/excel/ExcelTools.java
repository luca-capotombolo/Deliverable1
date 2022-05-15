package org.capotombolo.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class ExcelTools {

    private final String sheetName;
    private final String cell1;
    private final String cell2;
    private final String cell3;
    private final String cell4;
    private final String cell5;
    private final String cell6;
    private final String cell7;
    private final String cell8;
    private final String cell9;
    private final String cell10;
    private final String cell11;
    private final String cell12;
    private  Workbook wb;

    public ExcelTools(String sheetName, String cell1) {
        this.sheetName = sheetName;
        this.cell1 = cell1;                         //project
        this.cell2 = "RELEASE";
        this.cell3 = "NAME OF THE CLASS";
        this.cell4 = "# REVISION";
        this.cell5 = "LOC ADDED";
        this.cell6 = "AVG LOC ADDED";
        this.cell7 = "MAX LOC ADDED";
        this.cell8 = "SIZE";
        this.cell9 = "CHGSETSIZE";
        this.cell10 = "AVG CHGSETSIZE";
        this.cell11 = "MAX CHGSETSIZE";
        this.cell12 = "BUGGINESS";
    }

    public boolean createTable() {
        this.wb = new HSSFWorkbook();

        try (OutputStream fileOut = Files.newOutputStream(Paths.get("ISW2_" + this.cell1 + ".csv"))) {
            Sheet sheet = wb.createSheet(this.sheetName);
            Row titleRow = sheet.createRow(0);
            Cell cell = titleRow.createCell(0);
            cell.setCellValue(this.cell1);                  //project
            cell = titleRow.createCell(1);
            cell.setCellValue(this.cell2);                  //Release
            cell = titleRow.createCell(2);
            cell.setCellValue(this.cell3);                  //Name of the class
            cell = titleRow.createCell(3);
            cell.setCellValue(this.cell4);                  //# revision
            cell = titleRow.createCell(4);
            cell.setCellValue(this.cell5);                  //LOC ADDED
            cell = titleRow.createCell(5);
            cell.setCellValue(this.cell6);
            cell = titleRow.createCell(6);
            cell.setCellValue(this.cell7);
            cell = titleRow.createCell(7);
            cell.setCellValue(this.cell8);
            cell = titleRow.createCell(8);
            cell.setCellValue(this.cell9);
            cell = titleRow.createCell(9);
            cell.setCellValue(this.cell10);
            cell = titleRow.createCell(10);
            cell.setCellValue(this.cell11);
            cell = titleRow.createCell(11);
            cell.setCellValue(this.cell12);
            sheet.setColumnWidth(0, 4000);
            sheet.setColumnWidth(1, 4000);
            sheet.setColumnWidth(2, 35000);
            sheet.setColumnWidth(3, 4000);
            sheet.setColumnWidth(4, 4000);
            sheet.setColumnWidth(5, 4000);
            sheet.setColumnWidth(6, 4000);
            sheet.setColumnWidth(7, 4000);
            sheet.setColumnWidth(8, 4000);
            sheet.setColumnWidth(9, 5000);
            sheet.setColumnWidth(10, 5000);
            sheet.setColumnWidth(11, 3000);
            this.wb.write(fileOut);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public boolean writeData(HashMap<Release, List<MyFile>> releaseListHashMap, List<Release> releaseList){

        int length = releaseListHashMap.size();
        if(length==0){
            return false;
        }

        try (OutputStream fileOut = Files.newOutputStream(Paths.get("ISW2_" + this.cell1 + ".csv"))) {
            Sheet sheet = this.wb.getSheet(this.sheetName);
            int start = sheet.getLastRowNum();
            Row row;
            Cell cell;
            List<MyFile> myFilesForRelease;
            for (int count = 0; count <= releaseList.size()/2; count++) {
                //get all files of release
                myFilesForRelease = releaseListHashMap.get(releaseList.get(count));
                System.out.println(releaseList.get(count).getRelease());
                System.out.println(releaseList.get(count).getDate());
                for(MyFile myFile: myFilesForRelease){
                    row = sheet.createRow(start + 1);
                    cell = row.createCell(0);
                    cell.setCellValue(this.cell1);
                    cell = row.createCell(1);
                    cell.setCellValue(releaseList.get(count).release);
                    cell = row.createCell(2);
                    cell.setCellValue(myFile.path);
                    cell = row.createCell(3);
                    cell.setCellValue(myFile.numberRevisionRelease);
                    cell = row.createCell(4);
                    cell.setCellValue(myFile.numberLocAddedRelease);
                    cell = row.createCell(5);
                    cell.setCellValue(myFile.averageNumberLocAdded);
                    cell = row.createCell(6);
                    cell.setCellValue(myFile.maxNumberLocAdded);
                    cell = row.createCell(7);
                    cell.setCellValue(myFile.lines);
                    cell = row.createCell(8);
                    cell.setCellValue(myFile.setTouchedFileWithCRelease.size());
                    cell = row.createCell(9);
                    cell.setCellValue(myFile.avgNumberTouchedFile);
                    cell = row.createCell(10);
                    cell.setCellValue(myFile.maxNumberTouchedFile);
                    cell = row.createCell(11);
                    cell.setCellValue(myFile.state.toString());
                    start += 1;
                }
            }
            this.wb.write(fileOut);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}