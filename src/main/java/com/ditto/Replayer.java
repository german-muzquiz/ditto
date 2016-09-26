package com.ditto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.io.FileNotFoundException;
import java.util.Map;

import static spark.Spark.*;


/**
 * Handle incoming requests and supply responses from text file.
 */
public class Replayer {

    private static final Logger LOG = LoggerFactory.getLogger(Ditto.class);
    private Configuration configuration;

    public Replayer(Configuration configuration) {
        this.configuration = configuration;
    }

    public void start() throws FileNotFoundException {
        MessageFactory messageFactory = MessageFactory.newInstance(configuration);
        setUpGetEndpoints(messageFactory);
        setUpHeadEndpoints(messageFactory);
        setUpPostEndpoints(messageFactory);
        setUpPutEndpoints(messageFactory);
        setUpDeleteEndpoints(messageFactory);
    }

    private static void setUpGetEndpoints(MessageFactory messageFactory) {
        for (Map.Entry<String, Response> entry : messageFactory.responsesGetByUrl().entrySet()) {
            get(entry.getKey(), (req, res) -> handleRequestWithoutBody(entry, res));
        }
    }

    private static void setUpHeadEndpoints(MessageFactory messageFactory) {
        for (Map.Entry<String, Response> entry : messageFactory.responsesHeadByUrl().entrySet()) {
            head(entry.getKey(), (req, res) -> handleRequestWithoutBody(entry, res));
        }
    }

    private static void setUpDeleteEndpoints(MessageFactory messageFactory) {
        for (Map.Entry<String, Response> entry : messageFactory.responsesDeleteByUrl().entrySet()) {
            delete(entry.getKey(), (req, res) -> handleRequestWithoutBody(entry, res));
        }
    }
    private static void setUpPostEndpoints(MessageFactory messageFactory) {
        for (Map.Entry<String, Map<String, Response>> entryUrl : messageFactory.responsesPostByUrlAndBody().entrySet()) {
            post(entryUrl.getKey(), (req, res) -> handleRequestWithBody(entryUrl, req, res));
        }
    }

    private static void setUpPutEndpoints(MessageFactory messageFactory) {
        for (Map.Entry<String, Map<String, Response>> entryUrl : messageFactory.responsesPutByUrlAndBody().entrySet()) {
            put(entryUrl.getKey(), (req, res) -> handleRequestWithBody(entryUrl, req, res));
        }
    }

    private static Object handleRequestWithoutBody(Map.Entry<String, Response> entry, spark.Response res) {
        res.status(entry.getValue().getStatusCode());
        for (Map.Entry<String, String> header : entry.getValue().getHeaders().entrySet()) {
            if (header.getKey().equalsIgnoreCase("Content-Length")) {
                continue;
            }
            res.header(header.getKey(), header.getValue());
        }
        res.body(entry.getValue().getBody());
        return res.body() != null ? res.body() : "";
    }

    private static Object handleRequestWithBody(Map.Entry<String, Map<String, Response>> entryUrl, Request req, spark.Response res) {
        Map<String, Response> responsesByRequestBody = entryUrl.getValue();
        if (responsesByRequestBody.containsKey(req.body())) {
            Response response = responsesByRequestBody.get(req.body());
            res.status(response.getStatusCode());
            for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                if (header.getKey().equalsIgnoreCase("Content-Length")) {
                    continue;
                }
                res.header(header.getKey(), header.getValue());
            }
            res.body(response.getBody());
            return res.body() != null ? res.body() : "";

        } else {
            LOG.error("No response found for " + req.url() + " and body " + req.body());
            res.status(404);
            return null;
        }
    }
}
