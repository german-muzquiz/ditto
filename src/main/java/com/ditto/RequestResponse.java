package com.ditto;

/**
 * Holds a single pair of request/response.
 */
public class RequestResponse {
    private String request;
    private Response response;

    private String method;
    private String url;
    private String requestBody;

    public RequestResponse(String messagePair) {
        String[] pair = messagePair.split("(?=\\r\\nHTTP)");
        request = pair[0];
        response = new Response(pair[1].substring(2)); // Remove empty line at beginning of string
        parseMethod();
        parseUrl();
        parseRequestBody();
    }

    private void parseMethod() {
        method = request.split(" ")[0].replace("\r", "").replace("\n", "");
    }

    private void parseUrl() {
        url = request.split(" ")[1].replace("\r", "").replace("\n", "");
    }

    private void parseRequestBody() {
        String[] sections = request.split("\\r\\n\\r\\n");
        if (sections.length > 1 && !sections[1].trim().isEmpty()) {
            requestBody = sections[1].trim();
        }
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Response getResponse() {
        return response;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
