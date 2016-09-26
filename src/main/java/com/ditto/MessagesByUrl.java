package com.ditto;

import java.util.HashMap;
import java.util.Map;

/**
 * Hold available messages by URL.
 */
public class MessagesByUrl {
    private Map<String, MessagesByBody> messagesByUrl = new HashMap<>();


    public void addRequestResponse(RequestResponse requestResponse) {
        messagesByUrl.putIfAbsent(requestResponse.getUrl(), new MessagesByBody());
        messagesByUrl.get(requestResponse.getUrl()).addRequestResponse(requestResponse);
    }

    public Response getResponse(String url) {
        return getResponse(url, null);
    }

    public Response getResponse(String url, String body) {
        MessagesByBody messagesByBody = messagesByUrl.get(url);
        return messagesByBody.getResponse(body);
    }

    public Map<String, MessagesByBody> getMessagesByUrl() {
        return new HashMap<>(messagesByUrl);
    }
}
