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
        float pGlobal = 0;
        float pIssue;
        int total = 0;
        int fv;
        int iv;
        int ov;

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
            pIssue = (fv - iv)/(float)(fv - ov);
            pGlobal += pIssue;
            total ++;
        }
        try {
            pGlobal = pGlobal / total;
        }catch (Exception e){
            e.printStackTrace();
        }
        return pGlobal;
    }

    public List<Float> incrementIteration() throws IOException {
        List<Float> pReleasesIncrementIteration = new ArrayList<>();
        float pRelease;
        float pIssue;
        float pColdStart = 0;
        int total;
        int fv;
        int iv;
        int ov;
        List<Issue> issueFixedInPrevVersions;
        int count = 0;

        for(Release release: releaseList){
            pRelease = 0;
            if(count==0){
                //I can not compute P0 in increment iteration. I discard all no post release defects
                count++;
                continue;
            }
            if(count==1){
                //I have discarded all no post release defects
                pColdStart = coldStart();
                pRelease = pColdStart;                               //P1
                pReleasesIncrementIteration.add(pRelease);
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
                pRelease = pColdStart;
                pReleasesIncrementIteration.add(pRelease);
                count++;
                continue;
            }
            total = 0;
            for(Issue issue: issueFixedInPrevVersions){
                iv = issue.iv.index;
                fv = issue.fv.index;
                ov = issue.ov.index;
                pIssue = (fv - iv)/(float)(fv - ov);
                pRelease += pIssue;
                total ++;
            }
            try {
                pRelease = pRelease / total;
            }catch (Exception e){
                e.printStackTrace();
            }
            pReleasesIncrementIteration.add(pRelease);
            count++;
        }

        return pReleasesIncrementIteration;
    }

    public List<Float> incrementTrainingSet() throws IOException {
        List<Float> pSubGlobals = new ArrayList<>();
        boolean coldStartDone = false;
        float pColdStart = 0;
        //count + 1 = total number of releases TRAINING SET + TESTING SET
        int count;
        int total;
        float pIssue;
        float pSubGlobal;
        float iv;
        float fv;
        float ov;
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
            pSubGlobal = 0;
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
                    pColdStart = coldStart();
                    coldStartDone = true;
                }
                pSubGlobal = pColdStart;
                pSubGlobals.add(pSubGlobal);
                continue;
            }

            for(Issue issue: issueWithFVInTrainingSet){
                iv = issue.iv.index;
                fv = issue.fv.index;
                ov = issue.ov.index;
                pIssue = (fv - iv)/(fv - ov);
                pSubGlobal += pIssue;
                total ++;
            }
            try {
                pSubGlobal = pSubGlobal / total;
            }catch (Exception e){
                e.printStackTrace();
            }
            pSubGlobals.add(pSubGlobal);
        }

        return pSubGlobals;
    }

    public Float coldStart() throws IOException {
        float pColdStartProject;
        float pColdStart;
        float pIssue;
        float iv;
        float fv;
        float ov;
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
            pColdStartProject = 0;
            releaseList1 = Jira.getReleases(project);
            issues = Jira.getBugs(project, releaseList1);
            for(Issue issue: issues){
                if(issue.iv!=null){
                    //issue with consistent AV on JIRA
                    iv = issue.iv.index;
                    fv = issue.fv.index;
                    ov = issue.ov.index;
                    pIssue = (fv - iv)/(fv - ov);
                    pColdStartProject += pIssue;
                    total ++;
                }
            }
            if(total >= 5){
                try {
                    pColdStartProject = pColdStartProject / total;      //Average P of the project
                }catch (Exception e){
                    e.printStackTrace();
                }
                p_projects.add(pColdStartProject);
            }
        }

        int n = p_projects.size();
        p_projects.sort(Comparator.comparing(o -> o));

        if(n%2 == 0){
            pColdStart = p_projects.get((n+1)/2);
        }else{
            pColdStart = (p_projects.get(n/2) + p_projects.get((n/2) + 1))/2;
        }
        return pColdStart;
    }
}
