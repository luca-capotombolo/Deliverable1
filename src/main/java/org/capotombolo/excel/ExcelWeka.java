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
import java.util.Objects;

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
            String [] classifiers = {"Random Forest", "Naive Bayes", "IBK"};
            Cell cellWE;
            for(String classifier: classifiers){
                for(ExcelRowWeka excelRowWeka: excelRowWekaList){
                    if(!Objects.equals(excelRowWeka.getClassifier(), classifier))
                        continue;
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
        int [] widths = {5000, 6000, 5000, 7000, 7000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000, 5000};
        String [] titles = {"project", "numberTrainingRelease", "percentTraining", "percentDefectiveTraining", "percentDefectiveTesting",
                "classifier", "balancing", "featureSelection", "sensitivity", "TP", "FP", "TN", "FN", "precision", "recall", "auc","kappa"};
        int count = 0;

        try (OutputStream fileOut = Files.newOutputStream(Paths.get(project+"_wekaResults.csv"))) {
            Sheet sheet5 = wb.createSheet("weka");
            Row titleRow = sheet5.createRow(0);
            for(;count<widths.length;count++){
                Cell cellWT = titleRow.createCell(count);
                cellWT.setCellValue(titles[count]);
                sheet5.setColumnWidth(count, widths[count]);
            }
            this.wb.write(fileOut);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
