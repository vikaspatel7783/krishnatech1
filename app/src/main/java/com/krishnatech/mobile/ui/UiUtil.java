package com.krishnatech.mobile.ui;


import android.app.AlertDialog;
import android.content.Context;

public class UiUtil {

    public static final String BASE_URL = "http://182.237.12.85:8080/krishnarest/rest";

    public static AlertDialog getAlertDailog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", null);
        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }
}
