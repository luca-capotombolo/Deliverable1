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
    public final List<Issue> issueListWithAVInconsistent;
    private final List<Issue> issueListWithAVConsistent;

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
            if(issue.getIv() ==null){
                //inconsistent AV on JIRA
                issueListWithAVInconsistent.add(issue);
                continue;
            }
            issueListWithAVConsistent.add(issue);
            iv = issue.getIv().getIndex();
            fv = issue.fv.getIndex();
            ov = issue.ov.getIndex();
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
        //count = 0 --> [1] count = 1 --> [1;2]
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
                iv = issue.getIv().getIndex();
                fv = issue.fv.getIndex();
                ov = issue.ov.getIndex();
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
        List<Float> pProjects = new ArrayList<>();
        List<Release> releaseList1;
        List<Issue> issues;

        for(String project: projects){
            total = 0;
            pColdStartProject = 0;
            releaseList1 = Jira.getReleases(project);
            issues = Jira.getBugs(project, releaseList1);
            for(Issue issue: issues){
                if(issue.getIv() !=null){
                    //issue with consistent AV on JIRA
                    iv = issue.getIv().getIndex();
                    fv = issue.fv.getIndex();
                    ov = issue.ov.getIndex();
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
                pProjects.add(pColdStartProject);
            }
        }

        int n = pProjects.size();
        pProjects.sort(Comparator.comparing(o -> o));

        if(n%2 == 0){
            pColdStart = pProjects.get((n+1)/2);
        }else{
            pColdStart = (pProjects.get(n/2) + pProjects.get((n/2) + 1))/2;
        }
        return pColdStart;
    }
}
