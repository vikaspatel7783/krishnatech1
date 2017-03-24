package com.krishnatech.mobile.ui;

import android.content.Context;

import com.android.volley.VolleyError;
import com.krishnatech.mobile.ServiceContext;
import com.krishnatech.mobile.http.VolleyHttpCommunicator;

import org.json.JSONObject;

import java.util.Map;

public class LogoutService extends VolleyHttpCommunicator {

    public static final int requestId = 1;

    public LogoutService(Context context, int requestId, int method, String url, Map<String, String> bodyParams, Map<String, String> requestHeader, VolleyResultCallback volleyResultCallback, final LogoutCallback logoutCallback) {
        super(context, requestId, method, url, bodyParams, requestHeader, new VolleyResultCallback() {
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
        });
    }

    public void logout() {
        execute();
    }

    public interface LogoutCallback {
        void onLoggedOut();
    }
 }
