package org.capotombolo.git;

import org.capotombolo.utils.Commit;
import org.capotombolo.utils.Issue;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
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

    public static void main(String[] args) {

    }

    private final String localPath;
    private final Repository localRepo;
    private final Git git;

    public GitSkills(String localPath) throws IOException {
        this.localPath = localPath;
        //this.localRepo = new FileRepository(localPath + "/.git");
        this.localRepo = new RepositoryBuilder().setGitDir(new File(this.localPath+"/.git"))
                            .readEnvironment()
                            .findGitDir()
                            .build();
        this.git = new Git(localRepo);
    }

    public String getLocalPath() {
        return this.localPath;
    }

    public void  setBranch(String branch) throws GitAPIException {
        try{
            CheckoutCommand checkoutCommand = this.git.checkout();
            checkoutCommand.setName("release-"+branch).call();
        }catch (CheckoutConflictException e){
            e.printStackTrace();
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
                    //System.out.println("Cambiamenti:");
                    for (DiffEntry entry : entries) {
                        if(entry.getChangeType()== DiffEntry.ChangeType.DELETE || entry.getChangeType()== DiffEntry.ChangeType.MODIFY) {
                            changedFiles.add(entry.getNewPath());                               //this file was changed by the commit
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

                    if (commitWithKey.release == null)
                        continue;
                    //che succede se un commit appartiene ad una release ancora non lasciata? Vedi se ci sono commit che hanno il campo release uguale a null
                    //commitWithKey = new Commit(new Date(commit.getCommitTime()*1000L), commit.getFullMessage(), changedFiles, null, commit.getName());       //commit legato all'ISSUE
                    commitWithIssueKeyList.add(commitWithKey);
                }

            }

            hashMapIssueCommits.put(issue, commitWithIssueKeyList);

            /*
            List<Commit> commits1 = hashMapIssueCommits.get(issue);
            System.out.println("ISSUE: " + issue.key);
            for(Commit commit: commits1){
                System.out.println("Commento: " + commit.comment);
            }
            System.out.println("\n");*/

        }
        return hashMapIssueCommits;
    }

    /*
    public List<Commit> searchCommitByKeyBug(String key, Release release) throws GitAPIException, IOException {
        List<Commit> commitWithKeyList = new ArrayList<>();
        RevFilter revFilter = new RevFilter() {
            @Override
            public boolean include(RevWalk revWalk, RevCommit revCommit) throws StopWalkException, MissingObjectException, IncorrectObjectTypeException, IOException {
                String comment = revCommit.getFullMessage();
                return comment.contains(key);
            }

            @Override
            public RevFilter clone() {
                return null;
            }
        };

        Iterable<RevCommit> commits = this.git.log().setRevFilter(revFilter).call();            //prendo tutti i commits della release corrente che hanno la key del ISSUE

        List<RevCommit> commitsList = new ArrayList<>();

        commits.iterator().forEachRemaining(commitsList::add);

        Commit commitWithKey;
        List<String> changedFiles;

        for(RevCommit commit: commitsList) {
            changedFiles = new ArrayList<>();
            System.out.println(commit.getName());
            System.out.println("Author: "+commit.getAuthorIdent().getName());
            System.out.println("Date: "+new Date(commit.getCommitTime() * 1000L));
            System.out.println("Full Message: "+commit.getFullMessage());
            commitWithKey = new Commit(new Date(commit.getCommitTime()*1000L), commit.getFullMessage(), null, release, commit.getName());       //commit legato all'ISSUE
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
            System.out.println("Cambiamenti:");
            for (DiffEntry entry : entries) {
                if(entry.getChangeType()== DiffEntry.ChangeType.DELETE || entry.getChangeType()== DiffEntry.ChangeType.MODIFY) {
                    System.out.println(entry.getNewPath());
                    changedFiles.add(entry.getNewPath());
                }
            }
            System.out.println("\n\n");
            //commitWithKey = new Commit(new Date(commit.getCommitTime()*1000L), commit.getFullMessage(), changedFiles, release, commit.getName());       //commit legato all'ISSUE
            commitWithKey = new Commit(null, commit.getFullMessage(), changedFiles, release, commit.getName());
            commitWithKeyList.add(commitWithKey);
        }

        return commitWithKeyList;

    }*/




}
