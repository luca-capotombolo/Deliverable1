package org.capotombolo.proportion;

import org.capotombolo.utils.Issue;
import org.capotombolo.utils.Release;

import java.util.ArrayList;
import java.util.List;

public class Proportion {

    public final List<Release> releaseList;
    public final List<Issue> issueList;

    public Proportion(List<Release> releaseList, List<Issue> issueList){
        this.releaseList = releaseList;
        this.issueList = issueList;
    }

    public void increment(){
        List<Issue> issueFixedInPrevVersions;
        List<Float> p_increments = new ArrayList<>();
        p_increments.add((float) 0);
        boolean first = true;
        Release prev = null;

        //for each version, I computed p_increment as the average P among defects fixed in versions 1 to R - 1
        for(Release release: this.releaseList){
            if(first){
                prev=release;
                first=false;
                continue;
            }
            //Get all issues that are fixed between release 1 and R - 1
            issueFixedInPrevVersions = new ArrayList<>();
            for(Issue issue: this.issueList){
                if(issue.iv!=null && issue.fixDate.compareTo(prev.getDate()) < 0){
                    issueFixedInPrevVersions.add(issue);
                }
                if (issue.fixDate.compareTo(prev.getDate()) >= 0)
                    break;
            }
            float p_issue;
            float p_increment=0;
            float fv, iv, ov;
            if(!issueFixedInPrevVersions.isEmpty()){
                System.out.println("Numero issue fixate nelle versioni precedenti: " + issueFixedInPrevVersions.size());
                for(Issue issue: issueFixedInPrevVersions){
                    if(issue.ov == issue.fv){
                        fv = this.releaseList.indexOf(issue.fv);
                        iv = this.releaseList.indexOf(issue.iv);
                        //ov = releaseList.indexOf(issue.ov);
                        p_issue= (fv - iv);
                    }else{
                        fv = this.releaseList.indexOf(issue.fv);
                        iv = this.releaseList.indexOf(issue.iv);
                        ov = this.releaseList.indexOf(issue.ov);
                        p_issue= (fv - iv)/(fv - ov);
                    }
                    p_increment+=p_issue;
                }
                p_increment=p_increment/issueFixedInPrevVersions.size();
                System.out.println("p_increment: " + p_increment);
            }
            p_increments.add(p_increment);
            prev=release;
        }
        float p=0;
        for(Float p_increment: p_increments){
            p+=p_increment;
        }
        p=p/p_increments.size();
        float iv, ov, fv;
        for(Issue issue: this.issueList){
            if(issue.iv==null){
                fv = this.releaseList.indexOf(issue.fv);
                ov = this.releaseList.indexOf(issue.ov);
                if(issue.ov==issue.fv){
                    iv = fv - p;
                }else{
                    iv = fv - (fv - ov)*p;
                }
                if(iv + 1> this.releaseList.size()){
                    issue.iv = this.releaseList.get(this.releaseList.size()-1);
                }else{
                    issue.iv = this.releaseList.get((int)iv +1);
                }
            }
        }
    }



    public static void main(String[] args) {
        float n = (float) 9.99999999;
        System.out.println((int)n);
    }
}
