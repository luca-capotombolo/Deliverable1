package org.capotombolo;

import org.capotombolo.excel.ExcelTools;
import org.capotombolo.filesystem.FoundAllJavaFiles;
import org.capotombolo.git.GitSkills;
import org.capotombolo.jira.Jira;
import org.capotombolo.utils.Commit;
import org.capotombolo.utils.Issue;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.*;
import java.util.ArrayList;
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

        //fino a qua funziona


        GitSkills gitSkills = new GitSkills(path);
        FoundAllJavaFiles foundAllJavaFiles;
        //ExcelTools excelTools;
        //excelTools = new ExcelTools("ISW2", project, "Release", "Name of the class", "Bugginess");
        //excelTools.createTable();
        boolean ret;
        int count = 0;
        List<Commit> totCommitWithKeys = new ArrayList<>();             //Commit with a Key issues within comment
        List<String> filenames;
        HashMap<Release, List<String>> hashMap = new HashMap<>();

        //Foreach release, insert data into excel
        for (Release release : releaseList) {
            //Mi posiziono sulla release corretta
            gitSkills.setBranch(release.getRelease());
            //costruisco il file excel
            if (count <= nRelease) {
                foundAllJavaFiles = new FoundAllJavaFiles(path);
                filenames = foundAllJavaFiles.foundAllFiles();             //all java files in the project
                hashMap.put(release, filenames);
                //ret = excelTools.writeData(filenames, release.getRelease());
                //if(!ret){
                //    System.out.println("Si è verificato un errore nella scrittura sul file Excel...");
                //    return;
                //}
            }
            //cerco i commit relativi ai bug
            //for(String key: keys){
            for (Issue issue : issueList)
                totCommitWithKeys.addAll(gitSkills.searchCommitByKeyBug(issue.key, release));
        }
        count++;


        for (Commit commit : totCommitWithKeys) {
            System.out.println("Commento: " + commit.comment);
            System.out.println("SHA: " + commit.sha);
            System.out.println("Release a cui il commit apprtiene: " + commit.release.getRelease());
            System.out.println("Data del commit: " + commit.date);
            System.out.println("Data della release: " + commit.release.getDate());
            System.out.println("File modificati: " + commit.files);
        }


    }
}
