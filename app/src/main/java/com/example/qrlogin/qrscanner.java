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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class qrscanner extends AppCompatActivity implements View.OnClickListener {


    //View Objects
    private Button buttonScan;
    String username;
    boolean status;


    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        setTitle("SCANN QR CODE");

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
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    JSONObject obj = new JSONObject(result.getContents());

                } catch (JSONException e) {
                    e.printStackTrace();


                    //   Toast.makeText(this, "QR is:" +, Toast.LENGTH_LONG).show();


                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            qrscanner.this);
                    builder.setTitle("WELCOME "+ username);
                    builder.setMessage(result.getContents());

                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                }
                            });
                    builder.show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public void onClick(View v) {
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
}


