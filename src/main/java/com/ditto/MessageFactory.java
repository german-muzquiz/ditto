package com.ditto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This class has all the request/response combinations supported by Ditto.
 */
public class MessageFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MessageFactory.class);

    private Map<String, List<RequestResponse>> messagesByMethod = new HashMap<>();

    public static MessageFactory newInstance(Configuration configuration) throws FileNotFoundException, UnsupportedEncodingException {
        LOG.info("Loading messages from " + configuration.getMessagesFile().getAbsolutePath() + "...");
        Scanner scanner = new Scanner(configuration.getMessagesFile()).useDelimiter("(?=\\n(GET|POST|PUT|DELETE|HEAD))");
        List<RequestResponse> messagePairs = new ArrayList<>();
        while(scanner.hasNext()) {
            messagePairs.add(new RequestResponse(scanner.next() + "\n"));
        }

        MessageFactory instance = new MessageFactory(messagePairs);
        LOG.info("Loaded " + messagePairs.size() + " messages.");
        return instance;
    }

    private MessageFactory(List<RequestResponse> requestResponses) {
        for (RequestResponse requestResponse : requestResponses) {
            messagesByMethod.putIfAbsent(requestResponse.getRequest().getMethod(), new ArrayList<>());
            messagesByMethod.get(requestResponse.getRequest().getMethod()).add(requestResponse);
        }
    }

    public List<RequestResponse> responsesGetByUrl() {
        return messagesByMethod.get("GET") != null ? messagesByMethod.get("GET") : new ArrayList<>();
    }

    public List<RequestResponse> responsesHeadByUrl() {
        return messagesByMethod.get("HEAD") != null ? messagesByMethod.get("HEAD") : new ArrayList<>();
    }

    public List<RequestResponse> responsesDeleteByUrl() {
        return messagesByMethod.get("DELETE") != null ? messagesByMethod.get("DELETE") : new ArrayList<>();
    }

    public List<RequestResponse> responsesPostByUrlAndBody() {
        return messagesByMethod.get("POST") != null ? messagesByMethod.get("POST") : new ArrayList<>();
    }

    public List<RequestResponse> responsesPutByUrlAndBody() {
        return messagesByMethod.get("PUT") != null ? messagesByMethod.get("PUT") : new ArrayList<>();
    }
}
