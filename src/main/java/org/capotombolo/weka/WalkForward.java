package org.capotombolo.weka;

import org.capotombolo.utils.Release;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import java.util.List;

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

        //implementing walk-forward...
        for(int count1=0; count1<=releaseList.size()/2; count1++){
            if(count1==0)
                continue;
            youngerRelease = releaseList.get(count1-1);
            testingRelease = releaseList.get(count1);
            System.out.println("TESTING RELEASE: " + testingRelease.release);
            System.out.println("YOUNGER RELEASE: " + youngerRelease.release);

            source1 = new ConverterUtils.DataSource("C:\\Users\\lucac\\MyJava\\Deliverable1\\training_"+project+"_"+youngerRelease.release + ".arff");
            training = source1.getDataSet();
            source2 = new ConverterUtils.DataSource("C:\\Users\\lucac\\MyJava\\Deliverable1\\testing_"+project+"_"+testingRelease.release + ".arff");
            testing = source2.getDataSet();

            int numAttr = training.numAttributes();
            training.setClassIndex(numAttr - 1);
            testing.setClassIndex(numAttr - 1);
            System.out.println("NUMBER ATTR: " + numAttr);

            classifierBayes = new NaiveBayes();
            classifierBayes.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierBayes, testing);
            System.out.println("NAIVE BAYES PRECISION: " + eval.precision(0));
            System.out.println("NAIVE BAYES RECALL: " + eval.recall(0));
            System.out.println("NAIVE BAYES AUC = "+eval.areaUnderROC(0));
            System.out.println("NAIVE BAYES kappa = "+eval.kappa());

            classifierRandomWalker = new RandomForest();
            classifierRandomWalker.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierRandomWalker, testing);
            System.out.println("RANDOM FOREST PRECISION: " + eval.precision(0));
            System.out.println("RANDOM FOREST RECALL: " + eval.recall(0));
            System.out.println("RANDOM FOREST AUC = "+eval.areaUnderROC(0));
            System.out.println("RANDOM FOREST kappa = "+eval.kappa());

            classifierIBK = new IBk();
            classifierIBK.buildClassifier(training);
            eval = new Evaluation(testing);
            eval.evaluateModel(classifierIBK, testing);
            System.out.println("IBK PRECISION: " + eval.precision(0));
            System.out.println("IBK RECALL: " + eval.recall(0));
            System.out.println("IBK AUC = "+eval.areaUnderROC(0));
            System.out.println("IBK kappa = "+eval.kappa());
        }

    }

}
