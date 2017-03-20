package com.krishnatech.mobile.ui;

import android.content.Context;

import com.android.volley.Request;
import com.krishnatech.mobile.http.VollyHttpCommunicator;

import java.util.HashMap;
import java.util.Map;

public class LogoutService {

    public void logout(Context context, VollyHttpCommunicator.VollyResultCallback vollyResultCallback) {
        Map<String, String> header = new HashMap<>();
        header.put("Authorization", "35535335353");
        VollyHttpCommunicator vollyHttpCommunicator = new VollyHttpCommunicator(context, 2, Request.Method.GET, "http://182.237.12.85:8080/krishnarest/rest/logout", null, header, vollyResultCallback);
        vollyHttpCommunicator.execute();
    }

}
