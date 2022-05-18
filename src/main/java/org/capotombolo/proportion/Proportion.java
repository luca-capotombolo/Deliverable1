package org.capotombolo.proportion;

import org.capotombolo.jira.Jira;
import org.capotombolo.utils.Issue;
import org.capotombolo.utils.Release;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Proportion {

    public final List<Release> releaseList;
    public final List<Issue> issueList;
    public List<Issue> issueListWithAVInconsistent;
    public List<Issue> issueListWithAVConsistent;

    public Proportion(List<Release> releaseList, List<Issue> issueList){
        this.releaseList = releaseList;
        this.issueList = issueList;
        this.issueListWithAVInconsistent = new ArrayList<>();
        this.issueListWithAVConsistent = new ArrayList<>();
    }

    public float globalProportion(){
        float p_global = 0;
        float p_issue;
        int total = 0, fv, iv, ov;

        for(Issue issue: issueList){
            if(issue.iv==null){
                //inconsistent AV on JIRA
                issueListWithAVInconsistent.add(issue);
                continue;
            }
            issueListWithAVConsistent.add(issue);
            iv = issue.iv.index;
            fv = issue.fv.index;
            ov = issue.ov.index;
            p_issue = (fv - iv)/(float)(fv - ov);
            p_global += p_issue;
            total ++;
        }
        p_global = p_global / (float) total;
        return p_global;
    }

    public List<Float> increment_iteration() throws IOException {
        List<Float> p_releases_increment_iteration = new ArrayList<>();
        float p_release, p_issue;
        float p_cold_start = 0;
        int total, fv, iv, ov;
        List<Issue> issueFixedInPrevVersions;
        int count = 0;

        //System.out.println("-------------------- INCREMENT ITERATION ----------------------");
        for(Release release: releaseList){
            p_release = 0;
            if(count==0){
                //I can not compute P0 in increment iteration. I discard all no post release defects
                count++;
                continue;
            }
            if(count==1){
                //I have discarded all no post release defects
                p_cold_start = coldStart();
                p_release = p_cold_start;                               //P1
                p_releases_increment_iteration.add(p_release);
                count++;
                continue;
            }
            issueFixedInPrevVersions = new ArrayList<>();

            for(Issue issue: issueListWithAVConsistent){
                if(issue.fv.getDate().compareTo(release.getDate()) < 0){
                    issueFixedInPrevVersions.add(issue);
                }
            }

            if(issueFixedInPrevVersions.size()<5){
                p_release = p_cold_start;
                p_releases_increment_iteration.add(p_release);
                count++;
                continue;
            }
            total = 0;
            for(Issue issue: issueFixedInPrevVersions){
                iv = issue.iv.index;
                fv = issue.fv.index;
                ov = issue.ov.index;
                p_issue = (fv - iv)/(float)(fv - ov);
                p_release += p_issue;
                total ++;
            }
            p_release = p_release /(float) total;
            p_releases_increment_iteration.add(p_release);
            count++;
        }

        return p_releases_increment_iteration;
    }

    public List<Float> increment_training_set() throws IOException {
        List<Float> p_sub_globals = new ArrayList<>();
        boolean coldStartDone = false;
        float p_cold_start = 0;
        //count + 1 = total number of releases TRAINING SET + TESTING SET
        int count, total;
        float p_issue, p_subGlobal, iv, fv, ov;
        Release release;
        List<Issue> issueWithFVInTrainingSet;


        // releaseList.size()/2 == number of releases wrote on Excel
        for(count = 0; count <= releaseList.size()/2; count ++){
            if(count == 0 || count == 1){
                //[1] - [1;2]
                count++;
                continue;
            }
            issueWithFVInTrainingSet = new ArrayList<>();
            p_subGlobal = 0;
            total = 0;
            //Bookkeeper --> [1,2;3] - [1,2,3;4] - [1,2,3,4;5] - [1,2,3,4,5;6]
            //Get the younger release in the training set
            release = releaseList.get(count-1);
            for(Issue issue: issueListWithAVConsistent){
                if(issue.fv.getDate().compareTo(release.getDate())<=0){
                    issueWithFVInTrainingSet.add(issue);
                }
            }

            if(issueWithFVInTrainingSet.size()<5){
                //I can not compute P of this training set, so I have to use Cold Start
                if(!coldStartDone){
                    p_cold_start = coldStart();
                    coldStartDone = true;
                }
                p_subGlobal = p_cold_start;
                p_sub_globals.add(p_subGlobal);
                continue;
            }

            for(Issue issue: issueWithFVInTrainingSet){
                iv = issue.iv.index;
                fv = issue.fv.index;
                ov = issue.ov.index;
                p_issue = (fv - iv)/(fv - ov);
                p_subGlobal += p_issue;
                total ++;
            }
            p_subGlobal = p_subGlobal / (float) total;
            p_sub_globals.add(p_subGlobal);
        }

        return p_sub_globals;
    }

    public Float coldStart() throws IOException {
        float p_cold_start_project, p_cold_start, p_issue, iv, fv, ov;
        int total;
        List<String> projects = new ArrayList<>();
        projects.add("AVRO");
        projects.add("OPENJPA");
        projects.add("STORM");
        projects.add("SYNCOPE");
        projects.add("TAJO");
        List<Float> p_projects = new ArrayList<>();
        List<Release> releaseList1;
        List<Issue> issues;

        for(String project: projects){
            total = 0;
            p_cold_start_project = 0;
            releaseList1 = Jira.getReleases(project);
            issues = Jira.getBugs(project, releaseList1);
            for(Issue issue: issues){
                if(issue.iv!=null){
                    //issue with consistent AV on JIRA
                    iv = issue.iv.index;
                    fv = issue.fv.index;
                    ov = issue.ov.index;
                    p_issue = (fv - iv)/(fv - ov);
                    p_cold_start_project += p_issue;
                    total ++;
                }
            }
            if(total >= 5){
                p_cold_start_project = p_cold_start_project / total;                //Average P of the project
                p_projects.add(p_cold_start_project);
            }
        }

        int n = p_projects.size();
        p_projects.sort(Comparator.comparing(o -> o));

        if(n%2 == 0){
            p_cold_start = p_projects.get((n+1)/2);
        }else{
            p_cold_start = (p_projects.get(n/2) + p_projects.get((n/2) + 1))/2;
        }
        return p_cold_start;
    }
}
