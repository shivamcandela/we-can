package com.candela.wecan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.candela.wecan.tests.base_tools.ConfigureWifi;
import com.candela.wecan.tests.base_tools.LF_Resource;
import com.candela.wecan.tools.LogCatManager;

import java.util.Hashtable;
import java.util.Map;

//import candela.lfresource.lfresource;
//import com.candela.wecan.tests.base_tools.LF_Resource;


/**
 * Startup Activity for Candela WE-CAN
 *
 */
public class StartupActivity extends AppCompatActivity {
    public static final String TAG = "LANforge-Interop";
    private Button button;
    static final int STARTING = 0;
    static final int RUNNING = 1;
    static final int STOPPED = 2;
    private EditText server_ip,u_name;
    static int state;
    private String ssid, passwd;
    public static Context context;
    protected static LF_Resource lf_resource = null;
    public static SharedPreferences sharedpreferences = null;
    public static boolean active=false;
    private LocationManager locationManager;
    private boolean GpsStatus;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        getSupportActionBar().hide();
        button = findViewById(R.id.enter_button);
        server_ip =  (EditText) findViewById(R.id.ip_enter_page);
        u_name = (EditText) findViewById(R.id.user_name);
        context = getBaseContext();
        sharedpreferences = getBaseContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        Map<String,?> keys = sharedpreferences.getAll();
        String last_ip = (String) keys.get("current_ip");
        String user_name = (String) keys.get("current_username");
        LogCatManager logCatManager = new LogCatManager();
        Bundle arg = getIntent().getExtras();
        boolean click = false;

        server_ip.setText(last_ip);
        u_name.setText(user_name);

        // Allow cmd-line to override.
        if (arg != null) {
            Log.d(TAG,"Adding arguments from command line: " + arg);
            boolean good_wifi_cred = true;
            String crypt = "open";
            String passwd = "";

            if (!arg.containsKey("ssid")) {
                good_wifi_cred = false;
                Log.d(TAG, "ssid : " + arg.getString("ssid"));
            }
            if (arg.containsKey("password")) {
                passwd = arg.getString("password");
                Log.d(TAG, "password : " + passwd);
            }
            if (arg.containsKey("encryption")) {
                crypt = arg.getString("encryption");
                Log.d(TAG, "encryption : " + crypt);
            }

            Log.d(TAG, "good_wifi_cred: " + good_wifi_cred);
            if (good_wifi_cred){
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                ConfigureWifi configureWifi = new ConfigureWifi(getApplicationContext(), wifiManager, arg.getString("ssid"),
                                                                passwd, crypt);
            }

            if (arg.containsKey("username")) {
                Log.d(TAG, "username : " + arg.getString("username"));
                u_name.setText(Editable.Factory.getInstance().newEditable(arg.getString("username")), EditText.BufferType.NORMAL);
            }
            if (arg.containsKey("serverip")) {
                Log.d(TAG, "serverip : " + arg.getString("serverip"));
                server_ip.setText(Editable.Factory.getInstance().newEditable(arg.getString("serverip")), EditText.BufferType.NORMAL);
            }
            if (arg.containsKey("auto_start")){
                Log.d(TAG, "click Button : " + arg.getString("auto_start"));
                if (arg.getString("auto_start").equals("1")) {
                    click = true;
                }
            }
        }

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_WIFI_STATE,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.CHANGE_WIFI_STATE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        CheckGpsStatus();
        //        networkSniffTask.execute();
//        Intent myIntent = new Intent(this, ClientConnectivityConfiguration.class);
//        startActivity(myIntent);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);

