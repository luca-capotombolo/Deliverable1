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
            logger.log(Level.INFO, String.format("TESTING RELEASE: %s", testingRelease.name));
            logger.log(Level.INFO, "YOUNGER RELEASE: " + youngerRelease.name);

            source1 = new ConverterUtils.DataSource("C:\\Users\\lucac\\MyJava\\Deliverable1\\training_"+project+"_"+youngerRelease.name + ".arff");
            training = source1.getDataSet();
            source2 = new ConverterUtils.DataSource("C:\\Users\\lucac\\MyJava\\Deliverable1\\testing_"+project+"_"+testingRelease.name + ".arff");
            testing = source2.getDataSet();

            int numAttr = training.numAttributes();
            training.setClassIndex(numAttr - 1);
            testing.setClassIndex(numAttr - 1);
            logger.log(Level.INFO, String.format("NUMBER ATTR: %s", numAttr));

            classifierBayes = new NaiveBayes();
            classifierBayes.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierBayes, testing);

            logger.log(Level.INFO, String.format("NAIVE BAYES PRECISION: %s" , eval.precision(0)));
            logger.log(Level.INFO, String.format("NAIVE BAYES RECALL: %s",eval.recall(0)));
            logger.log(Level.INFO, String.format("NAIVE BAYES AUC = %s",eval.areaUnderROC(0)));
            logger.log(Level.INFO, String.format("NAIVE BAYES kappa = %s",eval.kappa()));

            classifierRandomWalker = new RandomForest();
            classifierRandomWalker.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierRandomWalker, testing);

            logger.log(Level.INFO, String.format("RANDOM FOREST PRECISION: %s" , eval.precision(0)));
            logger.log(Level.INFO , String.format("RANDOM FOREST RECALL: %s" , eval.recall(0)));
            logger.log(Level.INFO, String.format("RANDOM FOREST AUC = %s", eval.areaUnderROC(0)));
            logger.log(Level.INFO, String.format("RANDOM FOREST kappa = %s",eval.kappa()));

            classifierIBK = new IBk();
            classifierIBK.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierIBK, testing);

            logger.log(Level.INFO, String.format("IBK PRECISION: %s" , eval.precision(0)));
            logger.log(Level.INFO, String.format("IBK RECALL: %s" , eval.recall(0)));
            logger.log(Level.INFO, String.format("IBK AUC = %s",eval.areaUnderROC(0)));
            logger.log(Level.INFO, String.format("IBK kappa =%s",eval.kappa()));

        }

    }

}
