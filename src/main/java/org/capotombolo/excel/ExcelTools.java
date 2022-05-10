package org.capotombolo.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
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

        try (OutputStream fileOut = Files.newOutputStream(Paths.get("ISW2_" + this.cell1 + ".csv"))) {
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

    public boolean writeData(HashMap<Release, List<MyFile>> releaseListHashMap){

        int length = releaseListHashMap.size();
        System.out.println(length);
        if(length==0){
            return false;
        }


        try (OutputStream fileOut = Files.newOutputStream(Paths.get("ISW2_" + this.cell1 + ".csv"))) {
            Sheet sheet = this.wb.getSheet(this.sheetName);
            int start = sheet.getLastRowNum();
            Row row;
            Cell cell;
            List<Release> releaseList = new ArrayList<>();
            List<MyFile> myFilesForRelease;
            releaseListHashMap.forEach((release, myFiles) -> {
                releaseList.add(release);
            });
            for (int count = 0; count < length; count++) {
                //get all files of release
                myFilesForRelease = releaseListHashMap.get(releaseList.get(count));
                for(MyFile myFile: myFilesForRelease){
                    row = sheet.createRow(start + 1);
                    cell = row.createCell(0);
                    cell.setCellValue(this.cell1);
                    cell = row.createCell(1);
                    cell.setCellValue(releaseList.get(count).release);
                    cell = row.createCell(2);
                    cell.setCellValue(myFile.path);
                    cell = row.createCell(3);
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