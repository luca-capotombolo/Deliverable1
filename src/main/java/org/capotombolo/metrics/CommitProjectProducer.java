package org.capotombolo.metrics;

import org.capotombolo.git.GitSkills;
import org.capotombolo.utils.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class CommitProjectProducer {

    List<Release> releaseList;
    String path;

    public CommitProjectProducer(List<Release> releaseList, String path) {
        this.path = path;
        this.releaseList = releaseList;
    }

    public List<CommitMetric> getAllCommitOfProject() throws IOException, GitAPIException {
        List<FileCommitMetric> changedFiles;                                                              //changed file by commit
        CommitMetric commitMetric;
        FileCommitMetric fileCommit;
        List<CommitMetric> commitMetrics = new ArrayList<>();
        GitSkills gitSkills = new GitSkills(this.path);
        Iterable<RevCommit> commits = gitSkills.git.log().all().call();
        List<RevCommit> commitsList = new ArrayList<>();
        commits.iterator().forEachRemaining(commitsList::add);                                  //get all commits for the project
        for (RevCommit commit : commitsList) {
            try {
                changedFiles = new ArrayList<>();
                ObjectReader reader = gitSkills.git.getRepository().newObjectReader();
                CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                ObjectId oldTree = gitSkills.git.getRepository().resolve(commit.getName() + "~1^{tree}");
                oldTreeIter.reset(reader, oldTree);
                CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                ObjectId newTree = gitSkills.git.getRepository().resolve(commit.getName() + "^{tree}");
                newTreeIter.reset(reader, newTree);
                DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
                diffFormatter.setRepository(gitSkills.git.getRepository());
                List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);
                //Get alla file changed by commit
                for (DiffEntry entry : entries) {
                    fileCommit = new FileCommitMetric(entry.getNewPath().replace("/", "\\"), entry.getChangeType());
                    changedFiles.add(fileCommit);
                }
            }catch (Exception e){
                continue;
            }

            //Get the release of the commit
            commitMetric = new CommitMetric(changedFiles, new Date(commit.getCommitTime() * 1000L), null, commit.getAuthorIdent().getName(), commit);

            for (Release release : releaseList) {
                if (release.date.compareTo(commitMetric.date) >= 0) {
                    commitMetric.release = release;                                    //first release with date greater than date of commit
                    break;                                                              //sorted releases
                }
            }

            //the commit belong a version that is not released
            if (commitMetric.release == null)
                continue;

            commitMetrics.add(commitMetric);
        }

        return commitMetrics;
    }

}

