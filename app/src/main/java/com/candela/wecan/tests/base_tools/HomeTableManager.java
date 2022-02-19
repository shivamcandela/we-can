package com.candela.wecan.tests.base_tools;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.candela.wecan.R;
import com.candela.wecan.ui.home.HomeFragment;
import com.cardiomood.android.controls.gauge.SpeedometerGauge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import candela.lfresource.LANforgeMgr;
import candela.lfresource.StringKeyVal;

public class HomeTableManager extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Runnable runnable_live_data;
    public static String[] up_down_global;
    boolean flag = false;



    @Override
    public void onClick(View v) {
        Log.e("mark 42", String.valueOf(v.getId()));
        switch (v.getId()) {
            case R.id.share_btn:
                ShareButtonListener();
                break;
            case R.id.speedometer:
                Speedometer();
                break;
            case R.id.system_info_btn:
                SystemInfoListener();
                break;
            case R.id.rxtx_btn:
                LiveBtnListener();
                break;

    }


}
    /* Share Button OnClick Listener */
    private void ShareButtonListener() {
        Log.e("Handler_iron", "ShareButtonListener");
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("*/*");
        File dataDirectory = new File(Environment.getExternalStorageDirectory() + "/WE-CAN/LiveData/LiveData.csv");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "WE-CAN Live Data Sharing");
        Uri uri;
        if (Build.VERSION.SDK_INT < 24) {
            uri = Uri.fromFile(dataDirectory);
        } else {
            uri = Uri.parse(dataDirectory.getPath());
        }
