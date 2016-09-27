package com.ditto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger(Ditto.class);

    public static void main( String[] args ) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        Configuration conf = new Configuration(args);
        LOG.info("Starting Ditto...");

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
