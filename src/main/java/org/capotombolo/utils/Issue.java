package org.capotombolo.utils;

import java.util.List;

public class Issue {

    public final String key;
    public final Release iv;
    public final List<Release> fvs;

    public Issue(String key, Release iv, List<Release> fvs)
    {
        this.key = key;
        this.iv = iv;
        this.fvs = fvs;
    }
}
