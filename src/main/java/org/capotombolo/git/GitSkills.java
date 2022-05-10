package org.capotombolo.git;

import org.capotombolo.utils.Commit;
import org.capotombolo.utils.Issue;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;

public class GitSkills {

    public final Git git;
    private final String localPath;

    public GitSkills(String localPath) throws IOException {
        this.localPath = localPath;
        Repository localRepo = new RepositoryBuilder().setGitDir(new File(this.localPath + "/.git"))
                .readEnvironment()
                .findGitDir()
                .build();
        this.git = new Git(localRepo);
    }

    public void  setBranch(String branch) throws GitAPIException {
        try{
            CheckoutCommand checkoutCommand = this.git.checkout();
            checkoutCommand.setName("release-"+branch).call();
        }catch (CheckoutConflictException e){
            if(this.localPath.contains("zookeeper")) {
                System.out.println(branch);
                this.git.add().addFilepattern(".").call();
                this.git.commit().setMessage("...").call();
                CheckoutCommand checkoutCommand = this.git.checkout();
                checkoutCommand.setName("release-" + branch).call();
                e.printStackTrace();
            }
        }
    }

    public HashMap<Issue, List<Commit>> classifyCommitsIssue(List<Issue> issueList, List<Release> releaseList) throws IOException, GitAPIException {

        HashMap<Issue, List<Commit>> hashMapIssueCommits = new HashMap<>();
        List<String> changedFiles;                                                              //changed file by commit
        Commit commitWithKey;                                                                   //commit which contains Key of an Issue
        List<Commit> commitWithIssueKeyList;                                                    //commit list of commits which contains Key of the Issue


        Iterable<RevCommit> commits = this.git.log().all().call();
        List<RevCommit> commitsList = new ArrayList<>();
        commits.iterator().forEachRemaining(commitsList::add);                                  //get all commits for the project

        for(Issue issue: issueList){
            commitWithIssueKeyList = new ArrayList<>();                                         //all commits for a specified issue

            for(RevCommit commit: commitsList){

                if(commit.getFullMessage().startsWith(issue.key+":")
                        || commit.getFullMessage().startsWith(issue.key + " ")
                        || commit.getFullMessage().startsWith(" " + issue.key + ":")
                        || commit.getFullMessage().startsWith("  " + issue.key + ":")
                        || commit.getFullMessage().startsWith(" " + issue.key + " ")){

                    //get all files changed by commit
                    changedFiles = new ArrayList<>();
                    ObjectReader reader = this.git.getRepository().newObjectReader();
                    CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
                    ObjectId oldTree = this.git.getRepository().resolve(commit.getName()+"~1^{tree}");
                    oldTreeIter.reset(reader, oldTree);
                    CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
                    ObjectId newTree = this.git.getRepository().resolve(commit.getName()+"^{tree}");
                    newTreeIter.reset(reader, newTree);
                    DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
                    diffFormatter.setRepository(this.git.getRepository());
                    List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);
                    for (DiffEntry entry : entries) {
                        if(entry.getChangeType()== DiffEntry.ChangeType.DELETE || entry.getChangeType()== DiffEntry.ChangeType.MODIFY) {
                            String newPath = entry.getNewPath().replaceAll("/", "\\\\");
                            //this file was changed by the commit
                            changedFiles.add(newPath);
                        }
                    }

                    //Get the release of the commit
                    commitWithKey = new Commit(new Date(commit.getCommitTime()*1000L), commit.getFullMessage(), changedFiles, null, commit.getName());
                    for(Release release: releaseList){
                        if(release.date.compareTo(commitWithKey.date)>0){
                            commitWithKey.release = release;                                    //first release with date greater than date of commit
                            break;                                                              //sorted releases
                        }
                    }

                    //check!
                    if (commitWithKey.release == null)
                        continue;

                    commitWithIssueKeyList.add(commitWithKey);
                }

            }


            hashMapIssueCommits.put(issue, commitWithIssueKeyList);

        }

        return hashMapIssueCommits;
    }
}
