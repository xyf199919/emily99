package gitlet;

import java.io.File;

/* Driver class for Gitlet, the tiny stupid version-control system.
   @author
*/


public class Main {
    private static final File DIR = new File(".gitlet/");
    private static final File COMMITDIR = new File(".gitlet/commit");
    private static Tree tree;

    public static File dir() {
        return DIR;
    }

    public static File commitdir() {
        return COMMITDIR;
    }

    public static void init() {
        if (DIR.isDirectory() && DIR.exists()) {
            System.out.println("A gitlet version-control system "
                + "already exists in the current directory.");
            return;
        }
        //make .gitlet directory
        DIR.mkdir();
        COMMITDIR.mkdir();
        tree = new Tree();
        tree.init();
    }

    public static void start(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            return;
        }
    }
    public static void main(String[] args) {
        start(args);
        String arg1 = "";
        String arg2 = "";
        String arg3 = "";
        String command = args[0];
        if (!dir().exists() && !command.equals("init")) {
            System.out.println("Not in an initialize gitlet directory.");
            return;
        }
        tree = Tree.deserialize();
        if (args.length > 1) {
            arg1 = args[1];
        }
        if (args.length > 2) {
            arg2 = args[2];
        }
        if (args.length > 3) {
            arg3 = args[3];
        }
        switch (command) {
            case "init":
                init();
                break;
            case "add":
                tree.add(arg1);
                break;
            case "commit":
                tree.commit(arg1);
                break;
            case "rm":
                tree.remove(arg1);
                break;
            case "log":
                tree.log();
                break;
            case "global-log":
                tree.globalLog();
                break;
            case "find":
                tree.find(arg1);
                break;
            case "status":
                tree.status();
                break;
            case "checkout":
                if (args.length == 3) {
                    if (arg1.equals("--")) {
                        tree.checkOut1(arg2);
                    } else {
                        System.out.println("Incorrect operands.");
                    }
                } else if (args.length == 4) {
                    if (arg2.equals("--")) {
                        tree.checkOut2(arg1, arg3);
                    } else {
                        System.out.println("Incorrect operands.");
                    }
                } else {
                    tree.checkOut3(arg1);
                }
                break;
            case "branch":
                tree.branch(arg1);
                break;
            case "rm-branch":
                tree.rmBranch(arg1);
                break;
            case "reset":
                tree.reset(arg1);
                break;
            case "merge":
                tree.merge(arg1);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
        }
        tree.serialize();
    }

    /* all the command should be under this comments*/
}
