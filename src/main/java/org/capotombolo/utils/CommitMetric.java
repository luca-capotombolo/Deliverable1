package org.capotombolo.utils;

import org.eclipse.jgit.revwalk.RevCommit;
import java.sql.Date;
import java.util.List;

public class CommitMetric {

    public final List<FileCommitMetric> fileCommits;
    public final Date date;

    public Release getRelease() {
        return release;
    }

    public void setRelease(Release release) {
        this.release = release;
    }

    private Release release;
    public final String authorName;
    public final RevCommit revCommit;

    public CommitMetric(List<FileCommitMetric> fileCommits, Date date, Release release, String authorName, RevCommit revCommit){
        this.release = release;
        this.date = date;
        this.fileCommits = fileCommits;
        this.authorName = authorName;
        this.revCommit = revCommit;
    }

    public Date getDate(){
        return this.date;
    }
}
