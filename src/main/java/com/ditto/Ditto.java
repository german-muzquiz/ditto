package com.ditto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static spark.Spark.port;
import static spark.Spark.threadPool;


/**
 * Simple REST service simulator
 */
public class Ditto {

    public static void main( String[] args ) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Configuration conf = new Configuration(args);

        port(conf.getListeningPort());
        threadPool(500);

        if (conf.getMode() == Configuration.Mode.replay) {
            Replayer replayer = new Replayer(conf);
            replayer.start();

        } else if (conf.getMode() == Configuration.Mode.record) {
            Recorder recorder = new Recorder(conf);
            recorder.start();
        }
    }

}
