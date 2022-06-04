package org.capotombolo.flow;

import org.capotombolo.excel.ExcelTools;
import org.capotombolo.filesystem.FoundAllJavaFiles;
import org.capotombolo.git.GitSkills;
import org.capotombolo.utils.Commit;
import org.capotombolo.utils.Issue;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Execution {

    private Execution(){

    }

    public static void clean(List<Issue> issueList, List<Release> releaseList, int nRelease, Map<Release, List<MyFile>> hashMapReleaseFiles){
        //I delete the IV value for all issues that have an inconsistent AV because I will have to do the labeling again
        for(Issue issue: issueList){
            if(issue.isChangedIV())
                issue.setIv(null);
        }

        //I reset the state of java files in all releases
        int h = 0;
        for(Release release: releaseList){
            if(h>nRelease)
                break;
            for(MyFile myFile: hashMapReleaseFiles.get(release)){
                if(myFile.getState() == MyFile.StateFile.BUG)
                    myFile.setState(MyFile.StateFile.NO_BUG);
            }
            h++;
        }
    }

    public static void labelingTestingSets(List<Release> releaseList, int nRelease, Map<Release, List<MyFile>> hashMapReleaseFiles,
                                           List<Issue> issueList, Map<Issue, List<Commit>>hashMapIssueCommits, String project){
        List<MyFile> myFiles;
        ExcelTools excelTools;
        boolean ret;
        int n = 0;

        for (Release release : releaseList) {
            if (n > nRelease)
                continue;
            myFiles = hashMapReleaseFiles.get(release);
            labelingFilesReleaseIssuesTesting(issueList, release, hashMapIssueCommits,myFiles);
            if(n!=0) {
                //Create Excel
                excelTools = new ExcelTools("ISW2", project, "testing_" + release.name);
                ret = excelTools.createTable();
                if (!ret)
                    System.exit(-3);
                ret = excelTools.writeSingleRelease(hashMapReleaseFiles.get(release));
                if (!ret)
                    System.exit(-4);
            }
            n++;
        }
    }

    private static void labelingFilesReleaseIssuesTesting(List<Issue> issueList, Release release, Map<Issue, List<Commit>>hashMapIssueCommits, List<MyFile> myFiles){
        List<Commit> commits;

        for (Issue issue : issueList) {
            //I use all information to labeling java classes
            if ((release.getDate().compareTo(issue.getIv().getDate()) >= 0) && (release.getDate().compareTo(issue.fv.getDate()) < 0)) {
                commits = hashMapIssueCommits.get(issue);
                for (Commit commit : commits) {
                    if (commit.date.compareTo(release.getDate()) > 0) {
                        labelingFilesCommit(myFiles, commit);
                    }
                }
            }
        }
    }

    private static void labelingFilesCommit(List<MyFile> myFiles, Commit commit){
        for (MyFile myFile : myFiles) {
            for(String file: commit.files){
                if(myFile.path.contains(file) && myFile.getState() != MyFile.StateFile.BUG){
                    myFile.setState(MyFile.StateFile.BUG);
                    break;
                }
            }
        }
    }

    public static void computeIVLabelingTestingGlobalProportion(List<Issue> issueList, List<Release> releaseList, float pGlobal){
        float fv;
        float ov;
        int index;

        for(Issue issue: issueList){
            if(issue.getIv() ==null){
                fv = issue.fv.getIndex();
                ov = issue.ov.getIndex();
                index = (int) (fv - (fv - ov)*pGlobal);
                if(index<=0)
                    issue.setIv(releaseList.get(0));
                else if (index >= releaseList.size()) {
                    issue.setIv(releaseList.get(releaseList.size() - 1));
                }else{
                    issue.setIv(releaseList.get(index));
                }
            }
        }
    }

    public static void getAllFileRelease(String path, Map<Release, List<MyFile>> hashMapReleaseFiles, int nRelease, List<Release> releaseList) throws IOException {
        List<MyFile> filenames;
        GitSkills gitSkills = new GitSkills(path);
        int count = 0;
        FoundAllJavaFiles foundAllJavaFiles;

        for (Release release : releaseList) {
            try {
                gitSkills.setBranch(release.getName());
            } catch (GitAPIException e) {
                //inconsistent data on JIRA
                continue;
            }
            if (count <= nRelease) {
                foundAllJavaFiles = new FoundAllJavaFiles(path);
                //all java files in the release
                filenames = foundAllJavaFiles.foundAllFiles();
                hashMapReleaseFiles.put(release, filenames);
            }
            count++;
        }
    }
}
