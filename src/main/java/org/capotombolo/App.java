package org.capotombolo;

import org.capotombolo.excel.ExcelTools;
import org.capotombolo.flow.Execution;
import org.capotombolo.git.GitSkills;
import org.capotombolo.jira.Jira;
import org.capotombolo.metrics.*;
import org.capotombolo.proportion.Proportion;
import org.capotombolo.utils.*;
import org.capotombolo.weka.ArffFileCreator;
import org.capotombolo.weka.ExcelRowWeka;
import org.capotombolo.weka.WalkForward;
import java.util.*;

public class App 
{

    static final String PROJECT = "BOOKKEEPER";

    public static void main(String[] args) throws Exception {
        final String PATH = "C:" +
                "\\Users" +
                "\\lucac" +
                "\\ESAME FALESSI" +
                "\\bookkeeper";

        //Get All Releases from JIRA
        List<Release> releaseList = Jira.getReleases(PROJECT);

        //number of Excel release
        int nRelease = releaseList.size() / 2;

        //Get All Bugs from JIRA
        List<Issue> issueList = Jira.getBugs(PROJECT, releaseList);

        //Get all files for each release
        GitSkills gitSkills = new GitSkills(PATH);

        HashMap<Release, List<MyFile>> hashMapReleaseFiles = new HashMap<>();

        Execution.getAllFileRelease(PATH, hashMapReleaseFiles, nRelease, releaseList);

        //Set the value of the metrics for all files in all releases
        //Get all commits of Project
        CommitProjectProducer commitProjectProducer = new CommitProjectProducer(releaseList, PATH);
        List<CommitMetric> commitMetrics = commitProjectProducer.getAllCommitOfProject();
        //Get all revision for each file in all releases
        NumberOfRevisionTotal numberOfRevisionTotal = new NumberOfRevisionTotal(hashMapReleaseFiles, commitMetrics);
        hashMapReleaseFiles = (HashMap<Release, List<MyFile>>) numberOfRevisionTotal.getNumberOfRevisionTotal();
        //Get all revision for each file in its release
        NumberOfRevisionRelease numberOfRevisionRelease = new NumberOfRevisionRelease(hashMapReleaseFiles, commitMetrics);
        hashMapReleaseFiles = (HashMap<Release, List<MyFile>>) numberOfRevisionRelease.getNumberOfRevisionRelease();
        //Get all authors for each file in all releases
        NumberOfAuthors numberOfAuthors = new NumberOfAuthors(hashMapReleaseFiles, commitMetrics);
        hashMapReleaseFiles = (HashMap<Release, List<MyFile>>) numberOfAuthors.getNumberOfAuthors();
        //Get LOC ADDED for each file in all releases
        //Get LOC ADDED (with maximum and average) for each file in its release
        LocAddedRelease locAddedRelease = new LocAddedRelease(hashMapReleaseFiles, commitMetrics, PATH);
        hashMapReleaseFiles = (HashMap<Release, List<MyFile>>) locAddedRelease.computeLocAddedRelease();
        //Get ChgSetSize (with maximum and average)
        ChgSetSize chgSetSize = new ChgSetSize(hashMapReleaseFiles, commitMetrics);
        hashMapReleaseFiles = (HashMap<Release, List<MyFile>>) chgSetSize.getChgSetSizeRelease();

        //Get all commits for each issue
        HashMap<Issue, List<Commit>> hashMapIssueCommits;
        hashMapIssueCommits = (HashMap<Issue, List<Commit>>) gitSkills.classifyCommitsIssue(issueList, releaseList);

        //I order the issues by fixDate
        issueList.sort(Comparator.comparing(o -> o.fixDate));

        //Compute IV of all issues that have inconsistent AV on JIRA and then labeling testing releases
        Proportion proportion = new Proportion(releaseList, issueList);
        float pGlobal = proportion.globalProportion();

        Execution.computeIVLabelingTestingGlobalProportion(issueList, releaseList, pGlobal);

        //I will create N - 1 csv for testing releases
        boolean ret;
        ExcelTools excelTools;
        ArffFileCreator arffFileCreator = new ArffFileCreator();

        //Labeling java files for testing set in walk-forward

        Execution.labelingTestingSets(releaseList, nRelease, hashMapReleaseFiles, issueList, hashMapIssueCommits, PROJECT);

        //I have labeled all the java classes of testing releases (even release zero)
        ret = arffFileCreator.createArffFileTestingSet(hashMapReleaseFiles, releaseList, "testing_" + PROJECT);
        if(!ret)
            System.exit(-5);

        excelTools = new ExcelTools("ISW2", PROJECT, "");
        ret = excelTools.createTable();
        if(!ret)
            System.exit(-14);
        ret = excelTools.writeData(hashMapReleaseFiles, releaseList);
        if(!ret)
            System.exit(-13);

        Execution.clean(issueList, releaseList, nRelease, hashMapReleaseFiles);


        //I do the labeling of the training sets that I will use in the walk-forward. I use all information in the training set
        List<Float> pSubGlobals = proportion.incrementTrainingSet();

        Execution.labelingTrainingSets(nRelease, releaseList, PROJECT, issueList, hashMapReleaseFiles, pSubGlobals, hashMapIssueCommits);

        WalkForward walkForward = new WalkForward();
        List<ExcelRowWeka> excelRowWekaList = walkForward.executeWalkForward(releaseList, PROJECT);

        ExcelTools excelTools1 = new ExcelTools(null, null, null);
        ret = excelTools1.writeWekaResult(excelRowWekaList, PROJECT);
        if(!ret)
            System.exit(-90);
    }
}
