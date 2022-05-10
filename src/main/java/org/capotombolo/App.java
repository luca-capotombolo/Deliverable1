package org.capotombolo;

import org.capotombolo.filesystem.FoundAllJavaFiles;
import org.capotombolo.git.GitSkills;
import org.capotombolo.jira.Jira;
import org.capotombolo.metrics.Size;
import org.capotombolo.proportion.Proportion;
import org.capotombolo.utils.Commit;
import org.capotombolo.utils.Issue;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class App 
{

    public static void main(String[] args) throws IOException, GitAPIException {

        final String project = "ZOOKEEPER";
        final String path = "C:\\Users\\lucac\\ESAME FALESSI\\zookeeper2\\zookeeper";

        //Get All Releases from JIRA
        List<Release> releaseList = Jira.getReleases(project);

        int nRelease = releaseList.size() / 2;                                          //release Excel

        //Get All Bugs from JIRA
        List<Issue> issueList = Jira.getBugs(project, releaseList);


        GitSkills gitSkills = new GitSkills(path);
        FoundAllJavaFiles foundAllJavaFiles;
        int count = 0;
        List<MyFile> filenames;
        HashMap<Release, List<MyFile>> hashMapReleaseFiles = new HashMap<>();           //{Release1: [file1, ..., fileN], Release2: [file1, ..., fileM], ...}
        HashMap<Issue, List<Commit>> hashMapIssueCommits;                               //{Issue1: [commit1, ..., commitN], Issue2: [commit1, ..., commitM], ...}

        for (Release release : releaseList) {
            try{
                gitSkills.setBranch(release.getRelease());
            }catch (RefNotFoundException e){
                //inconsistent data on JIRA
                continue;
            }
            if (count <= nRelease) {
                foundAllJavaFiles = new FoundAllJavaFiles(path);
                filenames = foundAllJavaFiles.foundAllFiles();                          //all java files in the release project
                hashMapReleaseFiles.put(release, filenames);
            }
            count++;
        }

        hashMapIssueCommits = gitSkills.classifyCommitsIssue(issueList, releaseList);

        //I must order the issues by fixDate
        issueList.sort(Comparator.comparing(o -> o.fixDate));

        //Proportion proportion = new Proportion(releaseList, issueList);

        //proportion.increment();


/*
        //Labeling java files
        List<Commit> commits;
        List<MyFile> myFiles;
        for(Release release: releaseList){
            for(Issue issue: issueList){
                if((release.getDate().compareTo(issue.iv.getDate()) >= 0) && (release.getDate().compareTo(issue.fv.getDate()) < 0)){
                    commits = hashMapIssueCommits.get(issue);
                    for(Commit commit: commits){
                        if(commit.date.compareTo(release.getDate()) > 0){
                            myFiles = hashMapReleaseFiles.get(release);
                            for(MyFile myFile: myFiles){
                                if(commit.files.contains(myFile.path)){
                                    myFile.state = MyFile.StateFile.BUG;
                                }
                            }
                        }
                    }
                }
            }
        }*/



        //Compute metrics
        //Size size = new Size(hashMapReleaseFiles, path);


        //Scrittura dei dati su Excel








    }
}
