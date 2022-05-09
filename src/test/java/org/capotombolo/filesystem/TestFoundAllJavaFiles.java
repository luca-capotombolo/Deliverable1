package org.capotombolo.filesystem;

import org.capotombolo.git.GitSkills;
import org.capotombolo.jira.Jira;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


@RunWith(Parameterized.class)
public class TestFoundAllJavaFiles {

    private final String path;
    private final String project;


    @Parameterized.Parameters
    public static Collection<Object[]> setParameters() {

        return Arrays.asList(new Object[][]{
                {"BOOKKEEPER", "C:\\Users\\lucac\\ESAME FALESSI\\bookkeeper"},
                {"ZOOKEEPER", "C:\\Users\\lucac\\ESAME FALESSI\\zookeeper2\\zookeeper"}
        });
    }

    public TestFoundAllJavaFiles(String project, String path){
        this.project=project;
        this.path = path;
    }


    @Test
    public void checkNumberJavaFilesOfRelease() throws IOException {
        boolean check=true;
        List<Release> releaseList = Jira.getReleases(project);                          //get all releases
        GitSkills gitSkills = new GitSkills(this.path);
        int count = 0;
        int nRelease = releaseList.size() / 2;                                          //get java files of only these release
        FoundAllJavaFiles foundAllJavaFiles;
        List<MyFile> filenames;
        HashMap<Release, List<MyFile>> hashMapReleaseFiles = new HashMap<>();           //{Release1: [file1, ..., fileN], Release2: [file1, ..., fileM], ...}

        for (Release release : releaseList) {
            if (count <= nRelease) {
                try{
                    gitSkills.setBranch(release.getRelease());
                }catch (RefNotFoundException e){
                    e.printStackTrace();
                    //inconsistent data on JIRA
                    continue;
                } catch (GitAPIException e) {
                    e.printStackTrace();
                    continue;
                }
                count++;
                foundAllJavaFiles = new FoundAllJavaFiles(path);
                filenames = foundAllJavaFiles.foundAllFiles();                          //all java files in the release project
                hashMapReleaseFiles.put(release, filenames);
            }
        }
        for(int i=0; i < hashMapReleaseFiles.size(); i++){
            List<MyFile> myFiles = hashMapReleaseFiles.get(releaseList.get(i));
            if(myFiles.isEmpty()){
                check = false;
                break;
            }
        }
        Assert.assertTrue(check);
    }

}
