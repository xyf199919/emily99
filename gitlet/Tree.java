package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;


public class Tree implements Serializable {
    private ArrayList<Branch> listOfBranch;
    // current branch
    private String currBranch;
    //the ID of current commit
    private String currCommit;
    //all the file that is already commited
    private HashSet<String> allCommitted = new HashSet<String>();
    ///////// Methods //////////

    public Tree() {
        currBranch = null;
        listOfBranch = new ArrayList<>();
        currCommit = null;
    }

    public static Tree deserialize() {
        return (Tree) Utils.deserialize(Main.dir(), "tree");

    }

    public Branch getBranch(String branchname) {
        for (Branch b : listOfBranch) {
            if (b.getBranchName().equals(branchname)) {
                return b;
            }
        }
        return null;
    }

    //creates new tree

    public Commit getCurrCommit() {
        return getBranch(currBranch).getCommit();
    }

    public void init() {
        if (currBranch != null) {
            System.out.println("A gitlet version-control "
                    + "system already exists in the current directory.");
            return;
        }
        //first commit
        Commit first = new Commit();
        //stage
        Stage stage = new Stage();
        //master branch
        listOfBranch.add(new Branch("master", first.getcurrID()));
        currBranch = listOfBranch.get(0).getBranchName();
        currCommit = getBranch(currBranch).getCommitID();
        allCommitted.add(first.getcurrID());
        //serialize changes
        listOfBranch.get(0).serialize();
        first.serialize();
        stage.serialize();
        serialize();
    }

    //creates new branch
    public void branch(String branchName) {
        for (Branch i : listOfBranch) {
            if (i.getBranchName().equals(branchName)) {
                System.out.println("A branch with that name already exists.");
                return;
            }
        }
        Branch newbranch = new Branch(branchName, getBranch(currBranch).getCommitID());
        listOfBranch.add(newbranch);
        newbranch.serialize();
        serialize();
    }

    /**
     * add file to stage
     */
    public void add(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        Stage stage = Stage.deserialize();
        Blob blob = new Blob(file);
        Commit curr = this.getCurrCommit();
        Blob blobincurr = curr.blobof(filename);
        //If the current working version
        // of the file is identical to the version in the current commit,
        //do not stage it to be added.
        if ((!curr.getContents().values().contains(blob.gethashID()))
                || stage.getRemovedFiles().containsKey(filename)) {
            stage.add(blob);
        }
        //serialize all;
        stage.serialize();
        blob.serialize();
    }

    //remove file
    public void remove(String filename) {
        Stage stage = Stage.deserialize();
        //If the file is tracked by the current commit,
        if (getCurrCommit().containsFile(filename)) {
            //delete the file from the working directory, ????????????
            new File(System.getProperty("user.dir"), filename).delete();
            //unstage and mark untracked
            stage.remove(getCurrCommit().blobof(filename));
            //if staged
        } else if (stage.getStagedNames().contains(filename)) {
            //unstage
            stage.getStagedFiles().remove(filename);
        } else {
            System.out.println("No reason to remove the file.");
        }
        stage.serialize();
    }

    //commit files in staging area
    public void commit(String message) {
        Stage stageNow = Stage.deserialize();
        Commit head = Commit.deserialize(currCommit);

        HashMap<String, Blob> blobMap = head.filenameToFindBlob();
        HashMap<String, String> idMap = head.getContents();
        HashMap<String, Blob> update = blobMap;


        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        }
        if (stageNow.getStagedFiles().isEmpty() && stageNow.getRemovedFiles().isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }

        //if map contents(from fileName to blobId) doesn't contain stagedFile,
        // add them into contents.
        for (String i : stageNow.getStagedFiles().keySet()) {
            if (!idMap.containsValue(stageNow.getStagedFiles().get(i))) {
                update.put(i, stageNow.blobOf(i));
                stageNow.blobOf(i).serialize();
            }
        }

