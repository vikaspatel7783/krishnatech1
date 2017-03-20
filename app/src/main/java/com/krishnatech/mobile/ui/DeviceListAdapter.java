package com.krishnatech.mobile.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.krishnatech.mobile.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeviceListAdapter extends BaseAdapter {

    private Context context;
    LayoutInflater inflater;
    private JSONArray items;

    public DeviceListAdapter(Context context, JSONArray items) {
        this.items = items;
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.template_device, null);
        }

        JSONObject deviceJsonObject = (JSONObject) getItem(position);

        TextView txtViewDeviceId = (TextView) convertView.findViewById(R.id.txtViewValueDeviceId);
        TextView txtViewDeviceName = (TextView) convertView.findViewById(R.id.txtViewValueDeviceName);
        TextView txtViewDeviceType = (TextView) convertView.findViewById(R.id.txtViewValueDeviceType);

        try {
            txtViewDeviceId.setText(deviceJsonObject.getString("deviceid"));
            txtViewDeviceType.setText(deviceJsonObject.getString("devicetype"));
            txtViewDeviceName.setText(deviceJsonObject.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return items.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return items.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Json parsing exception in device list adapter");
        }
    }

    @Override
    public long getItemId(int position) {
        try {
            return (int)((JSONObject)items.get(position)).get("id");
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("ID not found in device list object");
        }
    }
}