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

public class VollyHttpCommunicator extends JsonObjectRequest {

    private final Map<String, String> requestHeader;
    private final Context context;
    private final int requestId;
    private VollyResultCallback vollyResultCallback;

    public VollyHttpCommunicator(Context context,
                                 int requestId,
                                 int method,
                                 String url,
                                 JSONObject bodyParams,
                                 Map<String, String> requestHeader,
                                 VollyResultCallback vollyResultCallback) {

        super(method, url, bodyParams,
                new ResponseListener(requestId, vollyResultCallback),
                new ErrorListener(requestId, vollyResultCallback));
        this.context = context;
        this.requestId = requestId;
        this.vollyResultCallback = vollyResultCallback;
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
        this.vollyResultCallback.onResponse(requestId, response);
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


        private final VollyResultCallback vollyResultCallback;
        private final int requestId;

        public ResponseListener(int requestId, VollyResultCallback vollyResultCallback) {
            this.vollyResultCallback = vollyResultCallback;
            this.requestId = requestId;
        }

        @Override
        public void onResponse(JSONObject response) {
            vollyResultCallback.onResponse(requestId, response);
        }
    }

    public static class ErrorListener implements Response.ErrorListener {

        private final VollyResultCallback vollyResultCallback;
        private final int requestId;

        public ErrorListener(int requestId, VollyResultCallback vollyResultCallback) {
            this.vollyResultCallback = vollyResultCallback;
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
            vollyResultCallback.onErrorResponse(requestId, error);
        }
    }

    public interface VollyResultCallback {

        void onResponse(int requestId, JSONObject response);

        void onErrorResponse(int requestId, VolleyError error);
    }
}
