package org.capotombolo.metrics;

import org.capotombolo.utils.CommitMetric;
import org.capotombolo.utils.FileCommitMetric;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberOfAuthors {

    HashMap<Release, List<MyFile>> hashMap;
    List<CommitMetric> commitMetrics;

    public NumberOfAuthors(Map<Release, List<MyFile>> hashMap, List<CommitMetric> commitMetrics){
        this.commitMetrics = commitMetrics;
        this.hashMap = (HashMap<Release, List<MyFile>>) hashMap;
    }

    public Map<Release, List<MyFile>> getNumberOfAuthors() {
        Release release;
        List<MyFile> myFiles;

        for(Map.Entry<Release, List<MyFile>> entry: this.hashMap.entrySet()){
            release = entry.getKey();                                                           //Get current release
            myFiles = entry.getValue();                                                         //Get files of current release
            for(MyFile myFile: myFiles){
                //For each file of release
                for(CommitMetric commitMetric: commitMetrics){
                    //for each commit
                    if(commitMetric.getRelease().getDate().compareTo(release.getDate())<=0){
                        for(FileCommitMetric fileCommitMetric: commitMetric.fileCommits){
                            //for each changed files
                            if(myFile.path.contains(fileCommitMetric.filename)){
                                myFile.getAuthors().add(commitMetric.authorName);
                            }
                        }
                    }
                }
            }
        }
        return this.hashMap;
    }
}
