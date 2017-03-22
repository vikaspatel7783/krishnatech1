package com.krishnatech.mobile.ui;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.krishnatech.mobile.ServiceContext;
import com.krishnatech.mobile.http.VolleyHttpCommunicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class LogoutService extends VolleyHttpCommunicator {

    public static final int requestId = 1;

    public LogoutService(Context context, int requestId, int method, String url, JSONObject bodyParams, Map<String, String> requestHeader, VolleyResultCallback volleyResultCallback, LogoutCallback logoutCallback) {
        super(context, requestId, method, url, bodyParams, requestHeader, new CustomVollyCallback(logoutCallback));
    }

    public void logout() {
        execute();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            // TODO: Unfortunately, response received in plain text only as it is expected in JSON format.
            String plainString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));

            // Manually wrapping to json string
            try {
                String jsonString = "{ \"status\": \""+plainString+"\" }";
                return Response.success(new JSONObject(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class CustomVollyCallback implements VolleyResultCallback {

        private final LogoutCallback logoutCallback;

        public CustomVollyCallback(LogoutCallback logoutCallback) {
            this.logoutCallback = logoutCallback;
        }

        @Override
        public void onResponse(int requestId, JSONObject response) {
            ServiceContext.getInstance().clear();
            logoutCallback.onLoggedOut();
        }

        @Override
        public void onErrorResponse(int requestId, VolleyError error) {
            ServiceContext.getInstance().clear();
            logoutCallback.onLoggedOut();
        }
    }

    public interface LogoutCallback {
        void onLoggedOut();
    }
 }
