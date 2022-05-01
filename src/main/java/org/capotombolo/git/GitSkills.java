package org.capotombolo.git;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.StopWalkException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.treewalk.TreeWalk;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GitSkills {

    public static void main(String[] args) throws IOException, GitAPIException {
        GitSkills gitSkills = new GitSkills("C:\\Users\\lucac\\MyJava\\GitLabSearchCommitByComment", null);
        //gitSkills.searchCommitByCommentFilter("altroBranch", "altroBranch");
        gitSkills.searchCommitOfSpecificBranch("myjava");
        gitSkills.searchCommitOfSpecificBranch("altroBranch");
        //gitSkills.searchCommitOfAllBranch();
    }

    private final String localPath;
    private final String remotePath;
    private final Repository localRepo;
    private final Git git;

    public GitSkills(String localPath, String remotePath) throws IOException {
        this.localPath = localPath;
        this.remotePath = remotePath;
        this.localRepo = new FileRepository(localPath + "/.git");
        this.git = new Git(localRepo);
    }

    public void cloneRepo(String branch) throws GitAPIException {
        Git.cloneRepository().setURI(this.remotePath).setBranch(branch).setDirectory(new File(localPath)).call();
    }


    public void searchAllFilesModifiedByACommit(ObjectId treeID){
        try (TreeWalk treeWalk = new TreeWalk(this.localRepo)) {
            treeWalk.reset(treeID);
            while (treeWalk.next()){
                String path = treeWalk.getPathString();
                System.out.println(treeWalk.getAttributes().toString());
                System.out.println(path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /*
    Ancora non funziona
     */
    public void searchCommitOfAllBranch() throws GitAPIException, IOException {
        String branchName;
        List<Ref> branches;

        branches = git.branchList().call();

        for (Ref branch: branches) {
            branchName = branch.getName();

            System.out.println("Commits del branch: " + branchName);
            System.out.println("----------------------------------------------------");

            Iterable<RevCommit> commits = git.log().add(this.localRepo.resolve(branchName)).call();

            List<RevCommit> commitsList = new ArrayList<>();

            commits.iterator().forEachRemaining(commitsList::add);

            for(RevCommit commit: commitsList) {
                System.out.println(commit.getName());
                System.out.println(commit.getAuthorIdent().getName());
                System.out.println(new Date(commit.getCommitTime() * 1000L));
                System.out.println(commit.getFullMessage());
            }

        }

    }

    public void searchCommitByCommentFilter(String textGrep, String branchName) throws IOException, GitAPIException {

        RevFilter revFilter = new RevFilter() {
            @Override
            public boolean include(RevWalk revWalk, RevCommit revCommit) throws StopWalkException {
                String comment = revCommit.getFullMessage();
                return comment.contains(textGrep);
            }

            @Override
            public RevFilter clone() {
                return null;
            }
        };


        Iterable<RevCommit> commits = git.log().add(this.localRepo.resolve(branchName)).setRevFilter(revFilter).call();

        List<RevCommit> commitsList = new ArrayList<>();

        commits.iterator().forEachRemaining(commitsList::add);

        System.out.println("Commit trovati relativi al branch "+ branchName+" contenenti la stringa "+ textGrep+":");
        System.out.println("--------------------------------------------------------");
        for(RevCommit commit: commitsList) {
            System.out.println(commit.getName());
            System.out.println("Author: "+commit.getAuthorIdent().getName());
            System.out.println("Date: "+new Date(commit.getCommitTime() * 1000L));
            System.out.println("Full Message: "+commit.getFullMessage());
            //ottengo i files che sono stati modificati dal commit
            ObjectId treeID = commit.getTree().getId();
            this.searchAllFilesModifiedByACommit(treeID);
        }

    }

    public void searchCommitOfSpecificBranch(String branchName) throws IOException, GitAPIException {

        Iterable<RevCommit> commits = git.log().add(this.localRepo.resolve(branchName)).call();

        List<RevCommit> commitsList = new ArrayList<>();

        commits.iterator().forEachRemaining(commitsList::add);

        System.out.println("Commit trovati relativi al branch "+ branchName+":");
        System.out.println("--------------------------------------------------------");
        for(RevCommit commit: commitsList) {
            System.out.println(commit.getName());
            System.out.println("Author: "+commit.getAuthorIdent().getName());
            System.out.println("Date: "+new Date(commit.getCommitTime() * 1000L));
            System.out.println("Full Message: "+commit.getFullMessage());
        }

    }
}
