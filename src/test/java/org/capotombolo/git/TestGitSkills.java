package org.capotombolo.git;

import org.capotombolo.jira.Jira;
import org.capotombolo.utils.Commit;
import org.capotombolo.utils.Issue;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.errors.GitAPIException;
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
public class TestGitSkills  {

    private final String project;
    private final String path;


    @Parameterized.Parameters
    public static Collection<Object[]> setParameters() {

        return Arrays.asList(new Object[][]{
                {"BOOKKEEPER", "C:\\Users\\lucac\\ESAME FALESSI\\bookkeeper"},
                {"ZOOKEEPER","C:\\Users\\lucac\\ESAME FALESSI\\zookeeper2\\zookeeper"}
        });
    }

    public TestGitSkills(String project, String path){
        this.project = project;
        this.path = path;
    }

    @Test
    public void checkDateCommitDateRelease() throws IOException, GitAPIException {
        List<Release> releaseList = Jira.getReleases(this.project);
        List<Issue> issueList = Jira.getBugs(this.project, releaseList);
        GitSkills gitSkills = new GitSkills(this.path);

        boolean check = true;

        HashMap<Issue, List<Commit>> hashMapIssueCommits = gitSkills.classifyCommitsIssue(issueList, releaseList);

        for(Issue issue: issueList){
            List<Commit> commits = hashMapIssueCommits.get(issue);

            for(Commit commit: commits){
                if(commit.date.compareTo(commit.release.getDate()) >= 0){
                    check = false;
                    break;
                }
            }
        }

        Assert.assertTrue(check);
    }

}