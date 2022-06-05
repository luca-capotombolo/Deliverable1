package org.capotombolo.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.capotombolo.weka.ExcelRowWeka;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ExcelWeka {
    private Workbook wb;

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
                startWE += 1;
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
            sheet5.setColumnWidth(0, 5000);
            cellWT = titleRow.createCell(1);
            cellWT.setCellValue("numberTrainingRelease");                  //Release
            sheet5.setColumnWidth(1, 6000);
            cellWT = titleRow.createCell(2);
            cellWT.setCellValue("percentTraining");                  //Name of the class
            sheet5.setColumnWidth(2, 5000);
            cellWT = titleRow.createCell(3);
            cellWT.setCellValue("percentDefectiveTraining");                  //# revision
            sheet5.setColumnWidth(3, 7000);
            cellWT = titleRow.createCell(4);
            cellWT.setCellValue("percentDefectiveTesting");                  //LOC ADDED
            sheet5.setColumnWidth(4, 7000);
            cellWT = titleRow.createCell(5);
            cellWT.setCellValue("classifier");
            sheet5.setColumnWidth(5, 5000);
            cellWT = titleRow.createCell(6);
            cellWT.setCellValue("balancing");
            sheet5.setColumnWidth(6, 5000);
            cellWT = titleRow.createCell(7);
            cellWT.setCellValue("featureSelection");
            sheet5.setColumnWidth(7, 5000);
            cellWT = titleRow.createCell(8);
            cellWT.setCellValue("sensitivity");
            sheet5.setColumnWidth(8, 5000);
            cellWT = titleRow.createCell(9);
            cellWT.setCellValue("TP");
            sheet5.setColumnWidth(9, 5000);
            cellWT = titleRow.createCell(10);
            cellWT.setCellValue("FP");
            sheet5.setColumnWidth(10, 5000);
            cellWT = titleRow.createCell(11);
            cellWT.setCellValue("TN");
            sheet5.setColumnWidth(11, 5000);
            cellWT = titleRow.createCell(12);
            cellWT.setCellValue("FN");
            sheet5.setColumnWidth(12, 5000);
            cellWT = titleRow.createCell(13);
            cellWT.setCellValue("precision");
            sheet5.setColumnWidth(13, 5000);
            cellWT = titleRow.createCell(14);
            cellWT.setCellValue("recall");
            sheet5.setColumnWidth(14, 5000);
            cellWT = titleRow.createCell(15);
            cellWT.setCellValue("auc");
            sheet5.setColumnWidth(15, 5000);
            cellWT = titleRow.createCell(16);
            cellWT.setCellValue("kappa");
            sheet5.setColumnWidth(16, 5000);
            this.wb.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
