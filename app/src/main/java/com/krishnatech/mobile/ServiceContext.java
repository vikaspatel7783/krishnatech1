package com.krishnatech.mobile;

public class ServiceContext {

    private static ServiceContext serviceContext;
    private String mToken;

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
}
