package com.example.qrlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    EditText _emailText;
    //emplopyee password
    EditText _passwordText;
    //for login button
    Button _loginButton;

    //for user alread login or not check condition
    boolean Registered;

    ScrollView s1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Login");

        s1 = findViewById(R.id.s1);

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Registered = sharedPref.getBoolean("Registered", false);

        if (!Registered) {


        } else {
            Intent i=new Intent(getApplicationContext(),qrscanner.class);
            startActivity(i);
            finish();
        }


        _emailText = findViewById(R.id.input_email);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);


        _loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Connecting To Server");
                progressDialog.show();

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
                    String status = response.getString("status");
                    String message = response.getString("message");
                    String data = response.getString("data");
                   //sharedpreference pass data in qrscanner activity
                    final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("Registered", true);
                    editor.putString("status", status);
                    editor.putString("message", message);
                    editor.putString("data", data);
                    editor.putString("id",_emailText.getText().toString());
                    editor.apply();
                    /**
                     * redirect page login page to qr scanner page
                     * with pass data in sharedpreferences
                     *
                     * */
                    Intent i=new Intent(getApplicationContext(),qrscanner.class);
                    startActivity(i);
                    finish();

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

}





