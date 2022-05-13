package com.candela.wecan;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.candela.wecan.tests.base_tools.LF_Resource;

import java.util.Hashtable;
import java.util.Map;

//import candela.lfresource.lfresource;
//import com.candela.wecan.tests.base_tools.LF_Resource;


/**
 * Startup Activity for Candela WE-CAN
 *
 */
public class StartupActivity extends AppCompatActivity {

    private Button button;
    static final int STARTING = 0;
    static final int RUNNING = 1;
    static final int STOPPED = 2;
    private TextView server_ip,u_name,test_name_tv;
    static int state;
    private String ssid, passwd;
    public Context context;
    protected static LF_Resource lf_resource = null;
    public View my_view = null;
    public static SharedPreferences sharedpreferences = null;
    public static boolean active=false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        getSupportActionBar().hide();
        button = (Button) findViewById(R.id.enter_button);
        server_ip = findViewById(R.id.ip_enter_page);
        u_name = findViewById(R.id.user_name);
        test_name_tv = findViewById(R.id.test_name);
        sharedpreferences = getBaseContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        Map<String,?> keys = sharedpreferences.getAll();
        String last_ip = (String) keys.get("current_ip");
        String user_name = (String) keys.get("current_username");
        String test_name = (String) keys.get("current_testname");
        server_ip.setText(last_ip);
        u_name.setText(user_name);
        test_name_tv.setText(test_name);
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
        //        networkSniffTask.execute();
//        Intent myIntent = new Intent(this, ClientConnectivityConfiguration.class);
//        startActivity(myIntent);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                // Condition for minimum character limits
                if (u_name.getText().toString().replaceAll("\\s", "").length() <= 4 ||
                        test_name_tv.getText().toString().replaceAll("\\s", "").length() <= 4) {
                    Toast.makeText(getApplicationContext(), "user-name and test-name should be of min 5 characters", Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);
                    return;
                }

                // If everything is good, then do the logic
                else {
                    // u_name.getText()
                    // server_ip.getText()


                    // If server is already registered
                    Hashtable data = getLFResourceCredentials();
                    if (data.containsKey("server_ip-" + server_ip.getText())){
                        connect_server(data.get("server_ip-" + server_ip.getText()).toString(),
                                data.get("resource_id-" + server_ip.getText()).toString(),
                                data.get("realm_id-" + server_ip.getText()).toString(), view);

                    }
                    // Registering server details if it is not registered already
                    else{
                        setLFResourceCredentials(server_ip.getText().toString(),
                                "-1",
                                "-1",
                                u_name.getText().toString(),
                                test_name_tv.getText().toString());
                        connect_server(server_ip.getText().toString(), "-1", "-1", view);
                    }
                }
            }
        });
    }

    public void openServerConnection () {
        if (navigation.active){
            return;
        }
       Intent myIntent = new Intent(this, navigation.class);
       startActivity(myIntent);
    }

    public void connect_server(String ip, String resource_id, String realm_id, View v){
        my_view = v;

        if (lf_resource != null) {
            lf_resource.do_run = false;
            try {
                lf_resource.join(); //wait until it dies
            }
            catch (Exception ee) {
                ee.printStackTrace();
            }
        }

        lf_resource = new LF_Resource(this, ip, resource_id, realm_id, getApplicationContext());
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
        editor.putString("current_testname", test_name_tv.getText().toString());
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
            Toast.makeText(getApplicationContext(), "Disconnected from Server, Closing the View", Toast.LENGTH_LONG).show();
            Intent myIntent = new Intent(this, StartupActivity.class);
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
    public int setLFResourceCredentials(String server_ip, String realm, String resource_id, String username, String test_name){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("server_ip-" + server_ip, server_ip);
        editor.putString("resource_id-" + server_ip, resource_id);
        editor.putString("realm_id-" + server_ip ,realm);
        editor.putString("user_name-" + server_ip, username);
        editor.putString("test_name-" + server_ip, test_name);
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

}
