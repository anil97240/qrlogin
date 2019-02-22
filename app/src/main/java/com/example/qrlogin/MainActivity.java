package com.example.qrlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private static final int REQUEST_SIGNUP = 0;

    ProgressDialog progressDialog;
    //for edit text Employee id
    EditText _emailText, _passwordText;
    //for login button
    Button _loginButton;
    //for user alread login or not check condition
    boolean Registered;
    ScrollView s1;

    private CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");
        SharedPreferences shared = getSharedPreferences("User", MODE_PRIVATE);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);
        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        s1 = findViewById(R.id.s1);


        Registered = shared.getBoolean("Registered", false);
        if (!Registered) {

        } else {
            Intent i = new Intent(getApplicationContext(), qrscanner.class);
            startActivity(i);
            finish();
        }

        SharedPreferences userdetail = getSharedPreferences("userdetail", MODE_PRIVATE);
        String id = userdetail.getString("emp_id", "");
        String pas = userdetail.getString("password", "");
        checkBoxRememberMe.setChecked(true);
        if (userdetail.contains("emp_id")) {
            if (checkBoxRememberMe.isChecked()) {
                _emailText.setText(id);
                _passwordText.setText(pas);
            }

        }


        _loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(false);

                progressDialog.setMessage("Connecting To Server");
                progressDialog.show();
                if (checkBoxRememberMe.isChecked()) {
                    SharedPreferences userdetail = getSharedPreferences("userdetail", MODE_PRIVATE);
                    SharedPreferences.Editor edtr = userdetail.edit();
                    edtr.putString("emp_id", _emailText.getText().toString());
                    edtr.putString("password", _passwordText.getText().toString());
                    edtr.apply();
                    checkBoxRememberMe.setChecked(true);
                }
                else {

                    SharedPreferences sharedPreferences = getSharedPreferences("userdetail", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                }

                try {
                    l();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });


    }


    public void l() throws JSONException {

        //create RequestQueue object
        RequestQueue mRequestQueue;
        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);
        // Start the queue
        mRequestQueue.start();
        JSONObject postparams = new JSONObject();
        postparams.put("empid", _emailText.getText().toString());
        postparams.put("password", _passwordText.getText().toString());
        // Formulate the request and handle the response.
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.Login, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "onResponse: " + response);
                try {
                    progressDialog.dismiss();
                    JSONObject json = new JSONObject(String.valueOf(response));
                    String status = json.optString("status");
                    String message = response.getString("message");
                    String data = response.getString("data");
                    //get an nested data of json
                    String username = String.valueOf(json.getJSONObject("data").getString("username"));

                    //sharedpreference pass data in qrscanner activity
                    SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("Registered", true);
                    editor.putString("status", status);
                    editor.putString("message", message);
                    editor.putString("data", data);
                    editor.putString("username", username);
                    editor.putString("id", _emailText.getText().toString());

                    /**
                     * redirect page login page to qr scanner page
                     * with pass data in sharedpreferences
                     *
                     * */
                    if (editor.commit()) {
                        Intent i = new Intent(getApplicationContext(), qrscanner.class);
                        startActivity(i);
                        finish();

                    }


                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Log.e(TAG, "onResponse: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();

                //snackbar for error message
                Snackbar snackbar = Snackbar.make(s1, "Can't Find Account", Snackbar.LENGTH_LONG);
                snackbar.show();

            }
        });
        mRequestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}





