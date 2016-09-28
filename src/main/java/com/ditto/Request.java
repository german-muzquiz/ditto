package com.ditto;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an HTTP request.
 */
public class Request {
    private String method;
    private String url;
    private Map<String, String> queryParams = new HashMap<>();
    private String body;

    public Request(String requestFromFile) {
        parseMethod(requestFromFile);
        parseUrl(requestFromFile);
        parseQueryParams(requestFromFile);
        parseRequestBody(requestFromFile);
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
            queryParams.put(pair[0], paramValue);
        }
    }

    private void parseRequestBody(String request) {
        String[] sections = request.split("\\r\\n\\r\\n");
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
        for (Map.Entry<String, String> param : queryParams.entrySet()) {
            String paramFromRequest = req.queryParams(param.getKey());
            String paramFromFile = URLDecoder.decode(param.getValue(), "UTF-8");

            if (paramFromRequest == null ||
               !paramFromRequest.equals(paramFromFile)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesBody(spark.Request req) {
        if (this.body == null || this.body.isEmpty()) {
            return req.body() == null || req.body().isEmpty();
        }

        return this.body.equals(req.body().trim());
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getBody() {
        return body;
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
