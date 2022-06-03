package org.capotombolo.metrics;

import org.capotombolo.git.GitSkills;
import org.capotombolo.utils.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LocAddedRelease {

    private final HashMap<Release, List<MyFile>> hashMap;
    private final List<CommitMetric> commitMetrics;
    private final  GitSkills gitSkills;

    public LocAddedRelease(Map<Release, List<MyFile>> hashMap, List<CommitMetric> commitMetrics, String path) throws IOException {
        this.commitMetrics = commitMetrics;
        this.hashMap = (HashMap<Release, List<MyFile>>) hashMap;
        this.gitSkills = new GitSkills(path);
    }

    public Map<Release, List<MyFile>> computeLocAddedRelease()  {
        Release release;
        List<MyFile> myFiles;
        int linesAdded;

        for (Map.Entry<Release, List<MyFile>> entry : this.hashMap.entrySet()) {
            release = entry.getKey();                                                           //Get current release
            myFiles = entry.getValue();                                                         //Get files of current release
            for (MyFile myFile : myFiles) {
                //For each file of release
                for (CommitMetric commitMetric : commitMetrics) {
                    //for each commit
                    linesAdded = 0;
                    computeLocAddedFileCommit(commitMetric, release, myFile, linesAdded);
                }
                try {
                    myFile.setAverageNumberLocAdded(myFile.getNumberLocAddedRelease() / myFile.getNumberRevisionRelease());
                }catch (Exception e){
                    //No revision in this release
                }
            }
        }
        return hashMap;
    }

    private void computeLocAddedFileCommit(CommitMetric commitMetric, Release release, MyFile myFile, int linesAdded){
        int tot;
        if (commitMetric.getRelease().getDate().compareTo(release.getDate()) == 0) {
            //commit.release == release
            for(FileCommitMetric fileCommitMetric: commitMetric.fileCommits){
                linesAdded = compareFileCommitToFile(myFile, fileCommitMetric, commitMetric, linesAdded);
            }
            tot = myFile.getNumberLocAddedRelease();
            myFile.setNumberLocAddedRelease(tot + linesAdded);
            setMaxNumberLA(linesAdded, myFile);
        }
    }

    private int compareFileCommitToFile(MyFile myFile, FileCommitMetric fileCommitMetric, CommitMetric commitMetric, int linesAdded){
        //Fixed file
        if(myFile.path.contains(fileCommitMetric.filename) && (DiffEntry.ChangeType.ADD==fileCommitMetric.fileCommitState
                || DiffEntry.ChangeType.MODIFY ==fileCommitMetric.fileCommitState)){
            try {
                ObjectReader reader = gitSkills.git.getRepository().newObjectReader();
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                ObjectId oldTree = gitSkills.git.getRepository().resolve(commitMetric.revCommit.getName() + "~1^{tree}");
                oldTreeIter.reset(reader, oldTree);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                ObjectId newTree = gitSkills.git.getRepository().resolve(commitMetric.revCommit.getName() + "^{tree}");
                newTreeIter.reset(reader, newTree);
                DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
                diffFormatter.setRepository(gitSkills.git.getRepository());
                List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);
                //Get all file changed by commit
                linesAdded = diffEntry(entries, fileCommitMetric, diffFormatter, linesAdded);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return linesAdded;
    }

    private int diffEntry(List<DiffEntry> entries, FileCommitMetric fileCommitMetric, DiffFormatter diffFormatter, int linesAdded) throws IOException {
        for (DiffEntry diff : entries) {
            //prendo il cambiamento che mi interessa
            if(Objects.equals(diff.getNewPath().replace("/", "\\"), fileCommitMetric.filename)) {
                for (Edit edit : diffFormatter.toFileHeader(diff).toEditList()) {
                    linesAdded += edit.getEndB() - edit.getBeginB();
                }
            }
        }
        return linesAdded;
    }

    private void setMaxNumberLA(int linesAdded, MyFile myFile){
        if(linesAdded > myFile.getMaxNumberLocAdded()) {
            myFile.setMaxNumberLocAdded(linesAdded);
        }
    }
}
