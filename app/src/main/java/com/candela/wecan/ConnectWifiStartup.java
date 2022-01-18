package com.candela.wecan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ConnectWifiStartup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi_startup);
        getSupportActionBar().hide();
        EditText test_nw_ssid = findViewById(R.id.test_network_ssid);
        EditText test_nw_passkey = findViewById(R.id.test_network_ssid);
        Spinner spinner = findViewById(R.id.test_network_encryption);
    }
}