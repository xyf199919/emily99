package gitlet;
import java.sql.Timestamp;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.io.ObjectInputStream;
import java.util.Date;
import java.io.Serializable;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Collection;


public class Commit implements Serializable {
    // Time when the branch was created
    private static final SimpleDateFormat FORMAT =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * A hashmap from abbrevs to full codes.
     */

    //The SHA-1 identifier of parent, or null if I am the initial commit.
    private final String parentId; // My log message.
    private final String message;
    private Calendar calendar;
    private Date commitDate;
    private Timestamp time;
    //Get Commit
    private String commitID;
    private int numCount;

    //A mapping of file names to the SHA-1’s of their blobs
    private HashMap<String, String> contents = new HashMap<String, String>() {
    }; // Constructor

    public Commit(String message, String parentid, List<Blob> blobs) {
        parentId = parentid;
        calendar = Calendar.getInstance();
        commitDate = calendar.getTime();
        time = new Timestamp(commitDate.getTime());
        this.message = message;
        for (Blob i : blobs) {
            contents.put(i.getFileName(), i.gethashID());
        }

        commitDate = new Date();
        //setDateFormat(time);
        //commitDate = time.toString();

        String fileNames = "";
        Commit parentCommit = Commit.deserialize(parentId);
        for (String name : contents.keySet()) {
            fileNames.concat(name);
        }

        String iDs = "";
        for (String name : contents.values()) {
            iDs.concat(name);
        }
        commitID = Utils.sha1(this.message, time.toString(),
            fileNames, iDs, parentid);
        numCount = parentCommit.getNumCount() + 1;


        serialize();


    } // Methods


    public Commit() {
        calendar = Calendar.getInstance();
        commitDate = calendar.getTime();
        time = new Timestamp(commitDate.getTime());
        message = "initial commit";
        parentId = "1";
        commitDate = new Date();
        //setDateFormat(time);
        //commitDate = time.toString();
        numCount = 0;
        commitID = Utils.sha1(message);
        serialize();

    }

     // convert the sha id into the commit oject
    public static Commit deserialize(String name) {
        Commit outcome;
        File inFile = new File(Main.commitdir(), name);
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(inFile));
            outcome = (Commit) inp.readObject();
            inp.close();
        } catch (IOException excp) {
            outcome = null;
        } catch (ClassNotFoundException excp2) {
            throw new Error("CNFE Error.");
        }
        return outcome;
    }

    public void put(String name, String sha1) {
        contents.put(name, sha1);
    }

    //public void setDateFormat(Date date) {
    // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //commitDate = dateFormat.format(date);
    //}
    public String getblobID(String name) {
        return contents.get(name);
    }

    public void remove(String name) {
        contents.remove(name);
    }

    public boolean containsFile(String fileShaID) {
        return contents.containsKey(fileShaID);
    }

    public HashMap<String, String> getContents() {
        return contents;
    }

    //Get SHA-1 identifier of my parent, or null if I am the initial commit.
    public String getParentID() {
        return parentId;
    } // get parent commit

    public String getMessage() {
        return message;
    }

    public String getcurrID() {
        return commitID;
    }

    public String getCommitDate() {
        return commitDate.toString();
    }

    // get blobs.{}
    public HashMap<String, String> getBlobs() {
        if (contents == null) {
            return new HashMap<>();
        }
        return contents;
    }

    public Collection<String> getFileNames() {
        return contents.keySet();
    }

    public Blob getBlob(String name) {
        String iD = contents.get(name);
        return Blob.deserialize(Main.dir(), iD);
    }
    //map the filename to the blob in the commit
    public HashMap<String, Blob> filenameToFindBlob() {
        Blob temp;
        HashMap<String, Blob> findBlob = new HashMap<String, Blob>();
        for (String each : contents.keySet()) {
            temp = Blob.deserialize(Main.dir(), contents.get(each));
            findBlob.put(each, temp);
        }
        return findBlob;
    }

    public int getNumCount() {
        return numCount;
    }

    public Blob blobof(String filename) {
        return filenameToFindBlob().get(filename);
    }
    //sha1 data commit message

    public Commit getcurrCommit(String sha1) {
        return Commit.deserialize(sha1);
    }

    public String toString() {
        String result = "===" + "\n" + "Commit "
            + this.commitID + "\n" + FORMAT.format(this.time)
            + "\n" + this.message
            + "\n";
        return result;
    }

    public void printCommit() {
        System.out.println(this.toString());
    }

    public void serialize() {
        Utils.serialize(Main.commitdir(), this.commitID, this);
    }


}
