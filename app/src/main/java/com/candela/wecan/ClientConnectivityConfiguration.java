package com.candela.wecan;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.candela.wecan.tests.BasicClientConnectivity;
import com.candela.wecan.tests.base_tools.GetPhoneWifiInfo;
import com.candela.wecan.tests.base_tools.HTTPHandler;
import com.candela.wecan.tests.base_tools.WifiReceiver;

import java.util.ArrayList;
import java.util.List;

public class ClientConnectivityConfiguration extends AppCompatActivity {

    public Spinner spinner;
    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private ListView wifiList;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    WifiReceiver receiverWifi;
    Button start_btn;

//    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_connectivity_configuration);
        getSupportActionBar().hide();

        spinner = findViewById(R.id.spinner);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        EditText editText = findViewById(R.id.password_cc);
        ProgressBar progressBar = findViewById(R.id.progress_cc);
        progressBar.setVisibility(View.INVISIBLE);
        Button scan_btn = findViewById(R.id.scan_btn_cc);
        GetPhoneWifiInfo getPhoneWifiInfo = new GetPhoneWifiInfo();
        getPhoneWifiInfo.GetWifiData(wifiManager);
        HTTPHandler httpHandler = getIntent().getParcelableExtra("server_handler");
//
        scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiverWifi = new WifiReceiver(wifiManager, wifiList, spinner);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(receiverWifi, intentFilter);
                getWifi();
            }
        });

        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
//
//        // Start scan if permission is enabled
        if (ActivityCompat.checkSelfPermission(ClientConnectivityConfiguration.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ClientConnectivityConfiguration.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        }

        // Start Test Button
        start_btn = findViewById(R.id.start_test_cc_btn);
        start_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                wifiManager.startScan();



                String wifi_name = spinner.getSelectedItem().toString();
                String pass = editText.getText().toString();

                BasicClientConnectivity cc_obj = new BasicClientConnectivity(getApplicationContext(), wifiManager);
                cc_obj.run_test(getApplicationContext(), wifiManager, wifi_name, pass, httpHandler);
                progressBar.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        if (wifiInfo.getSupplicantState().toString().equals("COMPLETED") && (wifiInfo.getSSID().equals(String.format("\"%s\"", wifi_name)))){
                            progressBar.setVisibility(View.INVISIBLE);
                            System.out.println(cc_obj.cc_data);
                            handler.removeCallbacks(this);
                        }
                        else{

                        }
                    }
                }, 10000);
            }
        });


    }



    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(ClientConnectivityConfiguration.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ClientConnectivityConfiguration.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                Toast.makeText(ClientConnectivityConfiguration.this, "location turned on", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            }
        } else {
            Toast.makeText(ClientConnectivityConfiguration.this, "scanning", Toast.LENGTH_SHORT).show();
            wifiManager.startScan();
            //Log.e("log", "CCC getWifi B: startScan");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   wifiManager.startScan();
                   //Log.e("log", "CCC onReqPermissionResult: startScan");
                } else {
                   return;
                }
                break;
        }//switch
    }


}