                checkConnect();
            }
        });

        if (click){
            button.callOnClick();
        }
    }

    public void checkConnect() {
        // Condition for minimum character limits
        if (u_name.getText().toString().replaceAll("\\s", "").length() <= 4) {
            Toast.makeText(getApplicationContext(), "user-name must be at least 5 characters", Toast.LENGTH_SHORT).show();
            button.setEnabled(true);
            return;
        }
        // If everything is good, then do the logic
        else {
            String username = u_name.getText().toString().trim();
            String serverip = server_ip.getText().toString().trim();
            String realm = "-1";
            String resource = "-1";

            // If server is already registered
            Hashtable data = getLFResourceCredentials();
            if (data.containsKey("server_ip-" + serverip)) {
                realm = data.get("realm_id-" + serverip).toString();
                resource = data.get("resource_id-" + serverip).toString();
            }

            connect_server(serverip, resource, realm, username);
        }
    }

    public void openServerConnection () {
        if (navigation.active){
            return;
        }
        Intent myIntent = new Intent(this, navigation.class);
        startActivity(myIntent);
    }

    public void connect_server(String ip, String resource_id, String realm_id, String username) {
        if (lf_resource != null) {
            lf_resource.do_run = false;
            try {
                lf_resource.join(); //wait until it dies
            }
            catch (Exception ee) {
                ee.printStackTrace();
            }
        }

        lf_resource = new LF_Resource(this, ip, resource_id, realm_id, username, getApplicationContext());
        lf_resource.start();
    }

    public void notifyCxChanged() {
        runOnUiThread(new Runnable() {
            public void run() {
                _notifyCxChanged();
            }
        });
    }

    public void updateRealmInfo() {
        String new_resource_id = lf_resource.getResource();
        String new_realm_id = lf_resource.getRealm();
        String ip = lf_resource.getRemoteHost();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        Log.e("shivam-iron", new_resource_id);
        editor.putString("server_ip-" + ip, ip);
        editor.putString("resource_id-" + ip, new_resource_id);
        editor.putString("realm_id-" + ip, new_realm_id);
        editor.putString("user_name-" + ip, u_name.getText().toString());
        editor.putString("current_ip", ip);
        editor.putString("current_username", u_name.getText().toString());
        editor.putString("current_resource", new_resource_id);
        editor.putString("current_realm", new_realm_id);
        editor.apply();
        editor.commit();
        navigation.setRealmInfoTextUI();
    }

    public void _notifyCxChanged() {
        state = lf_resource.lfresource.get_state();
        Log.e("log", "notifyCxChanged, state: " + state);

        if (lf_resource.getConnState()) {
            updateRealmInfo();
            // CardUtils cardUtils = new CardUtils(getApplicationContext());
            openServerConnection();
        }
        else {
            if (StartupActivity.active){
                return;
            }
            finishAffinity();
            Toast.makeText(getApplicationContext(), "Disconnected from Server, Closing the View", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(navigation.context, StartupActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(myIntent);
            button.setEnabled(true);
            // TODO: How to switch back to first view so user can reconnect if they want?
        }
    }

    // Function to check and request permission
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // Get locally stored sharedpreferences that stays even if app is closed
    public static Hashtable getLFResourceCredentials(){
        Hashtable<String, String> data = new Hashtable<String, String>();
        Map<String, ?> keys = sharedpreferences.getAll();
        if (keys.keySet().size() > 0) {
            for (String key : keys.keySet()) {
                data.put(key, keys.get(key).toString());
            }
        }
        return data;
    }

    // Set the sharedpreference for locally stored data
    // TODO:  Call this once lfresource is assigned a realm and resource.
    public int setLFResourceCredentials(String server_ip, String realm, String resource_id){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("server_ip-" + server_ip, server_ip);
        editor.putString("resource_id-" + server_ip, resource_id);
        editor.putString("realm_id-" + server_ip ,realm);
        editor.apply();
        editor.commit();
        return -1;
    }

    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }



    private void turnGPSOff(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

    public void CheckGpsStatus(){
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;

        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(GpsStatus == true) {
//            textview.setText("GPS Is Enabled");
        } else {
            Toast.makeText(getApplicationContext(), "Please Enable GPS!", Toast.LENGTH_LONG).show();
            Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);
//            textview.setText("GPS Is Disabled");
        }
    }
    public void turnGPSOn(){
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){
            //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            sendBroadcast(poke);
        }
    }

}
