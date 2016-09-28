package com.ditto;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents an HTTP response.
 */
public class Response {
    private int statusCode;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public Response(String fullResponse) throws UnsupportedEncodingException {
        String[] lines = fullResponse.split("\\n");
        int i;
        for (i = 0; i < lines.length; i++) {
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

        // Check to see if there is a body
        if (lines.length > ++i && !lines[i].trim().isEmpty()) {
            String[] bodyLines = Arrays.copyOfRange(lines, i, lines.length);
            String joinedLines = Arrays.stream(bodyLines)
                    .collect(Collectors.joining("\n"));
            body = joinedLines.getBytes("UTF-8");
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
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
                '}';
    }
}
