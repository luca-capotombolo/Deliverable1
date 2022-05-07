package org.capotombolo;

import org.capotombolo.filesystem.FoundAllJavaFiles;
import org.capotombolo.git.GitSkills;
import org.capotombolo.jira.Jira;
import org.capotombolo.utils.Commit;
import org.capotombolo.utils.Issue;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.io.*;
import java.util.HashMap;
import java.util.List;


public class App 
{

    public static void main(String[] args) throws IOException, GitAPIException {

        final String project = "BOOKKEEPER";
        final String path = "C:\\Users\\lucac\\ESAME FALESSI\\bookkeeper";

        //Get All Releases from JIRA
        List<Release> releaseList = Jira.getReleases(project);
        for (Release release : releaseList) {
            System.out.println("Release: " + release.getRelease());
            System.out.println("Date: " + release.getDate() + "\n\n\n\n");
        }


        int nRelease = releaseList.size() / 2;

        System.out.println("Numero versioni che vengono considerate: " + nRelease);


        //Get All Bugs from JIRA
        List<Issue> issueList = Jira.getBugs(project);

        //Per vedere le FVs delle release
        /*
        int i = 0;
        for (Issue issue: issueList){
            System.out.println("ISSUE: "+issue.key);
            System.out.println("IV: "+issue.iv);
            if(issue.fvs.size()==0){
                i++;
                continue;
            }
            System.out.println("FVs:");
            for(Release release: issue.fvs){
                System.out.println(release.getRelease());
            }
        }
        System.out.println(i);*/


        /*
        int i = 0;
        for(Issue issue: issueList){
            System.out.println("KEY: "+issue.key);
            System.out.println("Fixed releases:");
            for (int p=0; p<issue.fvs.size(); p++){
                System.out.println("Release: " + issue.fvs.get(p).getRelease());
                System.out.println("Release Date: " + issue.fvs.get(p).getDate());
            }
            System.out.println("\n\n");
            i++;
        }
        System.out.println(i);*/

        GitSkills gitSkills = new GitSkills(path);
        FoundAllJavaFiles foundAllJavaFiles;
        int count = 0;
        List<MyFile> filenames;
        HashMap<Release, List<MyFile>> hashMapReleaseFiles = new HashMap<>();           //{Release1: [file1, ..., fileN], Release2: [file1, ..., fileM], ...}
        HashMap<Issue, List<Commit>> hashMapIssueCommits;             //{Issue1: [commit1, ..., commitN], Issue2: [commit1, ..., commitM], ...}

        for (Release release : releaseList) {
            gitSkills.setBranch(release.getRelease());
            if (count <= nRelease) {
                foundAllJavaFiles = new FoundAllJavaFiles(path);
                filenames = foundAllJavaFiles.foundAllFiles();                          //all java files in the release project
                hashMapReleaseFiles.put(release, filenames);
            }
            count++;
        }

        hashMapIssueCommits = gitSkills.classifyCommitsIssue(issueList, releaseList);


        System.out.println(hashMapIssueCommits.size());
        for(Issue issue: issueList){

            List<Commit> commits = hashMapIssueCommits.get(issue);
            if(commits.size()==0){
                continue;
            }
            System.out.println("--------------------------------------------------------------------------------------");
            System.out.println("ISSUE = "+issue.key);
            for(Commit commit: commits){

                System.out.println("****************************************************");
                System.out.println("Data commit: " + commit.date);
                if(commit.release != null) {
                    System.out.println("Release: " + commit.release.getRelease());
                    System.out.println("Date release: " + commit.release.date);
                }
                else
                    System.out.println("Date release: null");
                System.out.println("****************************************************");
            }
        }




        /*
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

        //Scrittura dei dati su Excel


    }
}
