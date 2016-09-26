package com.ditto;

import java.util.HashMap;
import java.util.Map;

/**
 * Hold available messages by body.
 */
public class MessagesByBody {
    private Map<String, Response> responsesByBody = new HashMap<>();
    private Response responseWithoutBody;

    public void addRequestResponse(RequestResponse requestResponse) {
        if (requestResponse.getRequestBody() == null || requestResponse.getRequestBody().isEmpty()) {
            this.responseWithoutBody = requestResponse.getResponse();

        } else {
            responsesByBody.put(requestResponse.getRequestBody(), requestResponse.getResponse());
        }
    }

    public Response getResponse(String requestBody) {
        if (requestBody == null) {
            return responseWithoutBody;
        } else {
            return responsesByBody.get(requestBody);
        }
    }

    public Response getResponse() {
        return responseWithoutBody;
    }

    public Map<String, Response> getResponsesByBody() {
        return responsesByBody;
    }
}
