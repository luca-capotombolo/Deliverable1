package org.capotombolo.jira;

import org.capotombolo.utils.Issue;
import org.capotombolo.utils.Release;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


@RunWith(Parameterized.class)
public class TestJira  {

    private final String project;


    @Parameterized.Parameters
    public static Collection<Object[]> setParameters() {

        return Arrays.asList(new Object[][]{
                {"BOOKKEEPER"},
                {"ZOOKEEPER"}
        });
    }

    public TestJira(String project){
        this.project = project;
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

    /*
    @Test
    public void checkFVsOfIssue() throws IOException {
        boolean check = true;
        List<Issue> issueList = Jira.getBugs(this.project);
        for(Issue issue: issueList){
            if(issue.fvs==null){
                check = false;
                break;
            }
        }
        Assert.assertTrue(check);
    }*/

}
