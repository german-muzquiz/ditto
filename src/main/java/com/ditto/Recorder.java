package com.ditto;

import spark.Request;
import spark.Response;
import spark.utils.IOUtils;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import static spark.Spark.*;


/**
 * Record incoming requests and forward them to real destination.
 */
public class Recorder {

    private Configuration configuration;
    private Path outputFile;
    private boolean writeMessages;

    public Recorder(Configuration configuration, boolean writeMessages) throws IOException {
        this.configuration = configuration;
        this.outputFile = Paths.get(configuration.getMessagesFile().getAbsolutePath());
        this.writeMessages = writeMessages;
        this.writeMessages = writeMessages;
        if (!configuration.getMessagesFile().exists()) {
            configuration.getMessagesFile().createNewFile();
        }
    }

    public void start() throws KeyManagementException, NoSuchAlgorithmException {
        trustAllSSL();
        setUpGetEndpoint();
        setUpHeadEndpoint();
        setUpPostEndpoint();
        setUpPutEndpoint();
        setUpDeleteEndpoint();
    }

    private void setUpGetEndpoint() {
        get("/*", this::handleGetRequest);
    }

    public Object handleGetRequest(Request req, spark.Response res) throws IOException {
        String queryParams = getQueryParams(req);

        String requestContext = req.url().replaceAll(".*//.*?/", "");
        URL url = new URL(configuration.getDestination().toString() + "/" + requestContext + queryParams);
        StringBuilder recordedMessage = new StringBuilder();

        recordedMessage.append(req.requestMethod()).append(" ").append(req.pathInfo()).append(queryParams);
        recordedMessage.append(" HTTP/1.1\n");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        addHeaders(req, conn, recordedMessage);
        recordedMessage.append("\n");

        conn.setRequestMethod("GET");
        Map<String, String> responseHeaders = parseResponseHeaders(conn.getHeaderFields());
        String responseStatusLine = conn.getHeaderField(0);
        InputStream inputStream = getInputStream(conn, responseHeaders);
        String responseBody = inputStream != null ? IOUtils.toString(inputStream) : "";

        writeResponse(responseBody, responseHeaders, responseStatusLine, recordedMessage);
        prepareReturnResponse(res, conn, responseBody, responseHeaders);

        if (writeMessages) {
            synchronized (Recorder.class) {
                Files.write(outputFile, recordedMessage.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
            }
        }

        return res.body();
    }

    private void setUpHeadEndpoint() {
        head("/*", this::handleHeadRequest);
    }

    public Object handleHeadRequest(Request req, Response res) throws IOException {
        URL url = new URL(configuration.getDestination().toString() + "/" + req.splat()[0]);
        StringBuilder recordedMessage = new StringBuilder();
        recordedMessage.append(req.requestMethod()).append(" ").append(req.pathInfo()).append(" HTTP/1.1\n");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        addHeaders(req, conn, recordedMessage);
        recordedMessage.append("\n");

        conn.setRequestMethod("HEAD");
        Map<String, String> responseHeaders = parseResponseHeaders(conn.getHeaderFields());
        InputStream inputStream = getInputStream(conn, responseHeaders);
        String responseBody = IOUtils.toString(inputStream);
        String responseStatusLine = conn.getHeaderField(0);

        writeResponse(responseBody, responseHeaders, responseStatusLine, recordedMessage);
        prepareReturnResponse(res, conn, responseBody, responseHeaders);

        if (writeMessages) {
            synchronized (Recorder.class) {
                Files.write(outputFile, recordedMessage.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
            }
        }

        return res.body();
    }

    private void setUpDeleteEndpoint() {
        delete("/*", this::handleDeleteRequest);
    }

    public Object handleDeleteRequest(Request req, Response res) throws IOException {
        URL url = new URL(configuration.getDestination().toString() + "/" + req.splat()[0]);
        StringBuilder recordedMessage = new StringBuilder();
        recordedMessage.append(req.requestMethod()).append(" ").append(req.pathInfo()).append(" HTTP/1.1\n");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        addHeaders(req, conn, recordedMessage);
        recordedMessage.append("\n");

        conn.setRequestMethod("DELETE");
        Map<String, String> responseHeaders = parseResponseHeaders(conn.getHeaderFields());
        InputStream inputStream = getInputStream(conn, responseHeaders);
        String responseBody = IOUtils.toString(inputStream);
        String responseStatusLine = conn.getHeaderField(0);

        writeResponse(responseBody, responseHeaders, responseStatusLine, recordedMessage);
        prepareReturnResponse(res, conn, responseBody, responseHeaders);

        if (writeMessages) {
            synchronized (Recorder.class) {
                Files.write(outputFile, recordedMessage.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
            }
        }

        return res.body();
    }

    private void setUpPostEndpoint() {
        post("/*", this::handlePostRequest);
    }

    public Object handlePostRequest(Request req, Response res) throws IOException {
        URL url = new URL(configuration.getDestination().toString() + "/" + req.splat()[0]);
        StringBuilder recordedMessage = new StringBuilder();
        recordedMessage.append(req.requestMethod()).append(" ").append(req.pathInfo()).append(" HTTP/1.1\n");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        addHeaders(req, conn, recordedMessage);
        recordedMessage.append("\n");
        recordedMessage.append(req.body()).append("\n");
        recordedMessage.append("\n");

        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.getOutputStream().write(req.bodyAsBytes());

        Map<String, String> responseHeaders = parseResponseHeaders(conn.getHeaderFields());
        InputStream inputStream = getInputStream(conn, responseHeaders);
        String responseBody = IOUtils.toString(inputStream);
        String responseStatusLine = conn.getHeaderField(0);

        writeResponse(responseBody, responseHeaders, responseStatusLine, recordedMessage);
        prepareReturnResponse(res, conn, responseBody, responseHeaders);

        if (writeMessages) {
            synchronized (Recorder.class) {
                Files.write(outputFile, recordedMessage.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
            }
        }

        return res.body();
    }

    private void setUpPutEndpoint() {
        put("/*", this::handlePutRequest);
    }

    public Object handlePutRequest(Request req, Response res) throws IOException {
        URL url = new URL(configuration.getDestination().toString() + "/" + req.splat()[0]);
        StringBuilder recordedMessage = new StringBuilder();
        recordedMessage.append(req.requestMethod()).append(" ").append(req.pathInfo()).append(" HTTP/1.1\n");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        addHeaders(req, conn, recordedMessage);
        recordedMessage.append("\n");
        recordedMessage.append(req.body()).append("\n");
        recordedMessage.append("\n");

        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.getOutputStream().write(req.bodyAsBytes());

        Map<String, String> responseHeaders = parseResponseHeaders(conn.getHeaderFields());
        InputStream inputStream = getInputStream(conn, responseHeaders);
        String responseBody = IOUtils.toString(inputStream);
        String responseStatusLine = conn.getHeaderField(0);

        writeResponse(responseBody, responseHeaders, responseStatusLine, recordedMessage);
        prepareReturnResponse(res, conn, responseBody, responseHeaders);

        if (writeMessages) {
            synchronized (Recorder.class) {
                Files.write(outputFile, recordedMessage.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
            }
        }

        return res.body();
    }

    private void prepareReturnResponse(spark.Response res, HttpURLConnection conn, String responseBody,
                                       Map<String, String> responseHeaders) throws IOException {
        res.status(conn.getResponseCode());
        for (Map.Entry<String, String> header : responseHeaders.entrySet()) {
            if (header.getKey().equalsIgnoreCase("Content-Length")) {
                continue;
            }
            if (header.getKey().equalsIgnoreCase("Content-Encoding") && header.getValue().contains("gzip")) {
                continue;
            }
            res.header(header.getKey(), header.getValue());
        }
        res.body(responseBody);
    }

    private void writeResponse(String responseBody, Map<String, String> responseHeaders, String responseStatusLine,
                               StringBuilder recordedMessage) throws IOException {
        recordedMessage.append(responseStatusLine).append("\n");
        for (Map.Entry<String, String> header : responseHeaders.entrySet()) {
            if (header.getKey().equalsIgnoreCase("Content-Encoding") && header.getValue().contains("gzip")) {
                continue;
            }
            recordedMessage.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
        }
        recordedMessage.append("\n").append(responseBody).append("\n\n");
    }

    private void addHeaders(Request req, URLConnection conn, StringBuilder recordedMessage) throws IOException {
        for (String header : req.headers()) {
            recordedMessage.append(header).append(": ").append(req.headers(header)).append("\n");
            conn.setRequestProperty(header, req.headers(header));
        }
    }

    private InputStream getInputStream(HttpURLConnection conn, Map<String, String> responseHeaders) throws IOException {
        try {
            for (String headerName : responseHeaders.keySet()) {
                if (headerName.equalsIgnoreCase("Content-Encoding") && responseHeaders.get(headerName).equalsIgnoreCase("gzip")) {
                    return new GZIPInputStream(conn.getInputStream());
                }
            }
            return conn.getInputStream();

        } catch (Exception anEx) {
            return null;
        }
    }

    private void trustAllSSL() throws NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);

        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, new TrustManager[]{new FakeX509TrustManager()}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    private Map<String, String> parseResponseHeaders(Map<String, List<String>> headers) {
        Map<String, String> myHeaders = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            myHeaders.put(entry.getKey(), entry.getValue().stream().collect(Collectors.joining("; ")));
        }

        return myHeaders;
    }

    private String getQueryParams(spark.Request req) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder("");

        if (req.queryParams().size() > 0) {
            result.append("?");

            for (String param : req.queryParams()) {
                result.append(URLEncoder.encode(param, "UTF-8"));
                if (req.queryParams(param) != null && !req.queryParams(param).isEmpty()) {
                    result.append("=").append(URLEncoder.encode(req.queryParams(param), "UTF-8"));
                }
                result.append("&");
            }

            result.delete(result.length()-1, result.length());
        }

        return result.toString();
    }

    public static class FakeX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }
}
