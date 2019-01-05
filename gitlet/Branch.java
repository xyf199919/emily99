package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.FileInputStream;

public class Branch implements Serializable {
    private String branchName;
    private String commitID;

    //currentName is the name of the current commit(SHA1)
    public Branch(String branchName, String commitID) {
        this.branchName = branchName;
        this.commitID = commitID;
    }

    public static Branch deserialize(File dir, String name) {
        Branch outcome;
        File inFile = new File(dir, name);
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(inFile));
            outcome = (Branch) inp.readObject();
            inp.close();
        } catch (IOException excp) {
            outcome = null;
        } catch (ClassNotFoundException e) {
            throw new Error("CNFE Error.");
        }
        return outcome;
    }

    public void setcommit(String iD) {
        this.commitID = iD;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getPreviousCommitId() {
        return getCommit().getParentID();
    }

    public String getCommitID() {
        return commitID;
    }

    public Commit getCommit() {
        return Commit.deserialize(commitID);
    }

    public void serialize() {
        Utils.serialize(Main.dir(), this.getBranchName(), this);
    }
}
