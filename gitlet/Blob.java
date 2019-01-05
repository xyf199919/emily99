package gitlet;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.FileInputStream;

public class Blob implements Serializable {
    private byte[] blob;
    private String fileName;
    private String hashID;

    //constructor
    public Blob(File file) {
        blob = Utils.readContents(file);
        fileName = file.getName();
        hashID = Utils.sha1(blob, fileName);
    }

    static Blob deserialize(File dir, String name) {
        Blob outcome;
        File inFile = new File(dir, name);
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(inFile));
            outcome = (Blob) inp.readObject();
            inp.close();
        } catch (IOException excp) {
            outcome = null;
        } catch (ClassNotFoundException e) {
            throw new Error("CNFE Error.");
        }
        return outcome;
    }
    public String getString() {
        return new String(this.blob);
    }

    byte[] getContent() {
        return blob;
    }

    String getFileName() {
        return fileName;
    }

    String gethashID() {
        return this.hashID;
    }

    public void serialize() {
        Utils.serialize(Main.dir(), this.gethashID(), this);
    }
}
