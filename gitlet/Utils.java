package gitlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.io.FilenameFilter;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;


/* Assorted utilities.
   @author P. N. Hilfinger */
class Utils {

    /* SHA-1 HASH VALUES. */

    /* Filter out all but plain files. */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /* Returns the SHA-1 hash of the concatenation of VALS, which may be any
       mixture of byte arrays and Strings. */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /* FILE DELETION */

    /* Returns the SHA-1 hash of the concatenation of the strings in VALS. */
    static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* Deletes FILE if it exists and is not a directory.  Returns true if FILE
       was deleted, and false otherwise.  Refuses to delete FILE and throws
       IllegalArgumentException unless the directory designated by FILE also
       contains a directory named .gitlet. */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /* READING AND WRITING FILE CONTENTS */

    /* Deletes the file named FILE if it exists and is not a directory. Returns
       true if FILE was deleted, and false otherwise. Refuses to delete FILE and
       throws IllegalArgumentException unless the directory designated by FILE
       also contains a directory named .gitlet. */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* Write the entire contents of BYTES to FILE, creating or overwriting it as
       needed. Throws IllegalArgumentException in case of problems. */

    /* Return the entire contents of FILE as a byte array. FILE must be a normal
       file. Throws IllegalArgumentException in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /* OTHER FILE UTILITIES */

    static void writeContents(File file, byte[] bytes) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            Files.write(file.toPath(), bytes);
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /* Return the concatentation of FIRST and OTHERS into a File designator,
       analogous to the {@link java.nio.file.Paths.#get(String, String[])}
       method. */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /* DIRECTORIES */

    /* Return the concatentation of FIRST and OTHERS into a File designator,
       analogous to the {@link java.nio.file.Paths.#get(String, String[])}
       method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

    /* Returns a list of the names of all plain files in the directory DIR, in
       lexicographic order as Java Strings. Returns null if DIR does not denote
       a directory. */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    static void serialize(File dir, String name, Object obj) {
        File outFile = new File(dir, name);
        try {
            ObjectOutputStream out =
                new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(obj);
            out.flush();
            out.close();
        } catch (IOException excp) {
            throw new Error("IO Error.");
        }
    }

    static Object deserialize(File dir, String name) {
        Object outcome = new Object();
        File inFile = new File(dir, name);
        try {
            ObjectInputStream inp =
                new ObjectInputStream(new FileInputStream(inFile));
            outcome = inp.readObject();
            inp.close();
        } catch (IOException excp) {
            outcome = null;
        } catch (ClassNotFoundException e) {
            throw new Error("CNFE Error.");
        }
        return outcome;
    }

    /* Returns a list of the names of all plain files in the directory DIR, in
       lexicographic order as Java Strings. Returns null if DIR does not denote
       a directory. */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

}
