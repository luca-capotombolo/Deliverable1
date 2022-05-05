package org.capotombolo.git;

import org.capotombolo.utils.Commit;
import org.capotombolo.utils.Release;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitSkills {

    public static void main(String[] args) throws IOException, GitAPIException {
        GitSkills gitSkills = new GitSkills("C:\\Users\\lucac\\MyJava\\GitLabSearchCommitByComment");


        /*
        List<Commit> commitWithKeyList = new ArrayList<>();

        Iterable<RevCommit> commits = gitSkills.git.log().call();

        List<RevCommit> commitsList = new ArrayList<>();

        commits.iterator().forEachRemaining(commitsList::add);

        Commit commitWithKey;

        for(RevCommit commit: commitsList) {
            System.out.println(commit.getName());   //SHA
            System.out.println("Author: "+commit.getAuthorIdent().getName());
            System.out.println("Date: "+new Date(commit.getCommitTime() * 1000L));
            System.out.println("Full Message: "+commit.getFullMessage());
            commitWithKey = new Commit(new Date(commit.getCommitTime()*1000L), commit.getFullMessage(), null, null, commit.getName());
            commitWithKeyList.add(commitWithKey);
        }

        for(Commit commit: commitWithKeyList) {
            System.out.println("SHA: " + commit.sha);
            System.out.println("Date: " + commit.date);
            System.out.println("Message: " + commit.comment);
            ObjectReader reader = gitSkills.git.getRepository().newObjectReader();
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectId oldTree = gitSkills.git.getRepository().resolve(commit.sha+"~1^{tree}");
            oldTreeIter.reset(reader, oldTree);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectId newTree = gitSkills.git.getRepository().resolve(commit.sha+"^{tree}");
            newTreeIter.reset(reader, newTree);
            DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
            diffFormatter.setRepository(gitSkills.git.getRepository());
            List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);
            System.out.println("Cambiamenti:");
            for (DiffEntry entry : entries) {
                System.out.println(entry.getNewPath());
            }
            System.out.println("\n\n");
        }*/

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
            //commitWithKey = new Commit(new Date(commit.getCommitTime()*1000L), commit.getFullMessage(), null, release, commit.getName());       //commit legato all'ISSUE
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
            commitWithKey = new Commit(new Date(commit.getCommitTime()*1000L), commit.getFullMessage(), changedFiles, release, commit.getName());       //commit legato all'ISSUE
            commitWithKeyList.add(commitWithKey);
        }

        return commitWithKeyList;

    }




}
