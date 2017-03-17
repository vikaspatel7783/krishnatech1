package com.krishnatech.mobile.ui;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.krishnatech.mobile.R;
import com.krishnatech.mobile.ServiceContext;
import com.krishnatech.mobile.http.URLBasedRestCommunicator;
import com.krishnatech.mobile.http.VollyHttpCommunicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class LoginActivity extends Activity implements View.OnClickListener, VollyHttpCommunicator.VollyResultCallback {

    String url = UiUtil.BASE_URL + "/login";

    private EditText editTextUsername;
    private EditText editTextPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.edttxtUsername);
        editTextPassword = (EditText) findViewById(R.id.edtTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.pgBarLogin);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        showProgressBar(false);
    }

    @Override
    public void onClick(View v) {

        //communicate(url);

        JSONObject loginJsonParams = getLoginJsonObject();
        if (loginJsonParams == null) {
            UiUtil.getAlertDailog(this, "Login", "Username or Password can not be blank").show();
            return;
        }

        VollyHttpCommunicator vollyHttpCommunicator = new VollyHttpCommunicator(this, Request.Method.POST, url, loginJsonParams, null, this);
        vollyHttpCommunicator.execute();

        /*HashMap<String, String> headerParam = new HashMap<>();
        headerParam.put("Authorization", "a2V0YW46a2V0YW4xMjM=");
        VollyHttpCommunicator vollyHttpCommunicator = new VollyHttpCommunicator(
                this,
                Request.Method.GET,
                UiUtil.BASE_URL + "/device/list",
                null,
                headerParam,
                this);
        vollyHttpCommunicator.execute();*/


        showProgressBar(true);

        //new AsyncCommunicator(url).execute(loginJsonParams.toString());
    }

    private JSONObject getLoginJsonObject() {

        String userName = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            return null;
        }

        JSONObject loginJsonParams = new JSONObject();
        try {
            loginJsonParams.put("username", userName);
            loginJsonParams.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return loginJsonParams;
    }

    private void showProgressBar(boolean show) {
        progressBar.setVisibility(show? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResponse(JSONObject response) {
        showProgressBar(false);
        clearLoginUIFields();
        try {
            if (response.has("status") && response.get("status").equals("success")) {
                String token = (String) response.getJSONObject("data").get("token");
                ServiceContext.getInstance().setToken(token);
                UiUtil.getAlertDailog(this, "Login", "Token received: "+token).show();
            } else {
                UiUtil.getAlertDailog(this, "Login", "Response may not contain status value or not success").show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            UiUtil.getAlertDailog(this, "Login", "JsonParsing exception in response").show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        clearLoginUIFields();
        showProgressBar(false);
        UiUtil.getAlertDailog(this, "Login", "Wrong username or password entered").show();
    }

    private void clearLoginUIFields() {
        editTextPassword.setText("");
        editTextUsername.setText("");
        editTextUsername.requestFocus();
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
                    Toast.makeText(LoginActivity.this, "Status: "+jsonObject.get("status"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