        // if removedFile is not empty. the remove all the removedFiles
        if (!stageNow.getRemovedFiles().isEmpty()) {
            for (String i : stageNow.getRemovedFiles().keySet()) {
                update.remove(i);
            }

        }
        //String Message, String Parentid, List<Blob> blobs
        Commit newCommit = new Commit(message, currCommit, new ArrayList<Blob>(update.values()));
        currCommit = newCommit.getcurrID();
        allCommitted.add(currCommit);
        getBranch(currBranch).setcommit(currCommit);

        stageNow.clearStage();
        newCommit.serialize();
        stageNow.serialize();
        getBranch(currBranch).serialize();

    }

    //remove branch
    public void rmBranch(String nameDelete) {
        for (Branch i : listOfBranch) {
            if (i.getBranchName().equals(nameDelete)
                    && !i.getBranchName().equals(currBranch)) {
                listOfBranch.remove(i);
                return;
            } else if (i.getBranchName().equals(currBranch)) {
                System.out.println("Cannot remove the current branch.");
                return;
            }
        }
        System.out.println("A branch with that name does not exist.");
    }

    //gets all values in tree
    public void globalLog() {
        for (String i : allCommitted) {
            Commit.deserialize(i).printCommit();
        }
    }

    public void log() {
        logHelper(getBranch(currBranch).getCommitID());
    }

    private void logHelper(String currentCommitName) {

        Commit curr = Commit.deserialize(currentCommitName);
        while (curr.getNumCount() > 0) {
            curr.printCommit();
            Commit parent = Commit.deserialize(curr.getParentID());
            curr = parent;
        }
        curr.printCommit();
    }

    //case1 for checkOut

    public void find(String message) {
        int count = 0;
        for (String id : allCommitted) {
            Commit curr = Commit.deserialize(id);
            if (curr.getMessage().equals(message)) {
                System.out.println(id);
                count++;
            }
        }
        if (count == 0) {
            System.out.println("Found no commit with that message.");
        }
    }

    public void checkOut1(String fileName) {
        Commit head = Commit.deserialize(currCommit);
        String blobId = head.getblobID(fileName);
        if (blobId == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        Blob blob = Blob.deserialize(Main.dir(), blobId);
        Utils.writeContents(new File(fileName), blob.getContent());
    }

    //case2 for checkOut
    public void checkOut2(String commitId, String fileName) {
        Commit c = Commit.deserialize(commitId);
        if (!allCommitted.contains(commitId)) {
            System.out.println("No commit with that id exists.");
            return;
        }
        for (String i : allCommitted) {
            if (commitId.equals(i)) {
                Commit commit = Commit.deserialize(i);
                if (commit.getContents().containsKey(fileName)) {
                    Blob current = Blob.deserialize(Main.dir(), commit.getContents().get(fileName));
                    Utils.writeContents(new File(fileName), current.getContent());
                    return;
                } else {
                    System.out.println("File does not exist in that commit.");
                    return;
                }
            }
        }

    }

    //case3 for checkOut
    public void checkOut3(String branchName) {
        //Takes all files in the commit at the head of the given branch,
        // and puts them in the working directory,
        // overwriting the versions of the files that are already there if they exist.

        // Also, at the end of this command,
        // the given branch will now be considered the current branch (HEAD).
        // Any files that are tracked in the current branch but are
        // not present in the checked-out branch are deleted.
        // The staging area is cleared,
        if (!listOfBranch.contains(getBranch(branchName))) {
            System.out.println("No such branch exists.");
            return;
        }
        if (currBranch.equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }
        //If a working file is untracked in the current branch and
        // would be overwritten by the checkout
        Branch givenbranch = getBranch(branchName);
        Stage stage = Stage.deserialize();
        Commit newcommit = givenbranch.getCommit();
        Commit currcommit = Commit.deserialize(currCommit);

        for (String blobID : newcommit.getContents().values()) {
            Blob blob = Blob.deserialize(Main.dir(), blobID);
            String filename = blob.getFileName();
            File f = new File(filename);
            if (f.exists() && !currcommit.getContents().containsKey(filename)) {
                System.out.println("There is an untracked file in the way; "
                        +
                        "delete it or add it first.");
                return;
            }
        }
        for (String blobID : currcommit.getContents().values()) {
            Blob blob = Blob.deserialize(Main.dir(), blobID);
            String filename = blob.getFileName();
            if (!newcommit.getContents().values().contains(filename)) {
                Utils.restrictedDelete(filename);
            }
        }
        for (String blobID : newcommit.getContents().values()) {
            Blob blob = Blob.deserialize(Main.dir(), blobID);
            String filename = blob.getFileName();
            Utils.writeContents(new File(filename), blob.getContent());
        }
        getBranch(branchName).serialize();
        currBranch = branchName;
        currCommit = getBranch(branchName).getCommitID();
        stage.clearStage();
        stage.serialize();
    }

    /**
     * private void checkOut3Helper(Branch workingOn) {
     * //if the branch is empty
     * if (workingOn.getCommit().getParentID() == "1") {
     * return;
     * }
     * Commit currentCommit = workingOn.getCommit();
     * String direction = System.getProperty("user.dir");
     * <p>
     * //get all files name in the working directory
     * List<String> allFilesName = Utils.plainFilenamesIn(direction);
     * allFilesName.remove(".gitlet");
     * <p>
     * <p>
     * for (String i : allCommitted) {
     * Commit alreadyCommited = Commit.deserialize(i);
     * for (String j : allFilesName) {
     * if (alreadyCommited.getContents().containsKey(j)) {
     * System.out.println(j);
     * allFilesName.remove(j);//one problem here
     * System.out.println("os"); }
     * }
     * }
     * if (!allFilesName.isEmpty()) {
     * System.out.println("There is an untracked file in the way;"
     * + " delete it or add it first.");
     * }
     * <p>
     * System.out.println("put");
     * //put everything in the repository to the working directory
     * while (!currentCommit.getcurrID().equals("initial commit")) {
     * HashMap<String, String> contents = currentCommit.getContents();
     * Set<String> keys = contents.keySet();
     * for (String i : keys) {
     * String value = contents.get(i);
     * Blob currentOne = Blob.deserialize(Main.dir(), i);
     * Utils.writeContents(new File(i), currentOne.getContent());
     * }
     * currentCommit = Commit.deserialize(currentCommit.getParentID());
     * }
     * serialize();
     * }
     **/

    //displays branches, staged files
    public void status() {
        Stage stage = Stage.deserialize();
        System.out.println("=== Branches ===");
        for (Branch b : listOfBranch) {
            if (currBranch.equals(b.getBranchName())) {
                System.out.println("*" + b.getBranchName());
            } else {
                System.out.println(b.getBranchName());
            }
        }
        System.out.println();
        stage.printstage();
        System.out.println();
        stage.printremoved();
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
    }


    //the reset function(see what to add)
    public void reset(String commitID) {
        Stage stage = Stage.deserialize();
        if (!(allCommitted.contains(commitID))) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit update = Commit.deserialize(commitID);
        Commit curr = Commit.deserialize(currCommit);

        Stage currStage = Stage.deserialize();

        //check out files tracked by update
        for (String filename : update.getContents().keySet()) {
            File f = new File(filename);
            if (f.exists() && !(curr.getContents().keySet().contains(filename)
                    && !currStage.getRemovedFiles().containsKey(filename))) {
                System.out.println("There is an untracked file in the way;"
                        + " delete it or add it first.");
                return;
            }
            Utils.writeContents(f, update.filenameToFindBlob().get(filename).getContent());
            //checkOut1(filename);
        }
        //remove tracked files not in update
        for (String filename : curr.getContents().keySet()) {
            if (!update.getContents().keySet().contains(filename)) {
                new File(System.getProperty("user.dir"), filename).delete();
            }

        }

        // Moves the current branch’s head pointer
        // and the head pointer to that commit node
        currCommit = update.getcurrID();
        getBranch(currBranch).setcommit(currCommit);
        stage.clearStage();
        stage.serialize();
        getBranch(currBranch).serialize();
    }

    public void merge(String bname) {
        boolean conflicted = false;
        Branch b = getBranch(bname);
        Stage stage = Stage.deserialize();
        if (!stage.getStagedFiles().isEmpty() || !stage.getRemovedFiles().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            return;
        }
        if (!listOfBranch.contains(b)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (bname.equals(currBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            return;
        }
        Commit mergeB = Commit.deserialize(b.getCommitID());
        Commit curr = Commit.deserialize(currCommit);
        //find split point
        ArrayList<String> B = new ArrayList<>();
        B.add(mergeB.getcurrID());
        while (mergeB.getNumCount() > 0) {
            mergeB = Commit.deserialize(mergeB.getParentID());
            B.add(mergeB.getcurrID());
        }
        while (curr.getNumCount() > 0 && !B.contains(curr.getcurrID())) {
            curr = Commit.deserialize(curr.getParentID());
        }
        Commit splitPoint = curr;
        Commit bcommit = Commit.deserialize(b.getCommitID());
        Commit currcommit = Commit.deserialize(currCommit);
        //failure if there's no split
        if (splitPoint.getcurrID().equals(bcommit.getcurrID())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        }
        //if master needs to move forward
        if (splitPoint.getcurrID().equals(currCommit)) {
            System.out.println("Current branch fast-forwarded.");
            currBranch = b.getBranchName();
            b.setcommit(currCommit);
            currCommit = b.getCommitID();
            return;
        }
        mergehelp(splitPoint, currcommit, bcommit);
        //conflicts
        for (String filename : currcommit.getFileNames()) {
            if (bcommit.containsFile(filename)) {
                if (!currcommit.getblobID(filename).equals
                        (bcommit.getblobID(filename))) {
                    if (!conflicted) {
                        System.out.println("Encountered a merge conflict.");
                        conflicted = true;
                    }
                    writeconflict(filename, b);
                }
            }
        }
        for (String filename : bcommit.getFileNames()) {
            if (currcommit.containsFile(filename)) {
                if (!currcommit.getblobID(filename).equals
                        (bcommit.getblobID(filename))) {
                    if (!conflicted) {
                        System.out.println("Encountered a merge conflict.");
                        conflicted = true;
                    }
                    writeconflict(filename, b);
                }
            }
        }
        if (!conflicted) {
            commit("Merged " + currBranch + " with " + b.getBranchName() + ".");
        }
    }

    public void mergehelp(Commit splitPoint, Commit currcommit, Commit bcommit) {
        for (String filename : splitPoint.getFileNames()) {
            if (currcommit.containsFile(filename)) {
                if (splitPoint.getblobID(filename).equals(currcommit.getblobID(filename))) {
                    if (!bcommit.containsFile(filename)) {
                        remove(filename);
                    } else if (!splitPoint.getblobID(filename).equals
                            (bcommit.getblobID(filename))) {
                        File file = new File(filename);
                        Utils.writeContents(file, bcommit
                                .getBlob(filename).getContent());
                        add(filename);
                    }
                }
            }
        }
        for (String filename : bcommit.getFileNames()) {
            if (!splitPoint.containsFile(filename)
                    && !currcommit.containsFile(filename)) {
                File file = new File(filename);
                Utils.writeContents(file, bcommit
                        .getBlob(filename).getContent());
                add(filename);

            }
        }
    }

    public void writeconflict(String filename, Branch b) {
        File file = new File(filename);
        Commit currcommit = Commit.deserialize(currCommit);
        String ret = "<<<<<<< HEAD\n";
        if (!file.exists()) {
            Utils.writeContents(file, new byte[]{});
        }
        if (currcommit.containsFile(filename)) {
            ret = ret + (currcommit.blobof(filename).getString());
        }
        ret += ("=======\n");
        if (Commit.deserialize(b.getCommitID())
                .getFileNames().contains(filename)) {
            ret = ret + (Commit.deserialize(b.getCommitID())
                    .blobof(filename).getString());
        }
        ret += (">>>>>>>\n");
        Utils.writeContents(file, ret.getBytes());

    }


    public void serialize() {
        Utils.serialize(Main.dir(), "tree", this);
    }
}

