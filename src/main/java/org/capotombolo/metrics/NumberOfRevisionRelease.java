package org.capotombolo.metrics;

import org.capotombolo.utils.CommitMetric;
import org.capotombolo.utils.FileCommitMetric;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberOfRevisionRelease {

    HashMap<Release, List<MyFile>> hashMap;
    List<CommitMetric> commitMetrics;

    public NumberOfRevisionRelease(HashMap<Release, List<MyFile>> hashMap, List<CommitMetric> commitMetrics){
        this.hashMap = hashMap;
        this.commitMetrics = commitMetrics;
    }

    public HashMap<Release, List<MyFile>> getNumberOfRevisionRelease() {
        Release release;
        List<MyFile> myFiles;
        int count;

        for (Map.Entry<Release, List<MyFile>> entry : this.hashMap.entrySet()) {
            release = entry.getKey();
            myFiles = entry.getValue();
            for (MyFile myFile : myFiles) {
                //I suppose zero revision for this file
                count = 0;
                for (CommitMetric commitMetric : commitMetrics) {
                    if (commitMetric.release.getDate().compareTo(release.getDate()) == 0) {
                        for (FileCommitMetric fileCommitMetric : commitMetric.fileCommits) {
                            if (myFile.path.contains(fileCommitMetric.filename)) {
                                count++;
                            }
                        }
                    }
                }
                myFile.numberRevisionRelease = count;
            }
        }
        return this.hashMap;
    }

}
