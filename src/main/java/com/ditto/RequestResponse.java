package com.ditto;

/**
 * Holds a single pair of request/response.
 */
public class RequestResponse {
    private Request request;
    private Response response;

    public RequestResponse(String messagePair) {
        String[] pair = messagePair.split("(?=\\r\\nHTTP)");
        request = new Request(pair[0]);
        response = new Response(pair[1].substring(2)); // Remove empty line at beginning of string
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}
