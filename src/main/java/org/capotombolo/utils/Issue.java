package org.capotombolo.utils;

import java.sql.Date;
import java.util.List;

public class Issue {

    public final String key;
    public Release iv;
    public final Release ov;
    public final List<Release> av;
    public final Release fv;
    public final Date fixDate;

    public Issue(String key, Release iv, Release fv, Release ov, List<Release> av, Date fixDate)
    {
        this.key = key;
        this.iv = iv;
        this.fv = fv;
        this.ov = ov;
        this.av = av;
        this.fixDate = fixDate;
        this.getIVByAffectedVersion();
        this.consistencyCheck();
    }

    private void consistencyCheck() {
        if(this.iv != null && this.iv.getDate().compareTo(this.ov.getDate()) > 0){
            //IV > OV           inconsistent data
            this.iv = null;
        }
        if(this.iv!= null && this.iv.getDate().compareTo(this.fv.getDate()) > 0){
            //IV > FV           inconsistent data
            this.iv = null;
        }
    }

    private void getIVByAffectedVersion(){

        if(av.isEmpty()){
            //no affected version on JIRA
            //I will use Proportion
            iv = null;
        }else{
            Release olderRelease = av.get(0);
            for(Release release: av){
                if(olderRelease.getDate().compareTo(release.getDate())>0){
                    olderRelease = release;
                }
            }
            iv = olderRelease;
        }
    }
}
