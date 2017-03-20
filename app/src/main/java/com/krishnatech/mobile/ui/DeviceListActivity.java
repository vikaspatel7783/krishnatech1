package com.krishnatech.mobile.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.krishnatech.mobile.R;
import com.krishnatech.mobile.ServiceContext;
import com.krishnatech.mobile.http.VollyHttpCommunicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DeviceListActivity extends Activity implements VollyHttpCommunicator.VollyResultCallback {

    private final int listDeviceServiceId = 1;
    private ListView deviceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devicelist);

        deviceListView = (ListView) findViewById(R.id.lstViewDeviceList);
        fetchRegisteredDevices();
    }

    private void fetchRegisteredDevices() {
        showProgressBar(true, "Getting device list...");

        HashMap<String, String> header = new HashMap<>();
        header.put("Authorization", ServiceContext.getInstance().getToken());

        VollyHttpCommunicator vollyHttpCommunicator = new VollyHttpCommunicator(this,
                listDeviceServiceId,
                Request.Method.GET,
                UiUtil.BASE_URL +"/device/list",
                null,
                header,
                this);
        vollyHttpCommunicator.execute();
    }

    private void showProgressBar(boolean show, String label) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pgBarLogin);
        TextView txtViewPgBar = (TextView) findViewById(R.id.txtviewProgressbar);
        if (show) {
            txtViewPgBar.setVisibility(View.VISIBLE);
            txtViewPgBar.setText(label);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            txtViewPgBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponse(int requestId, JSONObject response) {
        showProgressBar(false, null);

        switch (requestId) {
            case listDeviceServiceId:
                try {
                    if (response.has("status") && response.get("status").equals("success")) {
                        showRegisteredDevicesScreen(response.getJSONArray("data"));
                    } else {
                        UiUtil.getAlertDailog(this, "Login", "Response may not contain status field or not success").show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    UiUtil.getAlertDailog(this, "Login", "JsonParsing exception in response").show();
                }
                break;
        }

    }

    private void showRegisteredDevicesScreen(JSONArray jsonArray) {
        final DeviceListAdapter deviceListAdapter = new DeviceListAdapter(this, jsonArray);
        deviceListView.setAdapter(deviceListAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject deviceJson = (JSONObject) deviceListAdapter.getItem(position);
                try {
                    Toast.makeText(DeviceListActivity.this, "selected device: "+deviceJson.get("deviceid"), Toast.LENGTH_SHORT).show();deviceJson.get("deviceid");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onErrorResponse(int requestId, VolleyError error) {
        showProgressBar(false, null);
        UiUtil.getAlertDailog(this, "Device List", "Exception in fetching registered devices").show();
    }
}
