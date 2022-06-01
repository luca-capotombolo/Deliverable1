package org.capotombolo.weka;

import org.capotombolo.utils.Release;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WalkForward {

    public void executeWalkForward(List<Release> releaseList, String project) throws Exception {
        ConverterUtils.DataSource source1;
        Instances training;
        ConverterUtils.DataSource source2;
        Instances testing;
        Release testingRelease;
        NaiveBayes classifierBayes;
        RandomForest classifierRandomWalker;
        IBk classifierIBK;
        Evaluation eval;
        String out;
        Release youngerRelease;
        float precisionMean = 0;
        float kappaMean = 0;
        float aucMean = 0;
        float recall = 0;

        Logger logger = Logger.getLogger(WalkForward.class.getName());

        //implementing walk-forward...
        for(int count1=0; count1<=releaseList.size()/2; count1++){
            if(count1==0)
                continue;
            youngerRelease = releaseList.get(count1-1);
            testingRelease = releaseList.get(count1);
            out = "TESTING RELEASE: " + testingRelease.name;
            logger.log(Level.INFO, out);
            out ="YOUNGER RELEASE: " + youngerRelease.name;
            logger.log(Level.INFO, out);

            source1 = new ConverterUtils.DataSource("C:\\Users\\lucac\\MyJava\\Deliverable1\\training_"+project+"_"+youngerRelease.name + ".arff");
            training = source1.getDataSet();
            source2 = new ConverterUtils.DataSource("C:\\Users\\lucac\\MyJava\\Deliverable1\\testing_"+project+"_"+testingRelease.name + ".arff");
            testing = source2.getDataSet();

            int numAttr = training.numAttributes();
            training.setClassIndex(numAttr - 1);
            testing.setClassIndex(numAttr - 1);
            out = "NUMBER ATTR: " + numAttr;
            logger.log(Level.INFO, out);

            classifierBayes = new NaiveBayes();
            classifierBayes.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierBayes, testing);

            out = "NAIVE BAYES PRECISION: " + eval.precision(0);
            logger.log(Level.INFO, out);
            out = "NAIVE BAYES RECALL: " + eval.recall(0);
            logger.log(Level.INFO, out);
            out = "NAIVE BAYES AUC = " + eval.areaUnderROC(0);
            logger.log(Level.INFO, out);
            out = "NAIVE BAYES kappa = " + eval.kappa();
            logger.log(Level.INFO, out);

            classifierRandomWalker = new RandomForest();
            classifierRandomWalker.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierRandomWalker, testing);

            out = "RANDOM FOREST PRECISION: " + eval.precision(0);
            logger.log(Level.INFO, out);
            out = "RANDOM FOREST RECALL: " + eval.recall(0);
            logger.log(Level.INFO , out);
            out = "RANDOM FOREST AUC = " + eval.areaUnderROC(0);
            logger.log(Level.INFO, out);
            out = "RANDOM FOREST kappa = " + eval.kappa();
            logger.log(Level.INFO, out);

            classifierIBK = new IBk();
            classifierIBK.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierIBK, testing);

            out = "IBK PRECISION: " + eval.precision(0);
            logger.log(Level.INFO, out);
            out = "IBK RECALL: " +  eval.recall(0);
            logger.log(Level.INFO, out);
            out = "IBK AUC = " + eval.areaUnderROC(0);
            logger.log(Level.INFO, out);
            out = "IBK kappa = " + eval.kappa();
            logger.log(Level.INFO, out);

        }

    }

}
