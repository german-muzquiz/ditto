package com.ditto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

        switch (conf.getMode()) {
            case replay:
                Replayer replayer = new Replayer(conf);
                replayer.start();
                break;
            case record:
                Recorder recorder = new Recorder(conf, true);
                recorder.start();
                break;
            case intercept:
                Interceptor interceptor = new Interceptor(new Replayer(conf), new Recorder(conf, false));
                interceptor.start();
                break;
        }
    }

}
