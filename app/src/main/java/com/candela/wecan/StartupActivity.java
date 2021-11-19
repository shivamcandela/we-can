package com.candela.wecan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.candela.wecan.tests.base_tools.LF_Resource;
import com.candela.wecan.tests.base_tools.Logcat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
    private static final String FILE_NAME = "data.conf";
    private TextView server_ip;
    static int state;
    //    private Boolean server_connected_status = false;
    private String ip, ssid, passwd, resource_id;
    public String new_resource_id;
    private String realm_id ="-1";
    private String new_realm_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        getSupportActionBar().hide();
        button = (Button) findViewById(R.id.enter_button);
        server_ip = findViewById(R.id.ip_enter_page);
//        Logcat logca = new Logcat();
//        logca.sg();

        /* Checks if external storage is available for read and write */

        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            ip= br.readLine();
            ssid = br.readLine();
            passwd = br.readLine();
            resource_id = br.readLine();
            realm_id = br.readLine();
            server_ip.setText(ip);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String resource_code = "-1";
//                String new_resource_id = resource_id;
                System.out.println("hoja bhai");
                System.out.println(server_ip.getText() + resource_id + new_resource_id);
                String new_ip = server_ip.getText().toString().trim();
                if( new_ip.length() == 0 )
                    server_ip.setError( "IP is required!" );
                else{
                    if (ip == null){
                        resource_id = "-1";
                        connect_server(new_ip, resource_id, realm_id, v);
                    }
                    else if(ip.equals(new_ip)){
                        if (resource_id == null){
                            resource_id = "-1";
                            connect_server(new_ip, resource_id, realm_id, v);

                        }else{

                            connect_server(new_ip, resource_id, realm_id, v);
                        }
                    }
                    else if (!(ip.equals(new_ip))){
                        resource_id = "-1";
                        connect_server(new_ip, resource_id, realm_id, v);
                        Log.d("onClick: ", "IP NOT EQUAL");
                    }

                }

            }
        });
//        LF_Resource p = new LF_Resource(143);
//        p.start();
    }

//
//        Intent myIntent = new Intent(this, ServerConnection.class);
//        startActivity(myIntent);

    public void openServerConnection () {

        Intent myIntent = new Intent(this, navigation.class);
        startActivity(myIntent);


    }

    public void connect_server(String ip, String resource_id, String realm_id, View v){
        LF_Resource p = new LF_Resource(143, ip, resource_id, realm_id, getApplicationContext());
        p.start();
        state = p.lfresource.get_state();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                state = p.lfresource.get_state();
                if (state == STOPPED){
                    Toast.makeText(v.getContext(), "Server STOPPED", Toast.LENGTH_LONG).show();
                }
                if (state == STARTING){
                    Toast.makeText(v.getContext(), "Server is STARTING", Toast.LENGTH_LONG).show();
                }
                if (state == RUNNING){
                    Toast.makeText(v.getContext(), "Server is RUNNING", Toast.LENGTH_LONG).show();
                }
            }
        }, 1000);

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.removeCallbacks(this);
                state = p.lfresource.get_state();
                if (state == RUNNING){
                    Toast.makeText(v.getContext(), "Connected to LANforge Server", Toast.LENGTH_LONG).show();
                    new_resource_id = p.getResource();
                    new_realm_id = p.getRealm();
                    System.out.println("sprideman" + new_resource_id);
                    save_db(ip, new_resource_id, new_realm_id);
                    openServerConnection();
                }
            }
        }, 1000);


    }

    public void save_db(String ip, String new_resource_id, String new_realm_id){
        System.out.println("chaman");
        String data = ip + "\n" + ssid + "\n" + passwd + "\n" + new_resource_id + "\n" + new_realm_id;
//                    server_connected_status = true;
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fos.write(data.getBytes(StandardCharsets.UTF_8));
//                        Toast.makeText(v.getContext(), "Configuration Saved Successfully ", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("onClick: ", "Data ==> " + ip);
        Log.d("onClick: ", "SSID ==> " + ssid);
        Log.d("onClick: ", "PASS ==> " + passwd);
        Log.d("onClick: ", "RES_ID ==> " + resource_id);
    }
}