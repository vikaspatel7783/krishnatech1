package com.krishnatech.mobile.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.krishnatech.mobile.R;
import com.krishnatech.mobile.ServiceContext;
import com.krishnatech.mobile.http.URLBasedRestCommunicator;
import com.krishnatech.mobile.http.VolleyHttpCommunicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends ParentActivity implements View.OnClickListener, VolleyHttpCommunicator.VolleyResultCallback {

    String url = UiUtil.BASE_URL + "/login";

    private static final int loginRequestId = 1;

    private EditText editTextUsername;
    private EditText editTextPassword;
    private ProgressBar progressBar;
    private TextView txtViewPgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupActionbar();
        setupUI();
    }

    private void setupUI() {
        editTextUsername = (EditText) findViewById(R.id.edttxtUsername);
        editTextPassword = (EditText) findViewById(R.id.edtTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.pgBarLogin);
        txtViewPgBar = (TextView) findViewById(R.id.txtviewProgressbar);

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        showProgressBar(false, null);
    }

    private void setupActionbar() {
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setIcon(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        String userName = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            UiUtil.getAlertDailog(this, "Login", "Username or Password can not be blank").show();
            return;
        }

        Map<String, String> loginParams = new HashMap<>();
        loginParams.put("username", userName);
        loginParams.put("password", password);

        VolleyHttpCommunicator volleyHttpCommunicator = new VolleyHttpCommunicator(this,
                loginRequestId, Request.Method.POST, url, loginParams, null, this);
        volleyHttpCommunicator.execute();

        showProgressBar(true, "Logging in progress...");

        //new AsyncCommunicator(url).execute(loginJsonParams.toString());
    }

    private void showProgressBar(boolean show, String progressbarText) {
        if (show) {
            txtViewPgBar.setVisibility(View.VISIBLE);
            txtViewPgBar.setText(progressbarText);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            txtViewPgBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResponse(int requestId, JSONObject response) {
        showProgressBar(false, null);
        clearLoginUIFields();

        switch (requestId) {
            case loginRequestId:
                try {
                    if (response.has(ServiceContext.KEY_STATUS) && response.get(ServiceContext.KEY_STATUS).equals(ServiceContext.KEY_SUCCESS)) {
                        String token = (String) response.getJSONObject(ServiceContext.KEY_DATA).get("token");
                        ServiceContext.getInstance().setToken(token);

                        showRegisteredDevicesScreen();
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

    @Override
    public void onErrorResponse(int requestId, VolleyError error) {
        clearLoginUIFields();
        showProgressBar(false, null);

        if (error.getCause() instanceof ConnectException) {
            UiUtil.getAlertDailog(this, "Login", "Unable to reach to server. Please try again or check Internet connectivity").show();
            return;
        }
        switch (requestId) {
            case loginRequestId:
                UiUtil.getAlertDailog(this, "Login", "Wrong username or password entered").show();
                break;
        }
    }

    private void clearLoginUIFields() {
        editTextPassword.setText("");
        editTextUsername.setText("");
        editTextUsername.requestFocus();
    }

    private void showRegisteredDevicesScreen() {
        startActivity(new Intent(this, DeviceListActivity.class));
        finish();
    }

    class AsyncCommunicator extends AsyncTask<String, Void, URLBasedRestCommunicator.Response> {

        private final String url;

        public AsyncCommunicator(String url) {
            this.url = url;
        }

        @Override
        protected URLBasedRestCommunicator.Response doInBackground(String... params) {

            URLBasedRestCommunicator.Response response = URLBasedRestCommunicator.communicate(URLBasedRestCommunicator.RequestType.POST, url, params[0], null);
            return response;
        }

        @Override
        protected void onPostExecute(URLBasedRestCommunicator.Response response) {
            if (response.getHttpException() != null) {
                Toast.makeText(LoginActivity.this, "Exception occured", Toast.LENGTH_SHORT).show();
            } else {
                JSONObject jsonObject = response.getJsonObject();
                Map<String, String> header = response.getHeader();

                try {
                    Toast.makeText(LoginActivity.this, "Status: "+jsonObject.get(ServiceContext.KEY_STATUS), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
