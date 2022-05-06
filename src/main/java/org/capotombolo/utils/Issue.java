package org.capotombolo.utils;

import java.util.List;

public class Issue {

    public final String key;
    public final Release iv;
    public final List<Release> fvs;
    public Release fv;

    public Issue(String key, Release iv, List<Release> fvs)
    {
        this.key = key;
        this.iv = iv;
        this.fvs = fvs;
        this.getYoungerFixVersion();
    }

    public void getYoungerFixVersion()
    {
        if(fvs.size() == 0){
            this.fv = null;
            return;
        }

        Release youngerRelease = fvs.get(0);

        for(Release release: fvs){
            if(youngerRelease.getDate().compareTo(release.getDate()) < 0)
            {
                youngerRelease = release;
            }
        }

        this.fv = youngerRelease;
    }
}
