package org.capotombolo.jira;


import org.capotombolo.utils.Issue;
import org.capotombolo.utils.Release;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.io.IOException;
import java.util.*;


@RunWith(Parameterized.class)
public class TestJira  {

    private final String project;
    private final String path;


    @Parameterized.Parameters
    public static Collection<Object[]> setParameters() {

        return Arrays.asList(new Object[][]{
                {"BOOKKEEPER", "C:\\Users\\lucac\\ESAME FALESSI\\bookkeeper"},
                {"ZOOKEEPER", "C:\\Users\\lucac\\ESAME FALESSI\\zookeeper"}
        });
    }

    public TestJira(String project, String path){
        this.project = project;
        this.path = path;
    }

    @Test
    public void checkOV() throws IOException {
        boolean check = true;
        List<Release> releaseList = Jira.getReleases(this.project);
        List<Issue> issueList = Jira.getBugs(this.project, releaseList);
        for(Issue issue: issueList){
            if(issue.ov==null){
                check=false;
                break;
            }
        }
        Assert.assertTrue(check);
    }

    @Test
    public void checkIVFV() throws IOException {
        boolean check = true;
        List<Release> releaseList = Jira.getReleases(this.project);
        List<Issue> issueList = Jira.getBugs(this.project, releaseList);
        for(Issue issue: issueList){
            if(!issue.av.isEmpty()) {
                if (issue.iv != null){
                    if (issue.iv.getDate().compareTo(issue.fv.getDate()) >= 0) {
                        check = false;
                        break;
                    }
                }
            }
        }
        Assert.assertTrue(check);
    }

    @Test
    public void checkOVFV() throws IOException {
        boolean check = true;
        List<Release> releaseList = Jira.getReleases(this.project);
        List<Issue> issueList = Jira.getBugs(this.project, releaseList);
        for(Issue issue: issueList){
            if (issue.ov.getDate().compareTo(issue.fv.getDate()) > 0) {
                check = false;
                break;
            }
        }
        Assert.assertTrue(check);
    }

    @Test
    public void checkDateRelease() throws IOException {
        boolean check = true;
        List<Release> releaseList = Jira.getReleases(this.project);
        for(Release release: releaseList){
            if(release.getDate() == null){
                check = false;
                break;
            }
        }
        Assert.assertTrue(check);
    }

    @Test
    public void checkOrderReleaseList() throws IOException {
        boolean check = true;
        Release release;
        int i = 0, ret;
        List<Release> releaseList = Jira.getReleases(this.project);
        release = releaseList.get(0);
        for (Release release1: releaseList){
            if(i==0){
                i++;
                continue;
            }
            ret = release1.getDate().compareTo(release.getDate());
            if(ret<=0){
                check=false;
                break;
            }
            release=release1;
            i++;
        }
        Assert.assertTrue(check);
    }


    @Test
    public void checkFVOfIssue() throws IOException {
        boolean check = true;
        List<Release> releaseList = Jira.getReleases(this.project);
        List<Issue> issueList = Jira.getBugs(this.project, releaseList);
        for(Issue issue: issueList){
            if(issue.fv==null){
                check = false;
                break;
            }
        }
        Assert.assertTrue(check);
    }

}
