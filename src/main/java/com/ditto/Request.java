package com.ditto;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Represents an HTTP request.
 */
public class Request {
    private String method;
    private String url;
    private Map<String, List<String>> queryParams = new HashMap<>();
    private String body;
    private Map<String, String> headers = new HashMap<>();

    public Request(String requestFromFile) {
        String lineBreak = "\\n";
        if (requestFromFile.contains("\r\n")) {
            lineBreak = "\\r\\n";
        }
        parseMethod(requestFromFile);
        parseUrl(requestFromFile);
        parseQueryParams(requestFromFile);
        parseHeaders(requestFromFile, lineBreak);
        parseRequestBody(requestFromFile, lineBreak);
    }

    private void parseMethod(String request) {
        method = request.split(" ")[0].replace("\r", "").replace("\n", "");
    }

    private void parseUrl(String request) {
        url = request.split(" ")[1].replace("\r", "").replace("\n", "");
        url = url.replaceFirst("\\?.*", "");
    }

    private void parseQueryParams(String request) {
        String fullUrl = request.split(" ")[1].replace("\r", "").replace("\n", "");
        if (!fullUrl.contains("?")) {
            return;
        }

        String paramsString = fullUrl.replaceFirst(".*\\?", "");

        if (paramsString.isEmpty()) {
            return;
        }

        String[] params = paramsString.split("&");
        for (String param : params) {
            String[] pair = param.split("=");
            String paramValue = pair.length > 1 ? pair[1] : null;
            queryParams.putIfAbsent(pair[0], new ArrayList<>());
            queryParams.get(pair[0]).add(paramValue);
        }
    }

    private void parseHeaders(String request, String lineBreak) {
        String[] sections = request.split(lineBreak + lineBreak);
        String[] headerLines = sections[0].split(lineBreak);
        for (int i = 1; i < headerLines.length; i++) {
            String headerName = headerLines[i].replaceAll(":.*", "");
            String headerValue = headerLines[i].replaceAll(".*?:", "").trim();
            headers.put(headerName, headerValue);
        }
    }

    private void parseRequestBody(String request, String lineBreak) {
        String[] sections = request.split(lineBreak + lineBreak);
        if (sections.length > 1 && !sections[1].trim().isEmpty()) {
            body = sections[1].trim();
        }
    }

    public boolean matchesRequest(spark.Request req) throws UnsupportedEncodingException {
        String myUrl = req.url().replaceFirst(".*//.*?/", "/");
        return matchesUrl(myUrl) &&
                matchesQueryParams(req) &&
                matchesBody(req);
    }

    private boolean matchesUrl(String url) {
        return url.equals(this.url);
    }

    private boolean matchesQueryParams(spark.Request req) throws UnsupportedEncodingException {
        for (Map.Entry<String, List<String>> param : queryParams.entrySet()) {
            String paramFromRequest = req.queryParams(param.getKey());
            String paramFromFile = URLDecoder.decode(param.getValue().get(0), "UTF-8");

            if (paramFromRequest == null ||
               !paramFromRequest.equals(paramFromFile)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesBody(spark.Request req) {
        if (!hasBodyMatchWildcard()) {
            if (this.body == null || this.body.isEmpty()) {
                return req.body() == null || req.body().isEmpty();
            }

            return this.body.equals(req.body().trim());

        } else {
            String incomingBody = req.body().trim().replaceAll("\\r", "").replaceAll("\\n", "");
            String regexBody = Pattern.quote(this.body.replaceAll("\\r", "").replaceAll("\\n", "")).replaceAll("\\*","\\\\E.*\\\\Q");
            return incomingBody.matches(regexBody);
        }
    }

    private boolean hasBodyMatchWildcard() {
        for (String headerName : headers.keySet()) {
            if (headerName.equalsIgnoreCase(Constants.HEADER_BODY_MATCH)) {
                return true;
            }
        }
        return false;
    }

    public String getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "Request{" +
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", queryParams=" + queryParams +
                ", body='" + body + '\'' +
                '}';
    }
}
