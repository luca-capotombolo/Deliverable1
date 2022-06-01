package org.capotombolo.utils;

import java.sql.Date;
import java.util.List;


public class Commit {
    public final String sha;
    public final Date date;
    public final String comment;
    public final List<String> files;

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    private Release release;

    public Commit(Date date, String comment, List<String> files, Release release, String sha) {
        this.date = date;
        this.comment = comment;
        this.files = files;
        this.release = release;
        this.sha = sha;
    }
}
