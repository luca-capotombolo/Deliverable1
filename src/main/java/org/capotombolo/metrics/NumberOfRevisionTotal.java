package org.capotombolo.metrics;


import org.capotombolo.utils.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumberOfRevisionTotal {

    HashMap<Release, List<MyFile>> hashMap;
    List<CommitMetric> commitMetrics;

    public NumberOfRevisionTotal(Map<Release, List<MyFile>> hashMap, List<CommitMetric> commitMetrics){
        this.hashMap = (HashMap<Release, List<MyFile>>) hashMap;
        this.commitMetrics = commitMetrics;
    }

    public Map<Release, List<MyFile>> getNumberOfRevisionTotal(){
        Release release;
        List<MyFile> myFiles;
        int count;

        for(Map.Entry<Release, List<MyFile>> entry: this.hashMap.entrySet()){
            release = entry.getKey();
            myFiles = entry.getValue();
            for(MyFile myFile: myFiles){
                //I suppose zero revision for this file
                count = 0;
                for(CommitMetric commitMetric: commitMetrics){
                    if(commitMetric.getRelease().getDate().compareTo(release.getDate()) <=0){
                        for(FileCommitMetric fileCommitMetric: commitMetric.fileCommits){
                            if(myFile.path.contains(fileCommitMetric.filename)){
                                count++;
                            }
                        }
                    }
                }
                myFile.numberRevisionTotal = count;
            }
        }
        return this.hashMap;
    }



}
