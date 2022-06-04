package org.capotombolo.flow;

import org.capotombolo.filesystem.FoundAllJavaFiles;
import org.capotombolo.git.GitSkills;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Execution {

    public static void getAllFileRelease(String path, HashMap<Release, List<MyFile>> hashMapReleaseFiles, int nRelease, List<Release> releaseList) throws IOException {
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
