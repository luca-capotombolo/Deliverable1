package org.capotombolo.flow;

import org.capotombolo.filesystem.FoundAllJavaFiles;
import org.capotombolo.git.GitSkills;
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
