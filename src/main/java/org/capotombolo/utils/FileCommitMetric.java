package org.capotombolo.utils;

import org.eclipse.jgit.diff.DiffEntry;

public class FileCommitMetric {

    public final String filename;
    public final DiffEntry.ChangeType fileCommitState;


    public FileCommitMetric(String filename, DiffEntry.ChangeType fileCommitState){
        this.filename = filename;
        this.fileCommitState = fileCommitState;
    }

}
