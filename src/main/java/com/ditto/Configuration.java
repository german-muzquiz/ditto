package com.ditto;

import java.io.File;
import java.net.URL;

/**
 * Properties to run Ditto.
 */
public class Configuration {

    public enum Mode {record, replay}

    private int listeningPort;
    private File messagesFile;
    private Mode mode;
    private URL destination;

    public Configuration(String[] args) {
        if (args.length < 2) {
            printGeneralUsage();
            System.exit(1);
        }

        parseMode(args[0]);
        parsePort(args[1]);
        parseMessagesFile(args);
        parseDestination(args);
    }

    private void parseMode(String arg) {
        try {
            mode = Mode.valueOf(arg);
        } catch (Exception anEx) {
            System.out.println("Invalid mode: " + arg);
            printGeneralUsage();
            System.exit(1);
        }
    }

    private void parsePort(String arg) {
        try {
            this.listeningPort = Integer.parseInt(arg);
        } catch (Exception anEx) {
            System.out.println("Invalid port: " + arg);
            printUsage();
            System.exit(1);
        }
    }

    private void parseMessagesFile(String[] args) {
        String arg = null;
        switch (mode) {
            case record:
                if (args.length < 4) {
                    printUsage();
                    System.exit(1);
                }
                arg = args[3];
                break;
            case replay:
                if (args.length < 3) {
                    printUsage();
                    System.exit(1);
                }
                arg = args[2];
                break;
        }

        if (arg == null || arg.isEmpty()) {
            System.out.println("Invalid message file: " + arg);
            printUsage();
            System.exit(1);
        }

        File file = new File(arg);
        if (mode == Mode.replay && !file.exists()) {
            System.out.println("File doesn't exist: " + file.getAbsolutePath());
            printReplayUsage();
            System.exit(1);
        }

        messagesFile = file;
    }

    private void parseDestination(String[] args) {
        if (mode == Mode.replay) {
            return;
        }

        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }

        String arg = args[2];
        if (arg == null || arg.isEmpty()) {
            System.out.println("Invalid destination: " + arg);
            printUsage();
            System.exit(1);
        }

        try {
            destination = new URL(arg);
        } catch (Exception anEx) {
            System.out.println("Invalid destination: " + arg);
            printUsage();
            System.exit(1);
        }
    }

    private void printUsage() {
        switch (mode) {
            case record:
                printRecordUsage();
                break;
            case replay:
                printReplayUsage();
                break;
        }
    }

    private void printGeneralUsage() {
        System.out.println("Usage: java -jar ditto.jar [record|replay]");
    }

    private void printReplayUsage() {
        System.out.println("Usage: java -jar ditto.jar replay <port> <messagesFile>");
        System.out.println("Where:");
        System.out.println("    port: port to listen for incoming requests");
        System.out.println("    messagesFile: text file containing requests/responses to replicate");
    }

    private void printRecordUsage() {
        System.out.println("Usage: java -jar ditto.jar record <port> <destination> <messagesFile>");
        System.out.println("Where:");
        System.out.println("    port:         port to listen for incoming requests");
        System.out.println("    destination:  real destination to which forward requests");
        System.out.println("    messagesFile: text file containing requests/responses to replicate");
    }

    public Mode getMode() {
        return mode;
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public File getMessagesFile() {
        return messagesFile;
    }

    public URL getDestination() {
        return destination;
    }
}
