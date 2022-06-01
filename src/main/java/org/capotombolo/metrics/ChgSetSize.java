package org.capotombolo.metrics;

import org.capotombolo.utils.CommitMetric;
import org.capotombolo.utils.FileCommitMetric;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
import java.util.*;

public class ChgSetSize {
    private final HashMap<Release, List<MyFile>> hashMap;
    private final List<CommitMetric> commitMetrics;

    public ChgSetSize(Map<Release, List<MyFile>> hashMap, List<CommitMetric> commitMetrics){
        this.commitMetrics = commitMetrics;
        this.hashMap = (HashMap<Release, List<MyFile>>) hashMap;
    }

    public Map<Release, List<MyFile>> getChgSetSizeRelease()  {
        Release release;
        List<MyFile> myFiles;
        Set<String>  filesTouchedWithC;
        boolean touched;
        int maxNumberTouchedFile;

        for (Map.Entry<Release, List<MyFile>> entry : this.hashMap.entrySet()) {
            release = entry.getKey();
            myFiles = entry.getValue();
            for (MyFile myFile : myFiles) {
                //For each file of release
                maxNumberTouchedFile = 0;
                for (CommitMetric commitMetric : commitMetrics) {
                    //for each commit
                    if (commitMetric.getRelease().getDate().compareTo(release.getDate()) == 0) {
                        //all files that has been touched with iterated file
                        filesTouchedWithC = new HashSet<>();
                        //I suppose that file is not touched by the commit
                        touched = false;
                        for(FileCommitMetric fileCommitMetric: commitMetric.fileCommits){
                            filesTouchedWithC.add(fileCommitMetric.filename);
                            if(myFile.path.contains(fileCommitMetric.filename)){
                                touched = true;
                            }
                        }
                        if(touched){
                            //myFile is in the set so I must delete it
                            if(maxNumberTouchedFile < (filesTouchedWithC.size() - 1))
                                maxNumberTouchedFile = filesTouchedWithC.size() - 1;
                            myFile.setTouchedFileWithCRelease.addAll(filesTouchedWithC);
                        }
                    }
                }
                //at least zero
                myFile.maxNumberTouchedFile = maxNumberTouchedFile;
                try {
                    myFile.avgNumberTouchedFile = myFile.setTouchedFileWithCRelease.size()/(float)myFile.numberRevisionRelease;
                }catch (Exception e){
                    //there are no revision for this file in the release
                }
            }
        }
        return hashMap;
    }

}
