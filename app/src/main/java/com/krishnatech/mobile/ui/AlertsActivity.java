package com.krishnatech.mobile.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.krishnatech.mobile.R;
import com.krishnatech.mobile.ServiceContext;
import com.krishnatech.mobile.http.VolleyHttpCommunicator;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class AlertsActivity extends ParentActivity implements VolleyHttpCommunicator.VolleyResultCallback, View.OnClickListener {

    private static final String PARAM_DEVICE_ID = "device_id";
    private static final String PARAM_DATE = "date";
    private int mYear;
    private int mMonth;
    private int mDay;
    private TextView txtviewDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts);

        TextView txtviewDeviceId = (TextView) findViewById(R.id.textView3);
        txtviewDeviceId.setText(ServiceContext.getInstance().getDeviceId());

        txtviewDate = (TextView) findViewById(R.id.textView5);
        TextView txtViewSelectDate = (TextView) findViewById(R.id.txtViewSelectDate);
        txtViewSelectDate.setOnClickListener(this);

        findViewById(R.id.btnFetchAlerts).setOnClickListener(this);
    }

    private void fetchAlerts() {
        HashMap<String, String> header = new HashMap<>();
        header.put(ServiceContext.KEY_AUTHORIZATION, ServiceContext.getInstance().getToken());

        HashMap<String, String> bodyParams = new HashMap<>();
        bodyParams.put(PARAM_DEVICE_ID, ServiceContext.getInstance().getDeviceId());
        bodyParams.put(PARAM_DATE, txtviewDate.getText()+"-"+txtviewDate.getText());

        new VolleyHttpCommunicator(
                this,
                1,
                Request.Method.GET,
                UiUtil.BASE_URL + "/device/alerts",
                bodyParams,
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
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtViewSelectDate:
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtviewDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;

            case R.id.btnFetchAlerts:
                showProgressbar("Fetching alerts for "+ServiceContext.getInstance().getDeviceId());
                fetchAlerts();
                break;
        }

    }
}
