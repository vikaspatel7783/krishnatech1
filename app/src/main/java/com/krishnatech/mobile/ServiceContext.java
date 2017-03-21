package com.krishnatech.mobile;

public class ServiceContext {

    public static final String KEY_AUTHORIZATION = "Authorization";
    public static final String KEY_STATUS = "status";
    public static final String KEY_SUCCESS = "success";
    public static final String KEY_DATA = "data";

    private static ServiceContext serviceContext;
    private String mToken;
    private String mDeviceId;

    private ServiceContext() {}

    public static ServiceContext getInstance() {
        if (serviceContext == null) {
            serviceContext = new ServiceContext();
        }
        return serviceContext;
    }

    public void setToken(String token) {
        mToken = token;
    }

    public String getToken() {
        return mToken;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public void setDeviceId(String mDeviceId) {
        this.mDeviceId = mDeviceId;
    }

    public void clear() {
        mToken = null;
        mDeviceId = null;
        serviceContext = null;
    }
}
