package com.krishnatech.mobile.http;

import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class URLBasedRestCommunicator {

    private static final String LOGGER_TAG = "URLCommunicate";

    public static Response communicate(RequestType requestMethod, String url, String bodyParams, HashMap<String, String> header) {

        if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
            Log.i(LOGGER_TAG, "Requesting service: " + url);
        }

        Response response = new Response();
        disableConnectionReuseIfNecessary();

        HttpURLConnection urlConnection = null;
        try {
            URL urlToRequest = new URL(url);
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setDoInput(true);

            // handle POST parameters
            if (requestMethod == RequestType.POST) {

                if (Log.isLoggable(LOGGER_TAG, Log.INFO)) {
                    Log.i(LOGGER_TAG, "POST parameters: " + bodyParams);
                }

                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod(requestMethod.name());

            } else if (requestMethod == RequestType.GET) {
                urlConnection.setDoOutput(false);
            }

            urlConnection.setRequestProperty("connection", "keep-alive");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            if (bodyParams != null) {
                urlConnection.setRequestProperty("content-length", String.valueOf(bodyParams.length()));
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                urlConnection.setFixedLengthStreamingMode(bodyParams.getBytes().length);
            }
            // add request headers
            addRequestHeaders(urlConnection, header);

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(bodyParams);
            out.flush();
            out.close();

            int statusCode = urlConnection.getResponseCode();

            switch (statusCode) {
                case 200:
                    // create JSON object from content
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    JSONObject jsonObject = new JSONObject(getResponseText(in));
                    response.setJsonObject(jsonObject);
                    Log.i(LOGGER_TAG, "Response params: " + jsonObject);

                    // retrieve header from url
                    HashMap<String, String> responseHeader = new HashMap<String, String>();
                    Map<String, List<String>> headers = urlConnection.getHeaderFields();
                    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                        String headerValue = entry.getValue().toString();
                        headerValue = headerValue.replace("[", "");
                        headerValue = headerValue.replace("]", "");
                        responseHeader.put(entry.getKey(), headerValue);
                    }
                    response.setHeader(responseHeader);
                    Log.i(LOGGER_TAG, "HEADER params: " + responseHeader);

                    return response;

                default:
                    response.setHttpException(new Exception("HTTP exception status received: "+statusCode));
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
            response.setHttpException(e);

        } catch (JSONException e) {
            e.printStackTrace();
            response.setHttpException(e);

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return response;
    }

    private static void addRequestHeaders(HttpURLConnection urlConnection, HashMap<String, String> header) {
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * required in order to prevent issues in earlier Android version.
     */
    private static void disableConnectionReuseIfNecessary() {
        // see HttpURLConnection API doc
        if (Integer.parseInt(Build.VERSION.SDK)
                < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        return new Scanner(inStream).useDelimiter("\\A").next();
    }

    public enum RequestType {
        GET, POST
    }

    static public class Response {

        JSONObject jsonObject;
        Map<String, String> header;
        Exception httpException;

        public void setJsonObject(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        public JSONObject getJsonObject() {
            return this.jsonObject;
        }

        public void setHeader(Map<String, String> header) {
            this.header = header;
        }

        public Map<String, String> getHeader() {
            return this.header;
        }

        public Exception getHttpException() {
            return httpException;
        }

        public void setHttpException(Exception httpException) {
            this.httpException = httpException;
        }
    }
}
