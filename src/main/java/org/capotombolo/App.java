package org.capotombolo;

import org.capotombolo.excel.ExcelTools;
import org.capotombolo.filesystem.FoundAllJavaFiles;
import org.capotombolo.git.GitSkills;
import org.capotombolo.jira.Jira;
import org.capotombolo.metrics.*;
import org.capotombolo.proportion.Proportion;
import org.capotombolo.utils.*;
import org.capotombolo.weka.ArffFileCreator;
import org.capotombolo.weka.WalkForward;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import java.util.*;

public class App 
{

    static final String PROJECT = "BOOKKEEPER";
    static final String PATH = "C:\\Users\\lucac\\ESAME FALESSI\\bookkeeper";
    static final String MACRO1 = "training_";

    public static void main(String[] args) throws Exception {

        //final String project = "ZOOKEEPER";
        //final String path = "C:\\Users\\lucac\\ESAME FALESSI\\zookeeper";
        // String project = "BOOKKEEPER";
        //final String path = "C:\\Users\\lucac\\ESAME FALESSI\\bookkeeper";

        //Get All Releases from JIRA
        List<Release> releaseList = Jira.getReleases(PROJECT);

        //number of Excel release
        int nRelease = releaseList.size() / 2;

        //Get All Bugs from JIRA
        List<Issue> issueList = Jira.getBugs(PROJECT, releaseList);

        //Get all files for each release
        GitSkills gitSkills = new GitSkills(PATH);
        FoundAllJavaFiles foundAllJavaFiles;
        int count = 0;
        List<MyFile> filenames;
        HashMap<Release, List<MyFile>> hashMapReleaseFiles = new HashMap<>();
        HashMap<Issue, List<Commit>> hashMapIssueCommits;
        for (Release release : releaseList) {
            try {
                gitSkills.setBranch(release.getName());
            } catch (RefNotFoundException e) {
                //inconsistent data on JIRA
                continue;
            }
            if (count <= nRelease) {
                foundAllJavaFiles = new FoundAllJavaFiles(PATH);
                //all java files in the release
                filenames = foundAllJavaFiles.foundAllFiles();
                hashMapReleaseFiles.put(release, filenames);
            }
            count++;
        }


        //Set the value of the metrics for all files in all releases
        //Get all commits of Project
        CommitProjectProducer commitProjectProducer = new CommitProjectProducer(releaseList, PATH);
        List<CommitMetric> commitMetrics = commitProjectProducer.getAllCommitOfProject();
        //Get all revision for each file in all releases
        NumberOfRevisionTotal numberOfRevisionTotal = new NumberOfRevisionTotal(hashMapReleaseFiles, commitMetrics);
        hashMapReleaseFiles = numberOfRevisionTotal.getNumberOfRevisionTotal();
        //Get all revision for each file in its release
        NumberOfRevisionRelease numberOfRevisionRelease = new NumberOfRevisionRelease(hashMapReleaseFiles, commitMetrics);
        hashMapReleaseFiles = numberOfRevisionRelease.getNumberOfRevisionRelease();
        //Get all authors for each file in all releases
        NumberOfAuthors numberOfAuthors = new NumberOfAuthors(hashMapReleaseFiles, commitMetrics);
        hashMapReleaseFiles = numberOfAuthors.getNumberOfAuthors();
        //Get LOC ADDED for each file in all releases
        LocAdded locAdded = new LocAdded(hashMapReleaseFiles, commitMetrics, PATH);
        hashMapReleaseFiles = locAdded.computeLocAdded();
        //Get LOC ADDED (with maximum and average) for each file in its release
        LocAddedRelease locAddedRelease = new LocAddedRelease(hashMapReleaseFiles, commitMetrics, PATH);
        hashMapReleaseFiles = locAddedRelease.computeLocAddedRelease();
        //Get ChgSetSize (with maximum and average)
        ChgSetSize chgSetSize = new ChgSetSize(hashMapReleaseFiles, commitMetrics);
        hashMapReleaseFiles = chgSetSize.getChgSetSizeRelease();

        //Get all commits for each issue
        hashMapIssueCommits = (HashMap<Issue, List<Commit>>) gitSkills.classifyCommitsIssue(issueList, releaseList);

        //I order the issues by fixDate
        issueList.sort(Comparator.comparing(o -> o.fixDate));

        //Compute IV of all issues that have inconsistent AV on JIRA and then labeling testing releases
        Proportion proportion = new Proportion(releaseList, issueList);
        float pGlobal = proportion.globalProportion();

        float fv;
        float ov;
        int index;
        for(Issue issue: issueList){
            if(issue.iv==null){
                fv = issue.fv.index;
                ov = issue.ov.index;
                index = (int) (fv - (fv - ov)*pGlobal);
                if(index<=0)
                    issue.iv = releaseList.get(0);
                else if (index >= releaseList.size()) {
                    issue.iv = releaseList.get(releaseList.size()-1);
                }else{
                    issue.iv = releaseList.get(index);
                }
            }
        }

        //I will create N - 1 csv for testing releases
        boolean ret;
        ExcelTools excelTools;
        ArffFileCreator arffFileCreator = new ArffFileCreator();

        //Labeling java files for testing set in walk-forward
        List<Commit> commits;
        List<MyFile> myFiles;
        int n = 0;
        for (Release release : releaseList) {
            if (n > nRelease)
                continue;
            myFiles = hashMapReleaseFiles.get(release);
            for (Issue issue : issueList) {
                //I use all information to labeling java classes
                if ((release.getDate().compareTo(issue.iv.getDate()) >= 0) && (release.getDate().compareTo(issue.fv.getDate()) < 0)) {
                    commits = hashMapIssueCommits.get(issue);
                    for (Commit commit : commits) {
                        if (commit.date.compareTo(release.getDate()) > 0) {
                            for (MyFile myFile : myFiles) {
                                for(String file: commit.files){
                                    if(myFile.path.contains(file) && myFile.state!= MyFile.StateFile.BUG){
                                            myFile.state = MyFile.StateFile.BUG;
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(n!=0) {
                //Create Excel
                excelTools = new ExcelTools("ISW2", PROJECT, "testing_" + release.name);
                ret = excelTools.createTable();
                if (!ret)
                    System.exit(-3);
                ret = excelTools.writeSingleRelease(hashMapReleaseFiles.get(release));
                if (!ret)
                    System.exit(-4);
            }
            n++;
        }

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

        //I delete the IV value for all issues that have an inconsistent AV because I will have to do the labeling again
        for(Issue issue: issueList){
            if(issue.changedIV)
                issue.iv = null;
        }

        //I reset the state of java files in all releases
        int h = 0;
        for(Release release: releaseList){
            if(h>nRelease)
                break;
            for(MyFile myFile: hashMapReleaseFiles.get(release)){
                if(myFile.state== MyFile.StateFile.BUG)
                    myFile.state = MyFile.StateFile.NO_BUG;
            }
            h++;
        }

        //I do the labeling of the training sets that I will use in the walk-forward. I use all information in the training set
        List<Float> pSubGlobals = proportion.incrementTrainingSet();

        int count1;
        float pSubGlobal;
        Release youngerRelease;
        int countPSubGlobal = 0;

        for(count1=1; count1<=nRelease; count1++){
            if(count1==1){
                youngerRelease = releaseList.get(0);
                //Get the younger release in the training set
                excelTools = new ExcelTools("ISW2", PROJECT, count1 + MACRO1 + youngerRelease.name);
                ret = excelTools.createTable();
                if(!ret)
                    System.exit(-6);
                ret = excelTools.writeSingleRelease(hashMapReleaseFiles.get(youngerRelease));
                if(!ret)
                    System.exit(-7);
                arffFileCreator.createArffFileTrainingSet(hashMapReleaseFiles,releaseList,MACRO1 + PROJECT ,youngerRelease);
                continue;
            }

            pSubGlobal = pSubGlobals.get(countPSubGlobal);
            countPSubGlobal++;
            //Younger Release in the training set
            youngerRelease = releaseList.get(count1 - 1);

            //Compute IV of issue that has no consistent AV on JIRA fixed in the training set
            for(Issue issue: issueList){
                if(issue.fv.date.compareTo(youngerRelease.date)<=0 && issue.iv==null){
                    fv = issue.fv.index;
                    ov = issue.ov.index;
                    index = (int) (fv - (fv - ov)*pSubGlobal);
                    if(index<=0)
                        issue.iv = releaseList.get(0);
                    else if (index >= count1 - 1) {
                        issue.iv = releaseList.get(count1 - 1);
                    }else{
                        issue.iv = releaseList.get(index);
                    }
                }
            }

            //labeling java classes of training set releases
            for(Release release: releaseList){
                //there are not two different releases with same date
                if(release.date.compareTo(youngerRelease.date)>0)
                    break;
                myFiles = hashMapReleaseFiles.get(release);
                for(Issue issue: issueList){
                    //I don't use future issue
                    if(issue.fv.date.compareTo(youngerRelease.date)<=0 && (release.getDate().compareTo(issue.iv.getDate()) >= 0)
                            && (release.getDate().compareTo(issue.fv.getDate()) < 0)){
                        commits = hashMapIssueCommits.get(issue);
                        for (Commit commit : commits) {
                            if (commit.date.compareTo(release.getDate()) > 0) {
                                for (MyFile myFile : myFiles) {
                                    for(String file: commit.files){
                                        if(myFile.path.contains(file) && myFile.state!= MyFile.StateFile.BUG){

                                            myFile.state = MyFile.StateFile.BUG;
                                            break;

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //all java classes in the training set releases have been labeled
            excelTools = new ExcelTools("ISW2", PROJECT, count1 + MACRO1 + youngerRelease.name);
            ret = excelTools.createTable();
            if(!ret)
                System.exit(-8);
            for(Release release1: releaseList){
                if(release1.date.compareTo(youngerRelease.date)<=0){
                    ret = excelTools.writeSingleRelease(hashMapReleaseFiles.get(release1));
                    if(!ret)
                        System.exit(-9);
                }
            }

            ret = arffFileCreator.createArffFileTrainingSet(hashMapReleaseFiles, releaseList, MACRO1+PROJECT, youngerRelease);
            if(!ret)
                System.exit(-12);

            //I will label with another sub global P because the training set changes, and I have new issues
            for(Issue issue: issueList){
                if(issue.changedIV)
                    issue.iv = null;
            }

            int k = 0;
            for(Release release: releaseList){
                if(k>nRelease)
                    break;
                for(MyFile myFile: hashMapReleaseFiles.get(release)){
                    if(myFile.state== MyFile.StateFile.BUG)
                        myFile.state = MyFile.StateFile.NO_BUG;
                }
                k++;
            }
        }

        WalkForward walkForward = new WalkForward();
        walkForward.executeWalkForward(releaseList, PROJECT);

    }
}
