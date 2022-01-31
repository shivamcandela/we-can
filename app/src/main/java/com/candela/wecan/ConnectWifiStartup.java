package com.candela.wecan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.candela.wecan.tests.base_tools.ConfigureWifi;
import com.candela.wecan.tests.base_tools.GetPhoneWifiInfo;
import com.candela.wecan.tests.base_tools.WECANManager;
import com.candela.wecan.tests.base_tools.WifiReceiver;

import java.util.List;

public class ConnectWifiStartup extends AppCompatActivity {

    SharedPreferences sharedpreferences = null;
    private ListView wifiList;
    WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi_startup);
        getSupportActionBar().hide();

        sharedpreferences = getBaseContext().getSharedPreferences("test_network_main", Context.MODE_PRIVATE);
        TextView network_description = findViewById(R.id.network_description);
        Spinner spinner = findViewById(R.id.test_network_ssid);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        Button connect_nw_btn = findViewById(R.id.connect_nw_btn);
        EditText passkey = findViewById(R.id.test_name);
        ImageButton refresh_scan = findViewById(R.id.refresh_scan);
        Button skip_conn_btn = findViewById(R.id.skip_conn_btn);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {

            System.out.println("iron");
            System.out.println(i.priority);
        }
//        GetPhoneWifiInfo getPhoneWifiInfo = new GetPhoneWifiInfo();
//        getPhoneWifiInfo.GetWifiData(wifiManager);
////
//        WECANManager.getTestNetwork(sharedpreferences);
//        System.out.println("iron spider: "+ wifiManager.getConnectionInfo().getSSID());
//        System.out.println("iron spider: "+ WECANManager.TEST_NETWORK_SSID);
//        if (WECANManager.TEST_NETWORK_SSID != null && (wifiManager.getConnectionInfo().getSSID()).equals("\"" + WECANManager.TEST_NETWORK_SSID + "\"")){
//            skip_conn_btn.setEnabled(true);
//        }
//
//        passkey.setText(WECANManager.TEST_NETWORK_PASSKEY);
//        skip_conn_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                openStartupActivity();
//            }
//        });
        refresh_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Scanning...", Toast.LENGTH_SHORT).show();
                WifiReceiver receiverWifi = new WifiReceiver(wifiManager, wifiList, spinner);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                registerReceiver(receiverWifi, intentFilter);
                getWifi();
            }
        });
        refresh_scan.callOnClick();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<ScanResult> results = wifiManager.getScanResults();
//                System.out.println("spider: " + parent.getSelectedItem().toString());
                System.out.println(results);
                for (int i = 0; i < results.size() ; i++){
                    ScanResult scanResult = results.get(i);
                    if (parent.getSelectedItem().toString().equals(scanResult.SSID)){
//                        System.out.println("Frequency : " + scanResult.frequency +
//                                " BSSID: " + scanResult.BSSID +
//                                " Capabilities: " + scanResult.capabilities);
                        network_description.setText("Frequency : " + scanResult.frequency +
                                                    " \nBSSID: " + scanResult.BSSID +
                                                    " \nCapabilities: " + scanResult.capabilities);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        connect_nw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ssid = spinner.getSelectedItem().toString();
                String encryption= "open";
                for (ScanResult network : wifiManager.getScanResults()){
                    if (network.SSID.equals(ssid)){
                        String capabilities = network.capabilities;
                        if (capabilities.contains("WPA2")){
                            encryption = "psk2";
                        }
                        else if (capabilities.contains("WPA")){
                            encryption = "psk";
                        }
                        else if (capabilities.contains("WEP")){
                            encryption = "wep";
                        }
                        else if (capabilities.contains("SAE")){
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                if (wifiManager.isWpa3SaeSupported()){
                                    encryption = "sae";
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "SSID With SAE Encryption not Supported", Toast.LENGTH_LONG).show();
                                    encryption = null;
                                }
                            }
                            else {
                                encryption = "sae";
                            }

                        }

                    }
                }
                String password = passkey.getText().toString();
                if (encryption == null){

                }
                else {
                    ConfigureWifi configureWifi = new ConfigureWifi(getApplicationContext(), wifiManager, ssid, password, encryption);
                    Handler handler = new Handler();
                    String finalEncryption = encryption;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.removeCallbacks(this);
//                            System.out.println("iron_spider: " + ssid);
//                            System.out.println("iron_spider: " + wifiManager.getConnectionInfo().getSSID());
                            if (("\"" + ssid + "\"").equals(wifiManager.getConnectionInfo().getSSID())){
                                Toast.makeText(getApplicationContext(), "Connected to Test Network...", Toast.LENGTH_LONG).show();
                                WECANManager.setTestNetwork(sharedpreferences, ssid, password, finalEncryption);
                                openStartupActivity();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Failed to Connect to Test Network...", Toast.LENGTH_LONG).show();
                            }

                        }
                    },30000);
                }
            }
        });

    }
    public void openStartupActivity () {
        Intent myIntent = new Intent(getApplicationContext(), StartupActivity.class);
        startActivity(myIntent);
    }

    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(ConnectWifiStartup.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ConnectWifiStartup.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                wifiManager.startScan();
                //Log.e("log", "CCC getWifi A: startScan");
            }
        } else {
            Toast.makeText(ConnectWifiStartup.this, "scanning", Toast.LENGTH_SHORT).show();
            wifiManager.startScan();
            //Log.e("log", "CCC getWifi B: startScan");
        }
    }


}