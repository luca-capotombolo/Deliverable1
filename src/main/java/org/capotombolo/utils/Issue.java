package org.capotombolo.utils;

import java.sql.Date;
import java.util.List;

public class Issue {

    public final String key;

    public Release getIv() {
        return iv;
    }

    public void setIv(Release iv) {
        this.iv = iv;
    }

    private Release iv;
    public final Release ov;
    public final List<Release> av;
    public final Release fv;
    public final Date fixDate;

    public boolean isChangedIV() {
        return changedIV;
    }

    public void setChangedIV(boolean changedIV) {
        this.changedIV = changedIV;
    }

    private boolean changedIV = false;

    public Issue(String key, Release iv, Release fv, Release ov, List<Release> av, Date fixDate)
    {
        this.key = key;
        this.iv = iv;
        this.fv = fv;
        this.ov = ov;
        this.av = av;
        this.fixDate = fixDate;
        if(iv==null)
            changedIV=true;
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
}
