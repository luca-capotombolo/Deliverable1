package org.capotombolo.weka;

public class ExcelRowWeka {
    public int getNumberTrainingRelease() {
        return numberTrainingRelease;
    }

    public void setNumberTrainingRelease(int numberTrainingRelease) {
        this.numberTrainingRelease = numberTrainingRelease;
    }

    public float getPercentTraining() {
        return percentTraining;
    }

    public void setPercentTraining(float percentTraining) {
        this.percentTraining = percentTraining;
    }

    public double getPercentDefectiveTraining() {
        return percentDefectiveTraining;
    }

    public void setPercentDefectiveTraining(double percentDefectiveTraining) {
        this.percentDefectiveTraining = percentDefectiveTraining;
    }

    public double getPercentDefectiveTesting() {
        return percentDefectiveTesting;
    }

    public void setPercentDefectiveTesting(double percentDefectiveTesting) {
        this.percentDefectiveTesting = percentDefectiveTesting;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getBalancing() {
        return balancing;
    }

    public void setBalancing(String balancing) {
        this.balancing = balancing;
    }

    public String getFeatureSelection() {
        return featureSelection;
    }

    public void setFeatureSelection(String featureSelection) {
        this.featureSelection = featureSelection;
    }

    public String getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(String sensitivity) {
        this.sensitivity = sensitivity;
    }

    public double getTp() {
        return tp;
    }

    public void setTp(double tp) {
        this.tp = tp;
    }

    public double getFp() {
        return fp;
    }

    public void setFp(double fp) {
        this.fp = fp;
    }

    public double getTn() {
        return tn;
    }

    public void setTn(double tn) {
        this.tn = tn;
    }

    public double getFn() {
        return fn;
    }

    public void setFn(double fn) {
        this.fn = fn;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getAuc() {
        return auc;
    }

    public void setAuc(double auc) {
        this.auc = auc;
    }

    public double getKappa() {
        return kappa;
    }

    public void setKappa(double kappa) {
        this.kappa = kappa;
    }

    private  int numberTrainingRelease;
    private  float percentTraining = 0;
    private  double percentDefectiveTraining;
    private  double percentDefectiveTesting;
    private  String classifier;
    private  String balancing;
    private  String featureSelection;
    private  String sensitivity;
    private  double tp;
    private  double fp;
    private  double tn;
    private  double fn;
    private  double precision;
    private  double recall;
    private  double auc;
    private  double kappa;
}
