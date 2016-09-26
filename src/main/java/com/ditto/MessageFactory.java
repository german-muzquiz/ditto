package com.ditto;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This class has all the request/response combinations supported by Ditto.
 */
public class MessageFactory {
    private Map<String, MessagesByUrl> messagesByMethod = new HashMap<>();

    public static MessageFactory newInstance(Configuration configuration) throws FileNotFoundException {
        Scanner scanner = new Scanner(configuration.getMessagesFile()).useDelimiter("(?=\\n(GET|POST|PUT|DELETE|HEAD))");
        List<RequestResponse> messagePairs = new ArrayList<>();
        while(scanner.hasNext()) {
            messagePairs.add(new RequestResponse(scanner.next() + "\n"));
        }
        return new MessageFactory(messagePairs);
    }

    private MessageFactory(List<RequestResponse> requestResponses) {
        for (RequestResponse requestResponse : requestResponses) {
            messagesByMethod.putIfAbsent(requestResponse.getMethod(), new MessagesByUrl());
            messagesByMethod.get(requestResponse.getMethod()).addRequestResponse(requestResponse);
        }
    }

    public Map<String, Response> responsesGetByUrl() {
        Map<String, Response> responses = new HashMap<>();
        if (messagesByMethod.get("GET") == null) {
            return responses;
        }

        for (Map.Entry<String, MessagesByBody> entry : messagesByMethod.get("GET").getMessagesByUrl().entrySet()) {
            responses.put(entry.getKey(), entry.getValue().getResponse());
        }
        return responses;
    }

    public Map<String, Response> responsesHeadByUrl() {
        Map<String, Response> responses = new HashMap<>();
        if (messagesByMethod.get("HEAD") == null) {
            return responses;
        }

        for (Map.Entry<String, MessagesByBody> entry : messagesByMethod.get("HEAD").getMessagesByUrl().entrySet()) {
            responses.put(entry.getKey(), entry.getValue().getResponse());
        }
        return responses;
    }

    public Map<String, Response> responsesDeleteByUrl() {
        Map<String, Response> responses = new HashMap<>();
        if (messagesByMethod.get("DELETE") == null) {
            return responses;
        }

        for (Map.Entry<String, MessagesByBody> entry : messagesByMethod.get("DELETE").getMessagesByUrl().entrySet()) {
            responses.put(entry.getKey(), entry.getValue().getResponse());
        }
        return responses;
    }

    public Map<String, Map<String, Response>> responsesPostByUrlAndBody() {
        Map<String, Map<String, Response>> responses = new HashMap<>();
        if (messagesByMethod.get("POST") == null) {
            return responses;
        }

        for (Map.Entry<String, MessagesByBody> entry : messagesByMethod.get("POST").getMessagesByUrl().entrySet()) {
            responses.putIfAbsent(entry.getKey(), new HashMap<>());
            responses.get(entry.getKey()).putAll(entry.getValue().getResponsesByBody());
        }
        return responses;
    }

    public Map<String, Map<String, Response>> responsesPutByUrlAndBody() {
        Map<String, Map<String, Response>> responses = new HashMap<>();
        if (messagesByMethod.get("PUT") == null) {
            return responses;
        }

        for (Map.Entry<String, MessagesByBody> entry : messagesByMethod.get("PUT").getMessagesByUrl().entrySet()) {
            responses.putIfAbsent(entry.getKey(), new HashMap<>());
            responses.get(entry.getKey()).putAll(entry.getValue().getResponsesByBody());
        }
        return responses;
    }
}
