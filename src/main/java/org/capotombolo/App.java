package org.capotombolo;

import org.capotombolo.excel.ExcelTools;
import org.capotombolo.filesystem.FoundAllJavaFiles;
import org.capotombolo.git.GitSkills;
import org.capotombolo.jira.Jira;
import org.capotombolo.proportion.Proportion;
import org.capotombolo.utils.Commit;
import org.capotombolo.utils.Issue;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class App 
{

    public static void main(String[] args) throws IOException, GitAPIException {

        final String project = "BOOKKEEPER";
        final String path = "C:\\Users\\lucac\\ESAME FALESSI\\bookkeeper";

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
            try {
                gitSkills.setBranch(release.getRelease());
            } catch (RefNotFoundException e) {
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

        Proportion proportion = new Proportion(releaseList, issueList);

        proportion.increment();

        //Labeling java files
        List<Commit> commits;
        List<MyFile> myFiles;
        int n = 0, s=0;
        for (Release release : releaseList) {
            System.out.println(release.getRelease());
            if (n == nRelease)
                break;
            for (Issue issue : issueList) {
                if ((release.getDate().compareTo(issue.iv.getDate()) >= 0) && (release.getDate().compareTo(issue.fv.getDate()) < 0)) {
                    commits = hashMapIssueCommits.get(issue);
                    for (Commit commit : commits) {
                        if (commit.date.compareTo(release.getDate()) > 0) {
                            myFiles = hashMapReleaseFiles.get(release);
                            for (MyFile myFile : myFiles) {
                                for(String file: commit.files){
                                    //System.out.println(file);
                                    if(myFile.path.contains(file)){
                                        if(myFile.state!= MyFile.StateFile.BUG) {
                                            myFile.state = MyFile.StateFile.BUG;
                                            s++;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            n++;
        }

        System.out.println(s);

        //Compute metrics
        //Size size = new Size(hashMapReleaseFiles, path);

        ExcelTools excelTools = new ExcelTools("ISW2", project, "Release", "Name of the class", "Bugginess");
        boolean ret = excelTools.createTable();
        if (!ret) {
            System.out.println("Si è verificato un errore nella creazione della tabella...");
            System.exit(-1);
        } else {
            System.out.println("La tabella è stata creata con successo...");
        }
        ret = excelTools.writeData(hashMapReleaseFiles);
        if (ret){
            System.out.println("modifica file excel avvenuta con successo...");
        }
    }
}
