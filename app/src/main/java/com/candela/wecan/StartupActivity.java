package com.candela.wecan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.candela.wecan.tests.base_tools.LF_Resource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        getSupportActionBar().hide();
        button = (Button) findViewById(R.id.enter_button);
        server_ip = findViewById(R.id.ip_enter_page);
//        LF_Resource p = new LF_Resource(143, "192.168.52.100", "2");
//        p.start();
        FileInputStream fis = null;
        try {
            fis =openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String ip;

            ip= br.readLine();
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
                String ip = server_ip.getText().toString();
                LF_Resource p = new LF_Resource(143, ip, "2");
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
                            openServerConnection();
                        }
                    }
                }, 2000);

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
}