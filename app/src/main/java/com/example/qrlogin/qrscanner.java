package com.example.qrlogin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.Map;

public class qrscanner extends AppCompatActivity implements View.OnClickListener {

    //tag for error message
    private static final String TAG = "MOHIT";
    //View Objects
    private Button buttonScan;
    String username;

    //for status
    String status;
//for sharedpreference values get
    String epmloyee_id;

    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        setTitle("Scann Qr Code");

        SharedPreferences userDetails = getApplication().getSharedPreferences("test", getApplication().MODE_PRIVATE);
        String test1 = userDetails.getString("test1", "");
        String test2 = userDetails.getString("test2", "");

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        epmloyee_id = String.valueOf(sharedPref.getBoolean("id", false));
        //View objects
        buttonScan = (Button) findViewById(R.id.buttonScan);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);

    }


    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it

            //call l() method for user entry
            try {
                l();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }

       //QR code comapre with "ally" same qr so add entry in db
            if (!(result.getContents() == "ally")) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    JSONObject obj = new JSONObject(result.getContents());

                } catch (JSONException e) {
                    e.printStackTrace();

                    //   Toast.makeText(this, "QR is:" +, Toast.LENGTH_LONG).show();
                    //alert for user welcome or thank you message display

        //for status get
                    if (status.equals(1)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                qrscanner.this);
                        builder.setTitle("Welcome " + username);
                        builder.setMessage(result.getContents());
                        //set button for user conformation
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
                                });
                        builder.show();
                    }
                    if(status.equals(2))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                qrscanner.this);
                        builder.setTitle("Thank you " + username);
                        builder.setMessage(result.getContents());
                        //set button for user conformation
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        builder.show();

                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

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
                Intent i = new Intent(qrscanner.this,MainActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }





    public void l() throws JSONException {


        //get current date for
        Date d = new Date();
        CharSequence s  = DateFormat.format("yyyy,mm,dd", d.getTime());

        RequestQueue mRequestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        JSONObject postparams = new JSONObject();

        postparams.put("e_id", epmloyee_id);
     //   postparams.put("id", "1");
        postparams.put("date", s.toString());

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,Constant.User, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(qrscanner.this, "" + response, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onResponse: "+response );
                try {
                     status=response.getString("status");
                    String message=response.getString("message");

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(qrscanner.this, "Can't Connect to server", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onErrorResponse: "+error );
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





