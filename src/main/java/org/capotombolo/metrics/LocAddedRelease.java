package org.capotombolo.metrics;

import org.capotombolo.git.GitSkills;
import org.capotombolo.utils.CommitMetric;
import org.capotombolo.utils.FileCommitMetric;
import org.capotombolo.utils.MyFile;
import org.capotombolo.utils.Release;
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
    private final String path;

    public LocAddedRelease(HashMap<Release, List<MyFile>> hashMap, List<CommitMetric> commitMetrics, String path){
        this.commitMetrics = commitMetrics;
        this.hashMap = hashMap;
        this.path = path;
    }

    public HashMap<Release, List<MyFile>> computeLocAddedRelease() throws IOException {
        Release release;
        List<MyFile> myFiles;
        int linesAdded;
        GitSkills gitSkills = new GitSkills(this.path);

        for (Map.Entry<Release, List<MyFile>> entry : this.hashMap.entrySet()) {
            release = entry.getKey();                                                           //Get current release
            myFiles = entry.getValue();                                                         //Get files of current release
            for (MyFile myFile : myFiles) {
                //For each file of release
                for (CommitMetric commitMetric : commitMetrics) {
                    //for each commit
                    linesAdded = 0;
                    if (commitMetric.release.getDate().compareTo(release.getDate()) == 0) {
                        //commit.release == release
                        for(FileCommitMetric fileCommitMetric: commitMetric.fileCommits){
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
                                    for (DiffEntry diff : entries) {
                                        //prendo il cambiamento che mi interessa
                                        if(Objects.equals(diff.getNewPath().replaceAll("/", "\\\\"), fileCommitMetric.filename)) {
                                            for (Edit edit : diffFormatter.toFileHeader(diff).toEditList()) {
                                                linesAdded += edit.getEndB() - edit.getBeginB();
                                            }
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        myFile.numberLocAddedRelease +=linesAdded;
                        if(linesAdded > myFile.maxNumberLocAdded) {
                            myFile.maxNumberLocAdded = linesAdded;
                        }
                    }

                }
                try {
                    myFile.averageNumberLocAdded = myFile.numberLocAddedRelease / myFile.numberRevisionRelease;
                }catch (Exception e){
                    //No revision in this release
                }
            }
        }
        return hashMap;
    }
}
