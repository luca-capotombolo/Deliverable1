package org.capotombolo.weka;

import org.capotombolo.utils.Release;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SpreadSubsample;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class WalkForward {
    private static final String PATH = "C:" + "\\Users" + "\\lucac" + "\\MyJava" + "\\Deliverable1";
    private static final String ARFF =".arff";
    private static final String TRAINING_STRING = "\\training_";
    private static final String TESTING_STRING = "\\testing_";
    private static final String RANDOM_FOREST = "Random Forest";
    private static final String NAIVE_BAYES = "Naive Bayes";
    private static final String IBK = "IBK";

    public List<ExcelRowWeka> executeWalkForward(List<Release> releaseList, String project) throws Exception {
        List<ExcelRowWeka> list1 = walkForwardStandard(releaseList, project);
        List<ExcelRowWeka> fullList = new ArrayList<>(list1);
        List<ExcelRowWeka> list2 = walkForwardOverSampling(releaseList, project);
        fullList.addAll(list2);
        List<ExcelRowWeka> list3 = walkForwardUnderSampling(releaseList, project);
        fullList.addAll(list3);
        List<ExcelRowWeka> list4 = walkForwardFeatureSelection(releaseList, project);
        fullList.addAll(list4);

        return fullList;
    }

    private void writeExcelRowWeka(int count, String [] args, float sensitivity, List<ExcelRowWeka> excelRowWekaList, double [] bugs,Evaluation eval){
        ExcelRowWeka excelRowWekaFS = new ExcelRowWeka();
        excelRowWekaFS.setNumberTrainingRelease(count);
        excelRowWekaFS.setPercentTraining(-1);
        excelRowWekaFS.setPercentDefectiveTraining((bugs[0]/(bugs[1] + bugs[0])));
        excelRowWekaFS.setPercentDefectiveTesting((bugs[2]/(bugs[2] + bugs[3])));
        excelRowWekaFS.setClassifier(args[0]);
        excelRowWekaFS.setBalancing(args[1]);
        excelRowWekaFS.setFeatureSelection(args[2]);
        excelRowWekaFS.setSensitivity(sensitivity);
        excelRowWekaFS.setTp(eval.numTruePositives(0));
        excelRowWekaFS.setFp(eval.numFalsePositives(0));
        excelRowWekaFS.setTn(eval.numTruePositives(1));
        excelRowWekaFS.setFn(eval.numFalsePositives(1));
        excelRowWekaFS.setPrecision(eval.precision(0));
        excelRowWekaFS.setRecall(eval.recall(0));
        excelRowWekaFS.setAuc(eval.areaUnderROC(0));
        excelRowWekaFS.setKappa(eval.kappa());
        excelRowWekaList.add(excelRowWekaFS);
    }

    private List<ExcelRowWeka> walkForwardFeatureSelection(List<Release> releaseList, String project) throws Exception {
        ConverterUtils.DataSource sourceFS1;
        Instances trainingFS;
        Instances newTrainingFS;
        Instances newTestingFS;
        ConverterUtils.DataSource sourceFS2;
        Instances testingFS;
        Release testingReleaseFS;
        NaiveBayes classifierBayesFS;
        RandomForest classifierRandomForestFS;
        IBk classifierIBKFS;
        Evaluation eval;
        Release youngerRelease;
        List<ExcelRowWeka> excelRowWekaList = new ArrayList<>();

        for (int count1 = 1; count1 <= releaseList.size() / 2; count1++) {
            youngerRelease = releaseList.get(count1 - 1);
            testingReleaseFS = releaseList.get(count1);

            sourceFS1 = new ConverterUtils.DataSource(PATH + TRAINING_STRING + project + "_" + youngerRelease.name + ARFF);
            trainingFS = sourceFS1.getDataSet();
            sourceFS2 = new ConverterUtils.DataSource(PATH + TESTING_STRING + project + "_" + testingReleaseFS.name + ARFF);
            testingFS = sourceFS2.getDataSet();

            int numAttr = trainingFS.numAttributes();
            trainingFS.setClassIndex(numAttr - 1);
            testingFS.setClassIndex(numAttr - 1);

            //check if there is at least one buggy
            double [] bugs;
            bugs = getCountBuggyNoBuggy(trainingFS, null);

            //tolgo i training set che non hanno alcuna classe buggy
            if (bugs[0] == 0)
                continue;


            //NAIVE BAYES
            classifierBayesFS = new NaiveBayes();
            AttributeSelection attributeSelection = new AttributeSelection();
            CfsSubsetEval cfsSubsetEval = new CfsSubsetEval();
            BestFirst search = new BestFirst();
            attributeSelection.setEvaluator(cfsSubsetEval);
            attributeSelection.setSearch(search);
            attributeSelection.setInputFormat(trainingFS);
            newTrainingFS = Filter.useFilter(trainingFS, attributeSelection);
            newTestingFS = Filter.useFilter(testingFS, attributeSelection);
            newTrainingFS.setClassIndex(newTrainingFS.numAttributes() - 1);
            classifierBayesFS.buildClassifier(newTrainingFS);
            eval = new Evaluation(testingFS);
            eval.evaluateModel(classifierBayesFS, newTestingFS);

            bugs = getCountBuggyNoBuggy(newTrainingFS, newTestingFS);
            String bestFirst = "BEST FIRST";

            String [] args = {NAIVE_BAYES, "NONE", bestFirst};

            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);

            //RANDOM FOREST
            classifierRandomForestFS = new RandomForest();
            attributeSelection = new AttributeSelection();
            cfsSubsetEval = new CfsSubsetEval();
            search = new BestFirst();
            attributeSelection.setEvaluator(cfsSubsetEval);
            attributeSelection.setSearch(search);
            attributeSelection.setInputFormat(trainingFS);
            newTrainingFS = Filter.useFilter(trainingFS, attributeSelection);
            newTestingFS = Filter.useFilter(testingFS, attributeSelection);
            newTrainingFS.setClassIndex(newTrainingFS.numAttributes() - 1);
            classifierRandomForestFS.buildClassifier(newTrainingFS);
            eval = new Evaluation(testingFS);
            eval.evaluateModel(classifierRandomForestFS, newTestingFS);

            bugs = getCountBuggyNoBuggy(newTrainingFS, newTestingFS);

            args = new String[]{RANDOM_FOREST, "NONE", bestFirst};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);



            classifierIBKFS = new IBk();
            attributeSelection = new AttributeSelection();
            cfsSubsetEval = new CfsSubsetEval();
            search = new BestFirst();
            attributeSelection.setEvaluator(cfsSubsetEval);
            attributeSelection.setSearch(search);
            attributeSelection.setInputFormat(trainingFS);
            newTrainingFS = Filter.useFilter(trainingFS, attributeSelection);
            newTestingFS = Filter.useFilter(testingFS, attributeSelection);
            newTrainingFS.setClassIndex(newTrainingFS.numAttributes() - 1);
            classifierIBKFS.buildClassifier(newTrainingFS);
            eval = new Evaluation(testingFS);
            eval.evaluateModel(classifierIBKFS, newTestingFS);

            bugs = getCountBuggyNoBuggy(newTrainingFS, newTestingFS);

            args = new String[]{IBK, "NONE", bestFirst};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);

        }
        return excelRowWekaList;
    }

    private double[] getCountBuggyNoBuggy(Instances training, Instances testing){
        double [] array = new double[4];
        Enumeration<Instance> enumeration = training.enumerateInstances();
        Instance instance;
        double value;
        double countBuggyTraining = 0;
        double countNoBuggyTraining = 0;
        while (enumeration.hasMoreElements()) {
            instance = enumeration.nextElement();
            value = instance.classValue();
            if (value == 0)
                countBuggyTraining++;
            else
                countNoBuggyTraining++;
        }
        double countBuggyTesting = 0;
        double countNoBuggyTesting = 0;
        if(testing!=null) {
            enumeration = testing.enumerateInstances();
            while (enumeration.hasMoreElements()) {
                instance = enumeration.nextElement();
                value = instance.classValue();
                if (value == 0)
                    countBuggyTesting++;
                else
                    countNoBuggyTesting++;
            }
        }
        array[0] = countBuggyTraining;
        array[1] = countNoBuggyTraining;
        array[2] = countBuggyTesting;
        array[3] = countNoBuggyTesting;
        return array;
    }

    private List<ExcelRowWeka> walkForwardUnderSampling(List<Release> releaseList, String project) throws Exception {
        ConverterUtils.DataSource sourceUnderSampling1;
        Instances trainingUS;
        Instances newTrainingUS;
        ConverterUtils.DataSource sourceUnderSampling2;
        Instances testingUS;
        Release testingReleaseUS;
        NaiveBayes classifierBayesUS;
        RandomForest classifierRandomForestUS;
        IBk classifierIBKUS;
        Evaluation eval;
        double [] bugs;
        Release youngerRelease;
        List<ExcelRowWeka> excelRowWekaList = new ArrayList<>();
        String underSampling = "UNDER SAMPLING";


        for (int count1 = 1; count1 <= releaseList.size() / 2; count1++) {
            youngerRelease = releaseList.get(count1 - 1);
            testingReleaseUS = releaseList.get(count1);

            sourceUnderSampling1 = new ConverterUtils.DataSource(PATH + TRAINING_STRING + project + "_" + youngerRelease.name + ARFF);
            trainingUS = sourceUnderSampling1.getDataSet();
            sourceUnderSampling2 = new ConverterUtils.DataSource(PATH + TESTING_STRING + project + "_" + testingReleaseUS.name + ARFF);
            testingUS = sourceUnderSampling2.getDataSet();

            int numAttr = trainingUS.numAttributes();
            trainingUS.setClassIndex(numAttr - 1);
            testingUS.setClassIndex(numAttr - 1);

            bugs = getCountBuggyNoBuggy(trainingUS, null);

            //tolgo i training set che non hanno alcuna classe buggy
            if (bugs[0] == 0)
                continue;

            classifierBayesUS = new NaiveBayes();
            SpreadSubsample spreadSubsample = new SpreadSubsample();
            String [] opts = new String[]{"-M", "1.0"};
            spreadSubsample.setOptions(opts);
            spreadSubsample.setInputFormat(trainingUS);
            newTrainingUS = Filter.useFilter(trainingUS, spreadSubsample);
            FilteredClassifier filteredClassifier = new FilteredClassifier();
            filteredClassifier.setClassifier(classifierBayesUS);
            filteredClassifier.setFilter(spreadSubsample);
            filteredClassifier.buildClassifier(newTrainingUS);
            eval = new Evaluation(testingUS);
            eval.evaluateModel(filteredClassifier, testingUS);

            bugs = getCountBuggyNoBuggy(newTrainingUS, testingUS);

            String [] args = new String[]{NAIVE_BAYES, underSampling, "NONE"};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);



            classifierRandomForestUS = new RandomForest();
            spreadSubsample = new SpreadSubsample();
            opts = new String[]{"-M", "1.0"};
            spreadSubsample.setOptions(opts);
            spreadSubsample.setInputFormat(trainingUS);
            newTrainingUS = Filter.useFilter(trainingUS, spreadSubsample);
            filteredClassifier = new FilteredClassifier();
            filteredClassifier.setClassifier(classifierRandomForestUS);
            filteredClassifier.setFilter(spreadSubsample);
            filteredClassifier.buildClassifier(newTrainingUS);
            eval = new Evaluation(testingUS);
            eval.evaluateModel(filteredClassifier, testingUS);

            bugs = getCountBuggyNoBuggy(newTrainingUS, testingUS);

            args = new String[]{RANDOM_FOREST, underSampling, "NONE"};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);



            classifierIBKUS = new IBk();
            spreadSubsample = new SpreadSubsample();
            opts = new String[]{"-M", "1.0"};
            spreadSubsample.setOptions(opts);
            spreadSubsample.setInputFormat(trainingUS);
            newTrainingUS = Filter.useFilter(trainingUS, spreadSubsample);
            filteredClassifier = new FilteredClassifier();
            filteredClassifier.setClassifier(classifierIBKUS);
            filteredClassifier.setFilter(spreadSubsample);
            filteredClassifier.buildClassifier(newTrainingUS);
            eval = new Evaluation(testingUS);
            eval.evaluateModel(filteredClassifier, testingUS);

            bugs = getCountBuggyNoBuggy(newTrainingUS, testingUS);

            args = new String[]{IBK, underSampling, "NONE"};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);

        }
        return excelRowWekaList;
    }

    private List<ExcelRowWeka> walkForwardOverSampling(List<Release> releaseList, String project) throws Exception {
        ConverterUtils.DataSource sourceOverSampling1;
        Instances trainingOS;
        Instances newTrainingOS;
        ConverterUtils.DataSource sourceOverSampling2;
        Instances testingOS;
        Release testingRelease;
        NaiveBayes classifierBayesOS;
        RandomForest classifierRandomForestOS;
        IBk classifierIBKOS;
        Evaluation eval;
        Release youngerRelease;
        List<ExcelRowWeka> excelRowWekaList = new ArrayList<>();
        double [] bugs;
        String overSampling = "OVER SAMPLING";


        for (int count1 = 1; count1 <= releaseList.size() / 2; count1++) {
            youngerRelease = releaseList.get(count1 - 1);
            testingRelease = releaseList.get(count1);

            sourceOverSampling1 = new ConverterUtils.DataSource(PATH + TRAINING_STRING + project + "_" + youngerRelease.name + ARFF);
            trainingOS = sourceOverSampling1.getDataSet();
            sourceOverSampling2 = new ConverterUtils.DataSource(PATH + TESTING_STRING + project + "_" + testingRelease.name + ARFF);
            testingOS = sourceOverSampling2.getDataSet();

            int numAttr = trainingOS.numAttributes();
            trainingOS.setClassIndex(numAttr - 1);
            testingOS.setClassIndex(numAttr - 1);

            bugs = getCountBuggyNoBuggy(trainingOS, null);

            //tolgo i training set che non hanno alcuna classe buggy
            if (bugs[0] == 0)
                continue;

            classifierBayesOS = new NaiveBayes();
            Resample resample = new Resample();
            //non ci sono abbastanza istanza BUG per fare senza rimpiazzamento
            String z = Double.toString((100 - (bugs[0]/trainingOS.size()*100)) * 2);
            String [] opts = new String[]{"-B", "1.0", "-Z", z};
            resample.setOptions(opts);
            resample.setInputFormat(trainingOS);
            newTrainingOS = Filter.useFilter(trainingOS, resample);
            FilteredClassifier filteredClassifier = new FilteredClassifier();
            filteredClassifier.setClassifier(classifierBayesOS);
            filteredClassifier.setFilter(resample);
            filteredClassifier.buildClassifier(newTrainingOS);
            eval = new Evaluation(testingOS);
            eval.evaluateModel(filteredClassifier, testingOS);

            bugs = getCountBuggyNoBuggy(newTrainingOS, testingOS);

            String [] args = new String[]{NAIVE_BAYES, overSampling, "NONE"};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);



            classifierRandomForestOS = new RandomForest();
            resample = new Resample();
            opts = new String[]{"-B", "1.0", "-Z", z};
            resample.setOptions(opts);
            resample.setInputFormat(trainingOS);
            newTrainingOS = Filter.useFilter(trainingOS, resample);
            filteredClassifier = new FilteredClassifier();
            filteredClassifier.setClassifier(classifierRandomForestOS);
            filteredClassifier.setFilter(resample);
            filteredClassifier.buildClassifier(newTrainingOS);
            eval = new Evaluation(testingOS);
            eval.evaluateModel(filteredClassifier, testingOS);

            bugs = getCountBuggyNoBuggy(newTrainingOS, testingOS);


            args = new String[]{RANDOM_FOREST, overSampling, "NONE"};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);



            classifierIBKOS = new IBk();
            resample = new Resample();
            opts = new String[]{"-B", "1.0", "-Z", z};
            resample.setOptions(opts);
            resample.setInputFormat(trainingOS);
            newTrainingOS = Filter.useFilter(trainingOS, resample);
            filteredClassifier = new FilteredClassifier();
            filteredClassifier.setClassifier(classifierIBKOS);
            filteredClassifier.setFilter(resample);
            filteredClassifier.buildClassifier(newTrainingOS);
            eval = new Evaluation(testingOS);
            eval.evaluateModel(filteredClassifier, testingOS);

            bugs = getCountBuggyNoBuggy(newTrainingOS, testingOS);

            args = new String[]{IBK, overSampling, "NONE"};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);

        }
        return excelRowWekaList;
    }

    private List<ExcelRowWeka> walkForwardStandard(List<Release> releaseList, String project) throws Exception {
        ConverterUtils.DataSource source1;
        Instances training;
        ConverterUtils.DataSource source2;
        Instances testing;
        Release testingRelease;
        NaiveBayes classifierBayes;
        RandomForest classifierRandomForest;
        IBk classifierIBK;
        Evaluation eval;
        Release youngerRelease;
        List<ExcelRowWeka> excelRowWekaList = new ArrayList<>();
        double [] bugs;


        //count1 = 0 --> [1]    count = 1 --> [1;2]     count = 2 --> [1,2;3]...
        for (int count1 = 1; count1 <= releaseList.size() / 2; count1++) {
            youngerRelease = releaseList.get(count1 - 1);
            testingRelease = releaseList.get(count1);

            source1 = new ConverterUtils.DataSource(PATH + TRAINING_STRING + project + "_" + youngerRelease.name + ARFF);
            training = source1.getDataSet();
            source2 = new ConverterUtils.DataSource(PATH + TESTING_STRING + project + "_" + testingRelease.name + ARFF);
            testing = source2.getDataSet();

            int numAttr = training.numAttributes();
            training.setClassIndex(numAttr - 1);
            testing.setClassIndex(numAttr - 1);

            bugs = getCountBuggyNoBuggy(training, testing);

            //tolgo i training set che non hanno alcuna classe buggy
            if (bugs[0] == 0)
                continue;

            classifierBayes = new NaiveBayes();
            classifierBayes.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierBayes, testing);

            String [] args = new String[]{NAIVE_BAYES, "NONE", "NONE"};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);


            classifierRandomForest = new RandomForest();
            classifierRandomForest.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierRandomForest, testing);

            args = new String[]{RANDOM_FOREST, "NONE", "NONE"};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);

            classifierIBK = new IBk();
            classifierIBK.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierIBK, testing);


            args = new String[]{IBK, "NONE", "NONE"};
            writeExcelRowWeka(count1, args,0.5f,excelRowWekaList,bugs,eval);

        }
        return excelRowWekaList;
    }

}
