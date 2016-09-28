package com.ditto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static spark.Spark.*;


/**
 * Intercept requests and answer from messages file, or proxy to real destination.
 */
public class Interceptor {
    private Replayer replayer;
    private Recorder recorder;

    public Interceptor(Replayer replayer, Recorder recorder) throws IOException {
        this.replayer = replayer;
        this.recorder = recorder;
    }

    public void start() throws FileNotFoundException, UnsupportedEncodingException, NoSuchAlgorithmException, KeyManagementException {
        setUpGetEndpoints();
        setUpHeadEndpoints();
        setUpDeleteEndpoints();
        setUpPostEndpoints();
        setUpPutEndpoints();
        replayer.start();
        recorder.start();
    }

    private void setUpGetEndpoints() {
        get("/*", (req, res) -> {
            Object response = replayer.handleRequest("GET", req, res);
            if (response != null) {
                return response;
            } else {
                return recorder.handleGetRequest(req, res);
            }
        });
    }

    private void setUpHeadEndpoints() {
        head("/*", (req, res) -> {
            Object response = replayer.handleRequest("HEAD", req, res);
            if (response != null) {
                return response;
            } else {
                return recorder.handleHeadRequest(req, res);
            }
        });
    }

    private void setUpDeleteEndpoints() {
        delete("/*", (req, res) -> {
            Object response = replayer.handleRequest("DELETE", req, res);
            if (response != null) {
                return response;
            } else {
                return recorder.handleDeleteRequest(req, res);
            }
        });
    }

    private void setUpPostEndpoints() {
        post("/*", (req, res) -> {
            Object response = replayer.handleRequest("POST", req, res);
            if (response != null) {
                return response;
            } else {
                return recorder.handlePostRequest(req, res);
            }
        });
    }

    private void setUpPutEndpoints() {
        put("/*", (req, res) -> {
            Object response = replayer.handleRequest("PUT", req, res);
            if (response != null) {
                return response;
            } else {
                return recorder.handlePutRequest(req, res);
            }
        });
    }
}
