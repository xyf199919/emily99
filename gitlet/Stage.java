package gitlet;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;


public class Stage implements Serializable {
    //maps filename to blobID
    private TreeMap<String, String> stagedFiles;
    private TreeMap<String, String> removedFiles;


    /**
     * constructor.
     */
    public Stage() {
        this.stagedFiles = new TreeMap<>();
        this.removedFiles = new TreeMap<>();
    }

    /**
     * deserialize helper method.
     */
    public static Stage deserialize() {
        return (Stage) Utils.deserialize(Main.dir(), "stage");
    }

    /**
     * get staged files
     */
    public TreeMap<String, String> getStagedFiles() {
        return this.stagedFiles;
    }

    /**
     * get removed files
     */
    public TreeMap<String, String> getRemovedFiles() {
        return this.removedFiles;
    }

    /**
     * names of removed files
     */
    public Set<String> getRemovedNames() {
        return this.removedFiles.keySet();
    }

    /**
     * names of staged files
     */
    public Set<String> getStagedNames() {
        return this.stagedFiles.keySet();
    }

    /**
     * serialize helper mathod.
     */
    public void serialize() {
        Utils.serialize(Main.dir(), "stage", this);
    }


    /**
     * add a Blob to staging area .
     */
    public void add(Blob b) {
        if (removedFiles.containsKey(b.getFileName())) {
            removedFiles.remove(b.getFileName());

        } else {
            getStagedFiles().put(b.getFileName(), b.gethashID());
        }

    }

    /**
     * remove Blob from stage and put it in removed.
     */
    public void remove(Blob b) {
        if (stagedFiles.containsKey(b.getFileName())) {
            stagedFiles.remove(b.getFileName());
        }

        getRemovedFiles().put(b.getFileName(), b.gethashID());

    }

    /**
     * unstage Blob
     * public void unStage(Blob b) {
     * stagedFiles.remove(b.getFileName());
     * }
     */

    // Returns the blob mapped to the FILENAME.
    public Blob blobOf(String filename) {
        String id = stagedFiles.get(filename);
        return Blob.deserialize(Main.dir(), id);
    }

    /**
     * clear stage
     */
    public void clearStage() {
        stagedFiles.clear();
        removedFiles.clear();
    }

    public void printstage() {
        System.out.println("=== Staged Files ===");

        if (stagedFiles.isEmpty()) {
            return;
        }
        for (String name : getStagedNames()) {
            System.out.println(name);
        }
    }

    public void printremoved() {
        System.out.println("=== Removed Files ===");
        if (removedFiles.isEmpty()) {
            return;
        }
        for (String name : getRemovedNames()) {
            System.out.println(name);
        }
    }

}
