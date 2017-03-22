package com.krishnatech.mobile.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.krishnatech.mobile.R;
import com.krishnatech.mobile.ServiceContext;
import com.krishnatech.mobile.http.VolleyHttpCommunicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AlertsActivity extends ParentActivity implements VolleyHttpCommunicator.VolleyResultCallback {

    private static final String PARAM_DEVICE_ID = "device_id";
    private static final String PARAM_DATE = "date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        showProgressbar("Fetching alerts for "+ServiceContext.getInstance().getDeviceId());
        fetchAlerts();
    }

    private void fetchAlerts() {
        HashMap<String, String> header = new HashMap<>();
        header.put(ServiceContext.KEY_AUTHORIZATION, ServiceContext.getInstance().getToken());

        JSONObject alertJsonBodyParams = new JSONObject();
        try {
            alertJsonBodyParams.put(PARAM_DEVICE_ID, ServiceContext.getInstance().getDeviceId());
            alertJsonBodyParams.put(PARAM_DATE, "20/03/2017"); //FIXME: what format of date?
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new VolleyHttpCommunicator(this,
                1,
                Request.Method.GET,
                UiUtil.BASE_URL + "/device/alerts",
                alertJsonBodyParams,
                header,
                this).execute();
    }

    @Override
    public void onResponse(int requestId, JSONObject response) {
        dismissProgressbar();
        UiUtil.getAlertDailog(this, "Alerts", "Success in fetching alerts").show();
    }

    @Override
    public void onErrorResponse(int requestId, VolleyError error) {
        dismissProgressbar();
        AlertDialog alertDialog = UiUtil.getAlertDailog(this, "Alerts", "Error in fetching alerts:\nStatus code: " + error.networkResponse.statusCode);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        alertDialog.show();
    }
}
