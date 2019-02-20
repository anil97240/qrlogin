package com.example.qrlogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class qrscanner extends AppCompatActivity implements View.OnClickListener {

    //tag for error message
    private static final String TAG = "MOHIT";
    String username;
    //for status
    String status,message;

    //for sharedpreference values get
    String epmloyee_id, name;
    //View Objects
    private Button buttonScan;
    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        setTitle("Scann Qr Code");

        SharedPreferences shared = getSharedPreferences("User", MODE_PRIVATE);
        epmloyee_id = (shared.getString("id", ""));
        name = (shared.getString("username", ""));


        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);

    }


    //Getting the scan results
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            String result1 = result.getContents();


            if (result1.equals("1")) {

                if (status.equals("1")) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(qrscanner.this);
                    builder1.setMessage("AAvo AAVO" + name);
                    builder1.setCancelable(true);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(qrscanner.this);
                    builder1.setMessage("TATA" + name);
                    builder1.setCancelable(true);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        //for open qr scanner
        qrScan.initiateScan();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item1:

                //logout for user all shareprefrece clear and redirect to login page

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(qrscanner.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(qrscanner.this, MainActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void l() throws JSONException {
        //get current date for
        Date d = new Date();
        final CharSequence s = DateFormat.format("yyyy-MM-dd", d.getTime());
        Toast.makeText(qrscanner.this, s.toString(), Toast.LENGTH_SHORT).show();
        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        JSONObject postparams = new JSONObject();
        postparams.put("e_id", epmloyee_id);
        postparams.put("date", s.toString());

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constant.User, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e(TAG, "onResponse: " + response);
                try {
                    status = response.getString("status");
                    message = response.getString("message");

                    Toast.makeText(qrscanner.this, "" + status + "" + message, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error);

            }
        });

        // Add the request to the RequestQueue.
        mRequestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        //for clsoe app
        finish();
        super.onBackPressed();
    }
}





