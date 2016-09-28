package com.ditto;

import java.io.File;
import java.net.URL;

/**
 * Properties to run Ditto.
 */
public class Configuration {

    public enum Mode {record, replay, intercept}

    private int listeningPort;
    private File messagesFile;
    private Mode mode;
    private URL destination;

    public Configuration(String[] args) {
        parseMode(args);
        parsePort(args);
        parseMessagesFile(args);
        parseDestination(args);
    }

    private void parseMode(String[] args) {
        if (args.length < 1) {
            printGeneralUsage();
            System.exit(1);
        }

        try {
            mode = Mode.valueOf(args[0]);
        } catch (Exception anEx) {
            System.out.println("Invalid mode: " + args[0]);
            printGeneralUsage();
            System.exit(1);
        }
    }

    private void parsePort(String[] args) {
        if (args.length < 2) {
            printUsage();
            System.exit(1);
        }

        try {
            this.listeningPort = Integer.parseInt(args[1]);
        } catch (Exception anEx) {
            System.out.println("Invalid port: " + args[1]);
            printUsage();
            System.exit(1);
        }
    }

    private void parseMessagesFile(String[] args) {
        String arg = null;
        switch (mode) {
            case intercept:
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
        if ((mode == Mode.replay || mode == Mode.intercept) && !file.exists()) {
            System.out.println("File doesn't exist: " + file.getAbsolutePath());
            printUsage();
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
            case intercept:
                printInterceptUsage();
                break;
        }
    }

    private void printGeneralUsage() {
        System.out.println("Usage: java -jar ditto.jar [record|replay|intercept]");
    }

    private void printReplayUsage() {
        System.out.println("Usage: java -jar ditto.jar replay <port> <messagesFile>");
        System.out.println("Replay: Answer requests with responses read from a file.");
        System.out.println("Where:");
        System.out.println("    port:         port to listen for incoming requests");
        System.out.println("    messagesFile: text file containing requests/responses to replicate");
    }

    private void printRecordUsage() {
        System.out.println("Usage: java -jar ditto.jar record <port> <destination> <messagesFile>");
        System.out.println("Record: Proxy all requests to other service and write all communication to a text file.");
        System.out.println("Where:");
        System.out.println("    port:         port to listen for incoming requests");
        System.out.println("    destination:  real destination to which forward requests");
        System.out.println("    messagesFile: text file to which messages are going to be saved");
    }

    private void printInterceptUsage() {
        System.out.println("Usage: java -jar ditto.jar intercept <port> <destination> <messagesFile>");
        System.out.println("Intercept: Answer matching requests with responses from a text file, and proxy all other requests to other service.");
        System.out.println("Where:");
        System.out.println("    port:         port to listen for incoming requests");
        System.out.println("    destination:  real destination to which forward requests that are not in messagesFile");
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
