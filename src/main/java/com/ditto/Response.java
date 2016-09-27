package com.ditto;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP response.
 */
public class Response {
    private int statusCode;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public Response(String fullResponse) {
        String[] lines = fullResponse.split("\\r\\n");
        for (int i = 0; i < lines.length; i++) {
            if (i == 0) {
                statusCode = Integer.parseInt(lines[i].split(" ")[1]);
                continue;
            }
            if (lines[i].trim().isEmpty()) {
                break;
            }
            String[] header = lines[i].split(":", 2);
            headers.put(header[0], header[1]);
        }

        String[] sections = fullResponse.split("\\r\\n\\r\\n");
        if (sections.length > 1 && !sections[1].trim().isEmpty()) {
            body = sections[1].trim();
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response = (Response) o;

        if (statusCode != response.statusCode) return false;
        if (headers != null ? !headers.equals(response.headers) : response.headers != null) return false;
        return body != null ? body.equals(response.body) : response.body == null;

    }

    @Override
    public int hashCode() {
        int result = statusCode;
        result = 31 * result + (headers != null ? headers.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
