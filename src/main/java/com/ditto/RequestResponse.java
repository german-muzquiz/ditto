package com.ditto;

import java.io.UnsupportedEncodingException;

/**
 * Holds a single pair of request/response.
 */
public class RequestResponse {
    private Request request;
    private Response response;

    public RequestResponse(String messagePair) throws UnsupportedEncodingException {
        String[] pair = messagePair.split("(?=\\nHTTP)");
        request = new Request(pair[0]);
        response = new Response(pair[1].substring(1)); // Remove empty line at beginning of string
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}
