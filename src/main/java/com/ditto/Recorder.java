package com.ditto;

import spark.Request;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
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

import static spark.Spark.*;


/**
 * Record incoming requests and forward them to real destination.
 */
public class Recorder {

    private Configuration configuration;
    private Path outputFile;

    public Recorder(Configuration configuration) throws IOException {
        this.configuration = configuration;
        this.outputFile = Paths.get(configuration.getMessagesFile().getAbsolutePath());
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
        get("/*", (req, res) -> {
            URL url = new URL(configuration.getDestination().toString() + "/" + req.splat()[0]);
            StringBuilder recordedMessage = new StringBuilder();
            recordedMessage.append(req.requestMethod()).append(" ").append(req.pathInfo()).append(" HTTP/1.1\r\n");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            addHeaders(req, conn, recordedMessage);
            recordedMessage.append("\r\n\r\n");

            conn.setRequestMethod("GET");
            InputStream inputStream = conn.getInputStream();
            String responseBody = isToString(inputStream);
            Map<String, String> responseHeaders = parseResponseHeaders(conn.getHeaderFields());
            String responseStatusLine = conn.getHeaderField(0);

            writeResponse(responseBody, responseHeaders, responseStatusLine, recordedMessage);
            prepareReturnResponse(res, conn, responseBody, responseHeaders);

            synchronized (this) {
                Files.write(outputFile, recordedMessage.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
            }

            return responseBody;
        });
    }

    private void setUpHeadEndpoint() {
        head("/*", (req, res) -> {
            URL url = new URL(configuration.getDestination().toString() + "/" + req.splat()[0]);
            StringBuilder recordedMessage = new StringBuilder();
            recordedMessage.append(req.requestMethod()).append(" ").append(req.pathInfo()).append(" HTTP/1.1\r\n");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            addHeaders(req, conn, recordedMessage);
            recordedMessage.append("\r\n\r\n");

            conn.setRequestMethod("HEAD");
            InputStream inputStream = conn.getInputStream();
            String responseBody = isToString(inputStream);
            Map<String, String> responseHeaders = parseResponseHeaders(conn.getHeaderFields());
            String responseStatusLine = conn.getHeaderField(0);

            writeResponse(responseBody, responseHeaders, responseStatusLine, recordedMessage);
            prepareReturnResponse(res, conn, responseBody, responseHeaders);

            synchronized (this) {
                Files.write(outputFile, recordedMessage.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
            }

            return responseBody;
        });
    }

    private void setUpDeleteEndpoint() {
        delete("/*", (req, res) -> {
            URL url = new URL(configuration.getDestination().toString() + "/" + req.splat()[0]);
            StringBuilder recordedMessage = new StringBuilder();
            recordedMessage.append(req.requestMethod()).append(" ").append(req.pathInfo()).append(" HTTP/1.1\r\n");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            addHeaders(req, conn, recordedMessage);
            recordedMessage.append("\r\n\r\n");

            conn.setRequestMethod("DELETE");
            InputStream inputStream = conn.getInputStream();
            String responseBody = isToString(inputStream);
            Map<String, String> responseHeaders = parseResponseHeaders(conn.getHeaderFields());
            String responseStatusLine = conn.getHeaderField(0);

            writeResponse(responseBody, responseHeaders, responseStatusLine, recordedMessage);
            prepareReturnResponse(res, conn, responseBody, responseHeaders);

            synchronized (this) {
                Files.write(outputFile, recordedMessage.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
            }

            return responseBody;
        });
    }

    private void setUpPostEndpoint() {
        post("/*", (req, res) -> {
            URL url = new URL(configuration.getDestination().toString() + "/" + req.splat()[0]);
            StringBuilder recordedMessage = new StringBuilder();
            recordedMessage.append(req.requestMethod()).append(" ").append(req.pathInfo()).append(" HTTP/1.1\r\n");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            addHeaders(req, conn, recordedMessage);
            recordedMessage.append("\r\n");
            recordedMessage.append(req.body()).append("\r\n");
            recordedMessage.append("\r\n\r\n");

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream().write(req.bodyAsBytes());

            InputStream inputStream = conn.getInputStream();
            String responseBody = isToString(inputStream);
            Map<String, String> responseHeaders = parseResponseHeaders(conn.getHeaderFields());
            String responseStatusLine = conn.getHeaderField(0);

            writeResponse(responseBody, responseHeaders, responseStatusLine, recordedMessage);
            prepareReturnResponse(res, conn, responseBody, responseHeaders);

            synchronized (this) {
                Files.write(outputFile, recordedMessage.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
            }

            return responseBody;
        });
    }

    private void setUpPutEndpoint() {
        put("/*", (req, res) -> {
            URL url = new URL(configuration.getDestination().toString() + "/" + req.splat()[0]);
            StringBuilder recordedMessage = new StringBuilder();
            recordedMessage.append(req.requestMethod()).append(" ").append(req.pathInfo()).append(" HTTP/1.1\r\n");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            addHeaders(req, conn, recordedMessage);
            recordedMessage.append("\r\n");
            recordedMessage.append(req.body()).append("\r\n");
            recordedMessage.append("\r\n\r\n");

            conn.setRequestMethod("PUT");
            conn.setDoOutput(true);
            conn.getOutputStream().write(req.bodyAsBytes());

            InputStream inputStream = conn.getInputStream();
            String responseBody = isToString(inputStream);
            Map<String, String> responseHeaders = parseResponseHeaders(conn.getHeaderFields());
            String responseStatusLine = conn.getHeaderField(0);

            writeResponse(responseBody, responseHeaders, responseStatusLine, recordedMessage);
            prepareReturnResponse(res, conn, responseBody, responseHeaders);

            synchronized (this) {
                Files.write(outputFile, recordedMessage.toString().getBytes("UTF-8"), StandardOpenOption.APPEND);
            }

            return responseBody;
        });
    }

    private void prepareReturnResponse(spark.Response res, HttpURLConnection conn, String responseBody, Map<String, String> responseHeaders) throws IOException {
        res.status(conn.getResponseCode());
        for (Map.Entry<String, String> header : responseHeaders.entrySet()) {
            if (header.getKey().equalsIgnoreCase("Content-Length")) {
                continue;
            }
            res.header(header.getKey(), header.getValue());
        }
        res.body(responseBody);
    }

    private void writeResponse(String responseBody, Map<String, String> responseHeaders, String responseStatusLine,
                               StringBuilder recordedMessage) throws IOException {
        recordedMessage.append(responseStatusLine).append("\r\n");
        for (Map.Entry<String, String> header : responseHeaders.entrySet()) {
            recordedMessage.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        recordedMessage.append("\r\n").append(responseBody).append("\r\n\r\n\r\n\r\n");
    }

    private void addHeaders(Request req, URLConnection conn, StringBuilder recordedMessage) throws IOException {
        for (String header : req.headers()) {
            recordedMessage.append(header).append(": ").append(req.headers(header)).append("\r\n");
            conn.setRequestProperty(header, req.headers(header));
        }
    }

    private void trustAllSSL() throws NoSuchAlgorithmException, KeyManagementException {
        HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);

        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, new TrustManager[]{new FakeX509TrustManager()}, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    private String isToString(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
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