//                        Uri uri = Uri.fromFile(dataDirectory);
//                        Uri uri1 = Uri.parse(dataDirectory.getPath());;
        sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
        HomeFragment.home_fragment_activity.startActivity(Intent.createChooser(sharingIntent, null));
    }

    /* Sys Info Tab OnClick Listener */
    private void SystemInfoListener() {
        Log.e("Handler_iron", "SystemInfoListener");
        HomeFragment.handler_speedometer_thread.removeCallbacks(HomeFragment.runnable_speedometer);
        HomeFragment.live_table_flag = false;
        HomeFragment.handler_live_data.removeCallbacks(HomeFragment.runnable_live);
        HomeFragment.scan_table_flag = false;
        HomeFragment.speedometer_linear.setVisibility(View.GONE);
        HomeFragment.up_down.setVisibility(View.GONE);
        HomeFragment.sys_table.removeAllViews();
        HomeFragment.system_info_btn.setTextColor(Color.GREEN);
        HomeFragment.live_btn.setTextColor(Color.WHITE);
        HomeFragment.scan_btn.setTextColor(Color.WHITE);
        HomeFragment.speedometer_btn.setTextColor(Color.WHITE);
        Vector<StringKeyVal> wifi_capabilities = new Vector<StringKeyVal>();
        Vector<StringKeyVal> wifi_mode = new Vector<StringKeyVal>();
        Vector<StringKeyVal> wifi_encryption = new Vector<StringKeyVal>();

        SharedPreferences sharedPreferences = HomeFragment.home_fragment_activity.getSharedPreferences("userdata", Context.MODE_PRIVATE);
        Map<String, ?> keys = sharedPreferences.getAll();
        String password = (String) keys.get("current-passwd");

        Map<String, String> system_info = new HashMap<String, String>();
        WifiManager wifiManager = (WifiManager) HomeFragment.home_fragment_activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiinfo = wifiManager.getConnectionInfo();


        system_info.put("MANUFACTURER", Build.MANUFACTURER);
        system_info.put("MODEL", Build.MODEL);
        system_info.put("PRODUCT", Build.PRODUCT);
        system_info.put("RELEASE", Build.VERSION.RELEASE);
        system_info.put("INCREMENTAL", Build.VERSION.INCREMENTAL);
        system_info.put("SDK No.", String.valueOf(Build.VERSION.SDK_INT));
        system_info.put("BOARD", Build.BOARD);
        system_info.put("BRAND", Build.BRAND);
        system_info.put("CPU_ABI", Build.CPU_ABI);
        system_info.put("HARDWARE", Build.HARDWARE);
        system_info.put("HOST", Build.HOST);
        system_info.put("ID", Build.ID);
//                        system_info.put("PHONE IP", Formatter.formatIpAddress(wifiinfo.getIpAddress()));

        Boolean AC_11 = null;
        Boolean AX_11 = null;
        Boolean N_11 = null;
        Boolean legacy = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AC_11 = wifiManager.isWifiStandardSupported(ScanResult
                    .WIFI_STANDARD_11AC);
        } else {
            AC_11 = false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AX_11 = wifiManager.isWifiStandardSupported(ScanResult
                    .WIFI_STANDARD_11AX);
        } else {
            AX_11 = false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            N_11 = wifiManager.isWifiStandardSupported(ScanResult
                    .WIFI_STANDARD_11N);
        } else {
            N_11 = false;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            legacy = wifiManager.isWifiStandardSupported(ScanResult
                    .WIFI_STANDARD_LEGACY);
        } else {
            legacy = false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            wifi_capabilities.add(new StringKeyVal("5G", String.valueOf((wifiManager.is5GHzBandSupported()))));
        } else {
            wifi_capabilities.add(new StringKeyVal("5G", String.valueOf(true)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            wifi_capabilities.add(new StringKeyVal("6G", String.valueOf((wifiManager.is6GHzBandSupported()))));
        } else {
            wifi_capabilities.add(new StringKeyVal("6G", String.valueOf(true)));
        }

        if (Build.VERSION.SDK_INT >= 31) {
            // This was added in API 31, I guess before then 2.4 was always supported.
            wifi_capabilities.add(new StringKeyVal("2G", String.valueOf((wifiManager.is24GHzBandSupported())))); // This line gives an error
        } else {
            wifi_capabilities.add(new StringKeyVal("2G", String.valueOf(true)));
        }

        wifi_mode.add(new StringKeyVal("11-AC", String.valueOf(AC_11)));
        wifi_mode.add(new StringKeyVal("11-AX", String.valueOf(AX_11)));
        wifi_mode.add(new StringKeyVal("11-N", String.valueOf(N_11)));
        wifi_mode.add(new StringKeyVal("LEGACY", String.valueOf(legacy)));

        HomeFragment.sys_table.setPadding(10, 0, 10, 0);
        TableRow heading = new TableRow(HomeFragment.home_fragment_activity);
        heading.setBackgroundColor(Color.rgb(120, 156, 175));
        TextView sl_head = new TextView(HomeFragment.home_fragment_activity);
        sl_head.setText(" SL. ");
        sl_head.setTextColor(Color.BLACK);
        sl_head.setGravity(Gravity.CENTER);
        heading.addView(sl_head);
        TextView key_head = new TextView(HomeFragment.home_fragment_activity);
        key_head.setText(" KEY ");
        key_head.setTextColor(Color.BLACK);
        key_head.setGravity(Gravity.CENTER);
        heading.addView(key_head);
        TextView val_head = new TextView(HomeFragment.home_fragment_activity);
        val_head.setText(" VALUE ");
        val_head.setTextColor(Color.BLACK);
        val_head.setGravity(Gravity.CENTER);
        heading.addView(val_head);
        HomeFragment.sys_table.addView(heading);

        int i = 1;
        for (Map.Entry<String, String> entry : system_info.entrySet()) {
            TableRow tbrow = new TableRow(HomeFragment.home_fragment_activity);
            if (i % 2 == 0) {
                tbrow.setBackgroundColor(Color.rgb(211, 211, 211));
            } else {
                tbrow.setBackgroundColor(Color.rgb(192, 192, 192));
            }

            TextView sl_view = new TextView(HomeFragment.home_fragment_activity);
            sl_view.setText(String.valueOf(i) + ".");
            sl_view.setTextSize(15);
            sl_view.setTextColor(Color.BLACK);
            sl_view.setGravity(Gravity.CENTER);
            tbrow.addView(sl_view);
            TextView key_view = new TextView(HomeFragment.home_fragment_activity);
            key_view.setText(entry.getKey());
            key_view.setTextSize(15);
            key_view.setTextColor(Color.BLACK);
            key_view.setGravity(Gravity.CENTER);
            tbrow.addView(key_view);
            TextView val_view = new TextView(HomeFragment.home_fragment_activity);
            val_view.setText(entry.getValue());
            val_view.setTextSize(15);
            val_view.setTextColor(Color.BLACK);
            val_view.setGravity(Gravity.CENTER);
            tbrow.addView(val_view);
            HomeFragment.sys_table.addView(tbrow);
            i = i + 1;
        }

    }

    /* Live Data Tab OnClick Listener */
    private void LiveBtnListener(){
        Log.e("Handler_iron", "LiveBtnListener");
        HomeFragment.speedometer_linear.setVisibility(View.GONE);
        if (HomeFragment.handler_speedometer_thread != null) {
            HomeFragment.handler_speedometer_thread.removeCallbacks(HomeFragment.runnable_speedometer);
        }
        if (HomeFragment.live_table_flag == false){
            HomeFragment.handler_live_data.removeCallbacks(HomeFragment.runnable_live);
        }
        HomeFragment.live_table_flag = true;
        HomeFragment.scan_table_flag = false;
        HomeFragment.up_down.setVisibility(View.GONE);
        HomeFragment.handler_live_data.post(HomeFragment.runnable_live);
    }

    /* Speedometer Tab OnClick Listener */
    private void Speedometer(){
        Log.e("Handler_iron", "Speedometer");
        HomeFragment.live_table_flag = false;
        HomeFragment.handler_live_data.removeCallbacks(HomeFragment.runnable_live);
        HomeFragment.live_table.removeAllViews();
        HomeFragment.scan_table.removeAllViews();
        HomeFragment.scan_table_flag = true;
        HomeFragment.system_info_btn.setTextColor(Color.WHITE);
        HomeFragment.live_btn.setTextColor(Color.WHITE);
        HomeFragment.scan_btn.setTextColor(Color.WHITE);
        HomeFragment.speedometer_btn.setTextColor(Color.GREEN);
        HomeFragment.speedometer_linear.setVisibility(View.VISIBLE);
        HomeFragment.up_down.setVisibility(View.VISIBLE);

        HomeFragment.speedometerup.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });

        HomeFragment.speedometerdown.setLabelConverter(new SpeedometerGauge.LabelConverter() {
            @Override
            public String getLabelFor(double progress, double maxProgress) {
                return String.valueOf((int) Math.round(progress));
            }
        });
        HomeFragment.handler_speedometer_thread.post(HomeFragment.runnable_speedometer);

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Handler handler = new Handler();
        final Runnable save_data = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                flag = buttonView.isChecked();
//                                    Data Saving in csv format
                try {
                    WifiManager wifiManager = (WifiManager) getApplicationContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
                    String uplink = up_down_global[2];
                    String downlink = up_down_global[3];
                    String cpu_used_percent = String.format("%.2f", (usedMem / (double) totalMem) * 100);
                    String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
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
                                stream.write("Date/Time,IP,SSID,BSSID,signal,Linkspeed,Uplink,Downlink,Channel,CPU_Utilization\n".getBytes());
                                stream.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //Calling Runable at time interval
                }
                catch (Exception e){
                    System.out.println(e);
                }

                if (flag) {
                    handler.postDelayed(this, 1000);
                } else {
                    handler.removeCallbacks(this);
                }
            }
        };
        handler.post(save_data);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }



}
