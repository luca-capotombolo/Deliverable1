package org.capotombolo.utils;

import org.eclipse.jgit.revwalk.RevCommit;
import java.sql.Date;
import java.util.List;

public class CommitMetric {

    public List<FileCommitMetric> fileCommits;
    public Date date;
    public Release release;
    public String authorName;
    public RevCommit revCommit;

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
