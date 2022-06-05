package org.capotombolo.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import org.capotombolo.weka.ExcelRowWeka;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ExcelTools {

    private final String sheetName;
    private static final String MACRO = "ISW2_";
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
    private final String cell13;
    private  Workbook wb;
    private final String title;


    public ExcelTools(String sheetName, String cell1, String title) {
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
        this.cell12 = "AUTHORS";
        this.cell13 = "BUGGINESS";
        this.title = title;
    }

    public boolean createTable() {
        this.wb = new HSSFWorkbook();

        try (OutputStream fileOut = Files.newOutputStream(Paths.get(this.title + MACRO + this.cell1 +  ".csv"))) {
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
            cell = titleRow.createCell(12);
            cell.setCellValue(this.cell13);
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
            sheet.setColumnWidth(11, 5000);
            sheet.setColumnWidth(12, 3000);
            this.wb.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean writeSingleRelease(List<MyFile> files){
        if(files.isEmpty()) {
            return false;
        }

        try (OutputStream fileOut = Files.newOutputStream(Paths.get(this.title + MACRO + this.cell1 +  ".csv"))) {
            Sheet sheet = this.wb.getSheet(this.sheetName);
            int start = sheet.getLastRowNum();
            Row row;
            Cell cell;
            for(MyFile file: files){
                row = sheet.createRow(start + 1);
                cell = row.createCell(0);
                cell.setCellValue(this.cell1);
                cell = row.createCell(1);
                cell.setCellValue(this.title);
                cell = row.createCell(2);
                cell.setCellValue(file.path);
                cell = row.createCell(3);
                cell.setCellValue(file.getNumberRevisionRelease());
                cell = row.createCell(4);
                cell.setCellValue(file.getNumberLocAddedRelease());
                cell = row.createCell(5);
                cell.setCellValue(file.getAverageNumberLocAdded());
                cell = row.createCell(6);
                cell.setCellValue(file.getMaxNumberLocAdded());
                cell = row.createCell(7);
                cell.setCellValue(file.getLines());
                cell = row.createCell(8);
                cell.setCellValue(file.getSetTouchedFileWithCRelease().size());
                cell = row.createCell(9);
                cell.setCellValue(file.getAvgNumberTouchedFile());
                cell = row.createCell(10);
                cell.setCellValue(file.getMaxNumberTouchedFile());
                cell = row.createCell(11);
                cell.setCellValue(file.getAuthors().size());
                cell = row.createCell(12);
                cell.setCellValue(file.getState().toString());
                start += 1;
            }
            this.wb.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean writeData(Map<Release, List<MyFile>> releaseListHashMap, List<Release> releaseList){

        int length = releaseListHashMap.size();
        if(length==0){
            return false;
        }

        try (OutputStream fileOut = Files.newOutputStream(Paths.get(MACRO + this.cell1 + ".csv"))) {
            Sheet sheet = this.wb.getSheet(this.sheetName);
            int start = sheet.getLastRowNum();
            Row row;
            Cell cell;
            List<MyFile> myFilesForRelease;
            for (int count = 0; count <= releaseList.size()/2; count++) {
                //get all files of release
                myFilesForRelease = releaseListHashMap.get(releaseList.get(count));
                for(MyFile myFile: myFilesForRelease){
                    row = sheet.createRow(start + 1);
                    cell = row.createCell(0);
                    cell.setCellValue(this.cell1);
                    cell = row.createCell(1);
                    cell.setCellValue(releaseList.get(count).name);
                    cell = row.createCell(2);
                    cell.setCellValue(myFile.path);
                    cell = row.createCell(3);
                    cell.setCellValue(myFile.getNumberRevisionRelease());
                    cell = row.createCell(4);
                    cell.setCellValue(myFile.getNumberLocAddedRelease());
                    cell = row.createCell(5);
                    cell.setCellValue(myFile.getAverageNumberLocAdded());
                    cell = row.createCell(6);
                    cell.setCellValue(myFile.getMaxNumberLocAdded());
                    cell = row.createCell(7);
                    cell.setCellValue(myFile.getLines());
                    cell = row.createCell(8);
                    cell.setCellValue(myFile.getSetTouchedFileWithCRelease().size());
                    cell = row.createCell(9);
                    cell.setCellValue(myFile.getAvgNumberTouchedFile());
                    cell = row.createCell(10);
                    cell.setCellValue(myFile.getMaxNumberTouchedFile());
                    cell = row.createCell(11);
                    cell.setCellValue(myFile.getAuthors().size());
                    cell = row.createCell(12);
                    cell.setCellValue(myFile.getState().toString());
                    start += 1;
                }
            }
            this.wb.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean writeWekaResult(List<ExcelRowWeka> excelRowWekaList, String project){
        boolean ret = createWekaTable(project);
        if(!ret)
            return false;
        ret = writeEvaluation(excelRowWekaList, project);
        return ret;
    }

    private boolean writeEvaluation(List<ExcelRowWeka> excelRowWekaList, String project) {
        try (OutputStream fileOut = Files.newOutputStream(Paths.get(project+"_wekaResults.csv"))) {
            Sheet sheet6 = this.wb.getSheet("weka");
            int startWE = sheet6.getLastRowNum();
            Row rowWE;
            Cell cellWE;
            for(ExcelRowWeka excelRowWeka: excelRowWekaList){
                /*
                rowWE = sheet6.createRow(startWE + 1);
                cellWE = rowWE.createCell(5);
                cellWE.setCellValue(excelRowWeka.getClassifier());
                cellWE = rowWE.createCell(6);
                cellWE.setCellValue(excelRowWeka.getBalancing());
                cellWE = rowWE.createCell(7);
                cellWE.setCellValue(excelRowWeka.getFeatureSelection());
                cellWE = rowWE.createCell(8);
                cellWE.setCellValue(excelRowWeka.getSensitivity());
                cellWE = rowWE.createCell(9);
                cellWE.setCellValue(excelRowWeka.getTp());
                cellWE = rowWE.createCell(10);
                cellWE.setCellValue(excelRowWeka.getFp());
                cellWE = rowWE.createCell(11);
                cellWE.setCellValue(excelRowWeka.getTn());
                cellWE = rowWE.createCell(12);
                cellWE.setCellValue(excelRowWeka.getFn());
                cellWE = rowWE.createCell(13);
                cellWE.setCellValue(excelRowWeka.getPrecision());
                cellWE = rowWE.createCell(14);
                cellWE.setCellValue(excelRowWeka.getRecall());
                cellWE = rowWE.createCell(15);
                cellWE.setCellValue(excelRowWeka.getAuc());
                cellWE = rowWE.createCell(16);
                cellWE.setCellValue(excelRowWeka.getKappa());
                cellWE = rowWE.createCell(0);
                cellWE.setCellValue(project);
                cellWE = rowWE.createCell(1);
                cellWE.setCellValue(excelRowWeka.getNumberTrainingRelease());
                cellWE = rowWE.createCell(2);
                cellWE.setCellValue(excelRowWeka.getPercentTraining());
                cellWE = rowWE.createCell(3);
                cellWE.setCellValue(excelRowWeka.getPercentDefectiveTraining());
                cellWE = rowWE.createCell(4);
                cellWE.setCellValue(excelRowWeka.getPercentDefectiveTesting());
                startWE += 1;*/
            }
            this.wb.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean createWekaTable(String project) {
        this.wb = new HSSFWorkbook();

        try (OutputStream fileOut = Files.newOutputStream(Paths.get(project+"_wekaResults.csv"))) {
            Sheet sheet5 = wb.createSheet("weka");
            Row titleRow = sheet5.createRow(0);
            Cell cellWT = titleRow.createCell(0);
            cellWT.setCellValue("project");                  //project
            cellWT = titleRow.createCell(1);
            cellWT.setCellValue("numberTrainingRelease");                  //Release
            cellWT = titleRow.createCell(2);
            cellWT.setCellValue("percentTraining");                  //Name of the class
            cellWT = titleRow.createCell(3);
            cellWT.setCellValue("percentDefectiveTraining");                  //# revision
            cellWT = titleRow.createCell(4);
            cellWT.setCellValue("percentDefectiveTesting");                  //LOC ADDED
            cellWT = titleRow.createCell(5);
            cellWT.setCellValue("classifier");
            cellWT = titleRow.createCell(6);
            cellWT.setCellValue("balancing");
            cellWT = titleRow.createCell(7);
            cellWT.setCellValue("featureSelection");
            cellWT = titleRow.createCell(8);
            cellWT.setCellValue("sensitivity");
            cellWT = titleRow.createCell(9);
            cellWT.setCellValue("TP");
            cellWT = titleRow.createCell(10);
            cellWT.setCellValue("FP");
            cellWT = titleRow.createCell(11);
            cellWT.setCellValue("TN");
            cellWT = titleRow.createCell(12);
            cellWT.setCellValue("FN");
            cellWT = titleRow.createCell(13);
            cellWT.setCellValue("precision");
            cellWT = titleRow.createCell(14);
            cellWT.setCellValue("recall");
            cellWT = titleRow.createCell(15);
            cellWT.setCellValue("auc");
            cellWT = titleRow.createCell(16);
            cellWT.setCellValue("kappa");
            sheet5.setColumnWidth(0, 5000);
            sheet5.setColumnWidth(1, 6000);
            sheet5.setColumnWidth(2, 5000);
            sheet5.setColumnWidth(3, 7000);
            sheet5.setColumnWidth(4, 7000);
            sheet5.setColumnWidth(5, 5000);
            sheet5.setColumnWidth(6, 5000);
            sheet5.setColumnWidth(7, 5000);
            sheet5.setColumnWidth(8, 5000);
            sheet5.setColumnWidth(9, 5000);
            sheet5.setColumnWidth(10, 5000);
            sheet5.setColumnWidth(11, 5000);
            sheet5.setColumnWidth(12, 5000);
            sheet5.setColumnWidth(13, 5000);
            sheet5.setColumnWidth(14, 5000);
            sheet5.setColumnWidth(15, 5000);
            sheet5.setColumnWidth(16, 5000);
            this.wb.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        ExcelTools excelTools = new ExcelTools(null, null, null);
        excelTools.writeWekaResult(null, "bookkeeper");
    }
}