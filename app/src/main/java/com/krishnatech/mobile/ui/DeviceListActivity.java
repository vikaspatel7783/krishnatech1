package com.krishnatech.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.krishnatech.mobile.R;
import com.krishnatech.mobile.ServiceContext;
import com.krishnatech.mobile.http.VolleyHttpCommunicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.krishnatech.mobile.ui.DeviceListAdapter.KEY_DEVICE_ID;

public class DeviceListActivity extends ParentActivity implements VolleyHttpCommunicator.VolleyResultCallback {

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
        showProgressbar("Getting device list...");

        HashMap<String, String> header = new HashMap<>();
        header.put(ServiceContext.KEY_AUTHORIZATION, ServiceContext.getInstance().getToken());

        VolleyHttpCommunicator volleyHttpCommunicator = new VolleyHttpCommunicator(this,
                listDeviceServiceId,
                Request.Method.GET,
                UiUtil.BASE_URL +"/device/list",
                null,
                header,
                this);
        volleyHttpCommunicator.execute();
    }

    @Override
    public void onResponse(int requestId, JSONObject response) {
        dismissProgressbar();

        try {
            if (response.has(ServiceContext.KEY_STATUS) && response.get(ServiceContext.KEY_STATUS).equals(ServiceContext.KEY_SUCCESS)) {
                showRegisteredDevicesScreen(response.getJSONArray(ServiceContext.KEY_DATA));
            } else {
                UiUtil.getAlertDailog(this, "Login", "Response may not contain status field or not success").show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            UiUtil.getAlertDailog(this, "Login", "JsonParsing exception in response").show();
        }
    }

    private void showRegisteredDevicesScreen(JSONArray jsonArray) {
        final DeviceListAdapter deviceListAdapter = new DeviceListAdapter(this, jsonArray);
        deviceListView.setAdapter(deviceListAdapter);

        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject deviceJsonObject = (JSONObject) deviceListAdapter.getItem(position);
                try {
                    String deviceId = deviceJsonObject.getString(KEY_DEVICE_ID);
                    ServiceContext.getInstance().setDeviceId(deviceId);
                    startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    UiUtil.getAlertDailog(getApplicationContext(), "DeviceId", "Problem while extracting deviceId from json");
                }
            }
        });
    }

    @Override
    public void onErrorResponse(int requestId, VolleyError error) {
        dismissProgressbar();
        UiUtil.getAlertDailog(this, "Device List", "Exception in fetching registered devices").show();
    }
}
