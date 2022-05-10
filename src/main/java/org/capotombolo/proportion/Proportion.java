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
        List<Float> p_increments = new ArrayList<>();           //List of P_Increment for all versions
        p_increments.add((float) 0);                            //P_increment of first release is 0
        boolean first = true;                                   //I take the first release
        Release prev = null;

        //for each version, I computed p_increment as the average P among defects fixed in versions from 1 to R - 1
        for(Release release: this.releaseList){
            if(first){
                prev=release;
                first=false;
                continue;
            }
            //Get all issues fixed between release 1 and R - 1
            issueFixedInPrevVersions = new ArrayList<>();
            for(Issue issue: this.issueList){
                if(issue.iv!=null && issue.fixDate.compareTo(prev.getDate()) < 0){
                    issueFixedInPrevVersions.add(issue);
                }
                if (issue.fixDate.compareTo(prev.getDate()) >= 0)
                    break;
            }
            float p_issue;
            float p_increment=0;                            //P_Increment of release N
            float fv, iv, ov;
            if(!issueFixedInPrevVersions.isEmpty()){
                System.out.println("Numero issue fixate nelle versioni precedenti: " + issueFixedInPrevVersions.size());
                for(Issue issue: issueFixedInPrevVersions){
                    if(issue.ov == issue.fv){
                        fv = this.releaseList.indexOf(issue.fv);
                        iv = this.releaseList.indexOf(issue.iv);
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
                System.out.println("p_increment of release " + release.getRelease() + ": " + p_increment);
            }
            p_increments.add(p_increment);
            prev=release;
        }

        //Compute IV of issue with inconsistent AV on JIRA
        float iv, ov, fv, p_increment;
        for(Issue issue: this.issueList){
            if(issue.iv==null){
                //Issue has no AV on JIRA
                fv = this.releaseList.indexOf(issue.fv);
                ov = this.releaseList.indexOf(issue.ov);
                p_increment = p_increments.get((int)fv);
                if(issue.ov==issue.fv){
                    iv = fv - p_increment;
                }else{
                    iv = fv - (fv - ov)*p_increment;
                }
                if(iv + 1> this.releaseList.size()){
                    issue.iv = this.releaseList.get(this.releaseList.size()-1);
                }else if(iv <= 0){
                    issue.iv = this.releaseList.get(0);
                }else{
                    issue.iv = this.releaseList.get((int)iv + 1);
                }
            }
        }
    }
}
