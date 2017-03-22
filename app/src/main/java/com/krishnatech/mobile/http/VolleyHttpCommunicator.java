package com.krishnatech.mobile.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleyHttpCommunicator extends JsonObjectRequest {

    private final Map<String, String> requestHeader;
    private final Context context;
    private final int requestId;
    private VolleyResultCallback volleyResultCallback;

    public VolleyHttpCommunicator(Context context,
                                  int requestId,
                                  int method,
                                  String url,
                                  JSONObject bodyParams,
                                  Map<String, String> requestHeader,
                                  VolleyResultCallback volleyResultCallback) {

        super(method, url, bodyParams,
                new ResponseListener(requestId, volleyResultCallback),
                new ErrorListener(requestId, volleyResultCallback));
        this.context = context;
        this.requestId = requestId;
        this.volleyResultCallback = volleyResultCallback;
        this.requestHeader = requestHeader;
    }

    public void execute() {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueue.add(this);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        this.volleyResultCallback.onResponse(requestId, response);
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        if (requestHeader != null) {
            for (Map.Entry<String, String> entry : requestHeader.entrySet()) {
                headers.put(entry.getKey(), entry.getValue());
            }
        }
        return headers;
    }

    public static class ResponseListener implements Response.Listener<JSONObject> {


        private final VolleyResultCallback volleyResultCallback;
        private final int requestId;

        public ResponseListener(int requestId, VolleyResultCallback volleyResultCallback) {
            this.volleyResultCallback = volleyResultCallback;
            this.requestId = requestId;
        }

        @Override
        public void onResponse(JSONObject response) {
            volleyResultCallback.onResponse(requestId, response);
        }
    }

    public static class ErrorListener implements Response.ErrorListener {

        private final VolleyResultCallback volleyResultCallback;
        private final int requestId;

        public ErrorListener(int requestId, VolleyResultCallback volleyResultCallback) {
            this.volleyResultCallback = volleyResultCallback;
            this.requestId = requestId;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
           /* Log.d(LoginActivity.class.getSimpleName(), "Error: " + error
                    + "\nStatus Code " + error.networkResponse.statusCode
                    + "\nResponse Data " + error.networkResponse.data
                    + "\nCause " + error.getCause()
                    + "\nmessage" + error.getMessage());*/
            error.printStackTrace();
            volleyResultCallback.onErrorResponse(requestId, error);
        }
    }

    public interface VolleyResultCallback {

        void onResponse(int requestId, JSONObject response);

        void onErrorResponse(int requestId, VolleyError error);
    }
}
