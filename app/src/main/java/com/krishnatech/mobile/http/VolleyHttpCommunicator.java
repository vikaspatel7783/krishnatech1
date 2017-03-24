package com.krishnatech.mobile.http;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VolleyHttpCommunicator {

    private final Map<String, String> requestHeader;
    private final Context context;
    private final int requestId;
    private final int requestMethod;
    private String URL;
    private final Map<String, String> param;
    private VolleyResultCallback volleyResultCallback;

    public VolleyHttpCommunicator(Context context,
                                  int requestId,
                                  int method,
                                  String url,
                                  Map<String, String> params,
                                  Map<String, String> header,
                                  VolleyResultCallback volleyResultCallback) {

        this.context = context;
        this.requestId = requestId;
        this.URL = url;
        this.param = params;
        this.requestMethod = method;
        this.volleyResultCallback = volleyResultCallback;
        this.requestHeader = header;
    }

    public void execute() {
        JSONObject jsonObjectRequestParams = null;
        if (requestMethod == Request.Method.GET) {
            this.URL = getGETParamURL(URL, param);
        } else {
            jsonObjectRequestParams = getJsonRequestObject(param);
        }
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        requestQueue.add(new Communicator(requestId, URL, jsonObjectRequestParams, requestHeader,
                new ResponseListener(requestId, volleyResultCallback),
                new ErrorListener(requestId, volleyResultCallback),
                volleyResultCallback));
    }

    private String getGETParamURL(String url, Map<String, String> bodyParams) {
        if (bodyParams != null) {
            StringBuilder stringBuilder = new StringBuilder(url);
            Iterator<Map.Entry<String, String>> iterator = bodyParams.entrySet().iterator();
            int i = 1;
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (i == 1) {
                    stringBuilder.append("?" + entry.getKey() + "=" + entry.getValue());
                } else {
                    stringBuilder.append("&" + entry.getKey() + "=" + entry.getValue());
                }
                iterator.remove(); // avoids a ConcurrentModificationException
                i++;
            }
            return stringBuilder.toString();
        }
        return url;
    }

    private String getEncodedParam(String param) {
        try {
            return URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject getJsonRequestObject(Map<String, String> requestParams) {
        JSONObject jsonObject = new JSONObject();
        Iterator<Map.Entry<String, String>> iterator = requestParams.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            try {
                jsonObject.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
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

    static class Communicator extends JsonObjectRequest {

        private final int requestId;
        private final Map<String, String> header;
        private final VolleyResultCallback vollyResultCallback;

        /**
         * Constructor which defaults to <code>GET</code> if <code>requestParams</code> is
         * <code>null</code>, <code>POST</code> otherwise.
         *
         * @param url
         * @param requestParams
         * @param listener
         * @param errorListener
         */
        public Communicator(int requestId,
                            String url,
                            JSONObject requestParams,
                            Map<String, String> header,
                            Response.Listener<JSONObject> listener,
                            Response.ErrorListener errorListener,
                            VolleyResultCallback volleyResultCallback) {
            super(url, requestParams, listener, errorListener);
            this.requestId = requestId;
            this.header = header;
            this.vollyResultCallback = volleyResultCallback;
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
            vollyResultCallback.onResponse(requestId, response);
        }

        @Override
        public String getBodyContentType() {
            return "application/json";
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> newHeaders = new HashMap<>();
            newHeaders.put("Content-Type", "application/json");

            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    newHeaders.put(entry.getKey(), entry.getValue());
                }
            }
            return newHeaders;
        }
    }
}
