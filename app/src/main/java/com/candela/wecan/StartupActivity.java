package com.candela.wecan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.candela.wecan.tests.base_tools.LF_Resource;

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
    protected LF_Resource lf_resource = null;
    public View my_view = null;
    SharedPreferences sharedpreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        getSupportActionBar().hide();
        button = (Button) findViewById(R.id.enter_button);
        server_ip = findViewById(R.id.test_network_encrypion);
        u_name = findViewById(R.id.test_network_ssid);
        test_name_tv = findViewById(R.id.test_network_passkey);
        sharedpreferences = getBaseContext().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        Map<String,?> keys = sharedpreferences.getAll();
        String last_ip = (String) keys.get("last");
        String user_name = (String) keys.get("user_name");
        String test_name = (String) keys.get("test_name");
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
        };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
//        Intent myIntent = new Intent(this, ClientConnectivityConfiguration.class);
//        startActivity(myIntent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (keys.keySet().contains(server_ip.getText().toString())){
                    Map<String,?> keys = sharedpreferences.getAll();
                    String ip = server_ip.getText().toString();
                    String realm_id = (String) keys.get("realm-" + ip);
                    String resource_id = (String) keys.get("resource-" + ip);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("user_name" ,  u_name.getText().toString());
                    editor.putString("test_name" ,  test_name_tv.getText().toString());
                    editor.apply();
                    editor.commit();
                    connect_server(server_ip.getText().toString(), resource_id, realm_id, view);
                }
                else{
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(server_ip.getText().toString(), "-1");
                    editor.putString("resource-" + server_ip.getText().toString(), "-1");
                    editor.putString("realm-" + server_ip.getText().toString(), "-1");
                    editor.putString("user_name" ,  u_name.getText().toString());
                    editor.putString("test_name" ,  test_name_tv.getText().toString());
                    editor.apply();
                    editor.commit();
                    Map<String,?> keys = sharedpreferences.getAll();
                    String ip = server_ip.getText().toString();
                    String realm_id = (String) keys.get("realm-" + ip);
                    String resource_id = (String) keys.get("resource-" + ip);
                    connect_server(server_ip.getText().toString(), resource_id, realm_id, view);
                }
            }
        });
    }

    public void openServerConnection () {
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

       editor.putString(ip, "-1");
       editor.putString("last", ip);
       editor.putString("resource-" + ip, new_resource_id);
       editor.putString("realm-" + ip, new_realm_id);

       editor.putString("current-ip", ip);
       editor.putString("current-resource", new_resource_id);
       editor.putString("current-realm", new_realm_id);

       editor.apply();
       editor.commit();
    }

    public void _notifyCxChanged() {
        state = lf_resource.lfresource.get_state();
        Log.e("log", "notifyCxChanged, state: " + state);

        if (lf_resource.getConnState()) {
            Toast.makeText(my_view.getContext(), "Connected to LANforge Server", Toast.LENGTH_SHORT).show();
            updateRealmInfo();
            // CardUtils cardUtils = new CardUtils(getApplicationContext());
            openServerConnection();
        }
        else {
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


}
