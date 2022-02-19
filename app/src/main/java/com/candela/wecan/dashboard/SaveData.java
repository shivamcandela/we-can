package com.candela.wecan.dashboard;

import static com.candela.wecan.tests.base_tools.HomeTableManager.up_down_global;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.candela.wecan.navigation;
import com.candela.wecan.ui.home.HomeFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class SaveData extends ActivityCompat implements Runnable{
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
//                                    Data Saving in csv format
//        try {

        WifiManager wifiManager = (WifiManager) navigation.context. getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiinfo = wifiManager.getConnectionInfo();
        String IP = Formatter.formatIpAddress(wifiinfo.getIpAddress());
        String SSID = wifiinfo.getSSID();
        String BSSID = wifiinfo.getBSSID();
        int Rssi = wifiinfo.getRssi();
        String LinkSpeed = wifiinfo.getLinkSpeed() + " Mbps";
        String channel = wifiinfo.getFrequency() + " MHz";
        long availMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long usedMem = totalMem - availMem;
        String uplink = HomeFragment.up_down_data[2];
        String downlink = HomeFragment.up_down_data[3];
        String cpu_used_percent = String.format("%.2f", (usedMem / (double) totalMem) * 100);
        String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date()).replace(",","");
        String livedata = currentDateTimeString + "," + IP + "," + SSID + "," + BSSID + "," + Rssi
                + "," + LinkSpeed + "," + uplink + "," + downlink + "," + channel + "," + cpu_used_percent + "\n";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
            //                                  Getting file as Test Name
            SharedPreferences sharedPreferences = HomeFragment.home_fragment_activity.getSharedPreferences("userdata", Context.MODE_PRIVATE);
            Map<String, ?> keys = sharedPreferences.getAll();
            String test_name = (String) keys.get("test_name");
            File appDirectory = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/WE-CAN");
            File logDirectory = new File(appDirectory + "/LiveData/");
            File logFile = new File(logDirectory, test_name + ".csv");
            File file = new File(String.valueOf(logFile));
            if (!logDirectory.exists()) {
                logDirectory.mkdirs();
                System.out.println("logDirectory:  " + logDirectory);
            }
            if (file.exists()) {
                try {
                    FileOutputStream stream = new FileOutputStream(logFile, true);
                    stream.write(livedata.getBytes());
                    stream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                FileOutputStream stream;
                try {
                    stream = new FileOutputStream(logFile);
                    stream.write("Date-Time,IP,SSID,BSSID,signal,Linkspeed,Uplink,Downlink,Channel,CPU_Utilization\n".getBytes());
                    stream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //Calling Runable at time interval
//        } catch (Exception e) {
//            System.out.println(e);
//        }
        HomeFragment.handler_save_data.postDelayed(HomeFragment.runnable_save_data, 1000);
    }
}
