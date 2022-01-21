package com.candela.wecan.ui.home;

import static android.net.wifi.WifiConfiguration.*;
import static android.view.KeyEvent.KEYCODE_BACK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.widget.Toast;

import com.candela.wecan.R;
import com.candela.wecan.databinding.FragmentHomeBinding;
import com.candela.wecan.tests.base_tools.CardUtils;
import com.candela.wecan.tests.base_tools.GetPhoneWifiInfo;
import com.cardiomood.android.controls.gauge.SpeedometerGauge;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import candela.lfresource.StringKeyVal;
import candela.lfresource.LANforgeMgr;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private static final String FILE_NAME = "data.conf";
    private TextView ip_show, link_speed;
    public Boolean live_table_flag = false,scan_table_flag=false, flag;
    public static HomeFragment instance = null;
    public String[] up_down_global;
    TableLayout sys_table = null;
    TableLayout live_table = null;
    TableLayout scan_table = null;

    long last_bps_time = 0;
    long last_rx_bytes = 0;
    long last_tx_bytes = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        instance = this;

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @SuppressLint({"MissingPermission", "NewApi"})
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onChanged(@Nullable String s) {
                Button scan_btn, system_info_btn, live_btn;
                scan_btn = getActivity().findViewById(R.id.scan_data);
                scan_btn.setEnabled(false);
                system_info_btn = getActivity().findViewById(R.id.system_info_btn);
                live_btn = getActivity().findViewById(R.id.rxtx_btn);
                ImageView share_btn = getActivity().findViewById(R.id.share_btn);
//                SWITCH BUTTON TO SAVE DATA....
                Switch switch_btn;
                switch_btn = getActivity().findViewById(R.id.save_data_switch);
//                TABLE LAYOUT FOR SHOWING TABLE DATA...
                sys_table = (TableLayout) getView().findViewById(R.id.table);
                live_table = (TableLayout) getView().findViewById(R.id.table);
                scan_table = (TableLayout) getView().findViewById(R.id.table);

                ip_show = getView().findViewById(R.id.server_ip_info);
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                Map<String, ?> keys = sharedPreferences.getAll();
                String username = (String) keys.get("user_name");
                ip_show = getView().findViewById(R.id.server_ip_info);
                String current_ip = (String) keys.get("current-ip");
                String current_resource = (String) keys.get("current-resource");
                String current_realm = (String) keys.get("current-realm");
                ip_show.setText("User-Name: " + username + "\nServer: " + current_ip + "\nRealm: " + current_realm + "\nResource: " + current_resource);

//                LINK SPEED UP/DOWN
                link_speed = getView().findViewById(R.id.link_speed);

                last_bps_time = System.currentTimeMillis();
                last_tx_bytes = TrafficStats.getTotalTxBytes();
                last_rx_bytes = TrafficStats.getTotalRxBytes();

                SpeedometerGauge speedometerdown = (SpeedometerGauge) getActivity().findViewById(R.id.speedometerdown);
                speedometerdown.setLabelConverter(new SpeedometerGauge.LabelConverter() {
                    @Override
                    public String getLabelFor(double progress, double maxProgress) {
                        return String.valueOf((int) Math.round(progress));
                    }
                });

                SpeedometerGauge speedometerup = (SpeedometerGauge) getView().findViewById(R.id.speedometerup);
                speedometerup.setLabelConverter(new SpeedometerGauge.LabelConverter() {
                    @Override
                    public String getLabelFor(double progress, double maxProgress) {
                        return String.valueOf((int) Math.round(progress));
                    }
                });
                Handler handler = new Handler();
                final Runnable runnable_link = new Runnable() {
                    @Override
                    public void run() {
                        String up_down[] = updateBpsDisplay();
                        up_down_global = up_down;
                        int downlink = Integer.parseInt(up_down[0]);
                        int uplink = Integer.parseInt(up_down[1]);
//              Configure upload value range colors
                        speedometerup.setLabelTextSize(10);
                        speedometerup.setMaxSpeed(500);
                        speedometerup.setMajorTickStep(25);
                        speedometerup.addColoredRange(0, 25, Color.RED);
                        speedometerup.addColoredRange(25, 100, Color.YELLOW);
                        speedometerup.addColoredRange(100, 500, Color.GREEN);
//                        Set the uplink value
                        speedometerup.setSpeed(uplink);

//                      Download Starts here
//                      Configure download value range colors
                        speedometerdown.setLabelTextSize(10);
                        speedometerdown.setMaxSpeed(500);
                        speedometerdown.setMajorTickStep(25);
                        speedometerdown.addColoredRange(0, 25, Color.RED);
                        speedometerdown.addColoredRange(25, 100, Color.YELLOW);
                        speedometerdown.addColoredRange(100, 500, Color.GREEN);
//                        Set the downlink value
                        speedometerdown.setSpeed(downlink);
//
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.post(runnable_link);

//              Share Data Button
                share_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                        startActivity(Intent.createChooser(sharingIntent, null));
                    }
                });

//              Save Data to csv
                switch_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        flag = switch_btn.isChecked();
                        Handler handler = new Handler();
                        final Runnable save_data = new Runnable() {
                            @Override
                            public void run() {
//                                    Data Saving in csv format
                                WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
                                        + "," + LinkSpeed + "," + uplink  + "," + downlink + ","+ channel + ","  + cpu_used_percent + "\n";

                                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                                        Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
//                                  Getting file as Test Name
                                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                                    Map<String,?> keys = sharedPreferences.getAll();
                                    String test_name= (String) keys.get("test_name");
                                    File appDirectory = new File(String.valueOf(Environment.getExternalStorageDirectory()) + "/WE-CAN");
                                    File logDirectory = new File(appDirectory + "/LiveData/");
                                    File logFile = new File(logDirectory, test_name + ".csv");
                                    File file = new File(String.valueOf(logFile));
                                    if (!logDirectory.exists()){
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
                                            stream.write("Date/Time,IP,SSID,BSSID,Rssi,Linkspeed,Uplink,Downlink,Channel,CPU_Utilization\n".getBytes());
                                            stream.close();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                //Calling Runable at time interval
                                if (flag) {
                                    handler.postDelayed(this, 1000);
                                } else {
                                    handler.removeCallbacks(this);
                                }
                            }
                        };
                        handler.post(save_data);
                    }
                });

//              Getting System Information
                system_info_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        live_table_flag = false;
                        scan_table_flag = false;
                        sys_table.removeAllViews();
                        Vector<StringKeyVal> wifi_capabilities = new Vector<StringKeyVal>();
                        Vector<StringKeyVal> wifi_mode = new Vector<StringKeyVal>();
                        Vector<StringKeyVal> wifi_encryption = new Vector<StringKeyVal>();

                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
                        Map<String, ?> keys = sharedPreferences.getAll();
                        String password = (String) keys.get("current-passwd");

                        Map<String, String> system_info = new HashMap<String, String>();
                        WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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
                        system_info.put("PHONE IP", Formatter.formatIpAddress(wifiinfo.getIpAddress()));

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

                        sys_table.setPadding(10, 0, 10, 0);
                        TableRow heading = new TableRow(getActivity());
                        heading.setBackgroundColor(Color.rgb(120, 156, 175));
                        TextView sl_head = new TextView(getActivity());
                        sl_head.setText(" SL. ");
                        sl_head.setTextColor(Color.BLACK);
                        sl_head.setGravity(Gravity.CENTER);
                        heading.addView(sl_head);
                        TextView key_head = new TextView(getActivity());
                        key_head.setText(" KEY ");
                        key_head.setTextColor(Color.BLACK);
                        key_head.setGravity(Gravity.CENTER);
                        heading.addView(key_head);
                        TextView val_head = new TextView(getActivity());
                        val_head.setText(" VALUE ");
                        val_head.setTextColor(Color.BLACK);
                        val_head.setGravity(Gravity.CENTER);
                        heading.addView(val_head);
                        sys_table.addView(heading);

                        int i = 1;
                        for (Map.Entry<String, String> entry : system_info.entrySet()) {
                            TableRow tbrow = new TableRow(getActivity());
                            if (i % 2 == 0) {
                                tbrow.setBackgroundColor(Color.rgb(211, 211, 211));
                            } else {
                                tbrow.setBackgroundColor(Color.rgb(192, 192, 192));
                            }

                            TextView sl_view = new TextView(getActivity());
                            sl_view.setText(String.valueOf(i) + ".");
                            sl_view.setTextSize(15);
                            sl_view.setTextColor(Color.BLACK);
                            sl_view.setGravity(Gravity.CENTER);
                            tbrow.addView(sl_view);
                            TextView key_view = new TextView(getActivity());
                            key_view.setText(entry.getKey());
                            key_view.setTextSize(15);
                            key_view.setTextColor(Color.BLACK);
                            key_view.setGravity(Gravity.CENTER);
                            tbrow.addView(key_view);
                            TextView val_view = new TextView(getActivity());
                            val_view.setText(entry.getValue());
                            val_view.setTextSize(15);
                            val_view.setTextColor(Color.BLACK);
                            val_view.setGravity(Gravity.CENTER);
                            tbrow.addView(val_view);
                            sys_table.addView(tbrow);
                            i = i + 1;
                        }
                    }
                });

//              live Data showing continuously on click Live Data Button
                live_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        live_table_flag = true;
                        scan_table_flag = false;
                        Handler handler = new Handler();
                        final Runnable r = new Runnable() {
                            @Override
                            public void run() {
//                                LocationManager locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//                                LocationListener locationListener = new LocationListener() {
//                                    public double latitude;
//                                    public double longitude;
//                                    @Override
//                                    public void onLocationChanged(Location location) {
//                                         TODO Auto-generated method stub
//                                        latitude = location.getLatitude();
//                                        longitude = location.getLongitude();
//                                        System.out.println("latitude :" +latitude + "\nlongitude: "+longitude);
//                                        double speed = location.getSpeed(); //spedd in meter/minute
//                                        speed = (speed*3600)/1000;      // speed in km/minute
//                                        System.out.println("speed in Km: "+speed);
//                                    }
//                                };
//                                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);


                                live_table.removeAllViews();
                                LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                if (getActivity() != null) {
                                WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                WifiInfo wifiinfo = wifiManager.getConnectionInfo();
                                String IP = null;
                                String SSID = null;
                                String BSSID = null;
                                int Rssi = 0;
                                String LinkSpeed = null;
                                String channel = null;
                                if (wifiinfo.getSupplicantState() == SupplicantState.COMPLETED) {
                                    IP = Formatter.formatIpAddress(wifiinfo.getIpAddress());
                                    SSID = wifiinfo.getSSID();
                                    BSSID = wifiinfo.getBSSID();
                                    Rssi = wifiinfo.getRssi();
                                    LinkSpeed = wifiinfo.getLinkSpeed() + " Mbps";
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        channel = wifiinfo.getFrequency() + " MHz";
                                    }
                                }
                                DhcpInfo Dhcp_details = wifiManager.getDhcpInfo();
                                String dns1 = Formatter.formatIpAddress(Dhcp_details.dns1);
                                String dns2 = Formatter.formatIpAddress(Dhcp_details.dns2);
                                String serverAddress = Formatter.formatIpAddress(Dhcp_details.serverAddress);
                                String gateway = Formatter.formatIpAddress(Dhcp_details.gateway);
                                String netmask = Formatter.formatIpAddress(Dhcp_details.netmask);
                                int leaseDuration = Dhcp_details.leaseDuration;
//                                String describeContents = Formatter.formatIpAddress(Dhcp_details.describeContents());

                                long availMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                                long totalMem = Runtime.getRuntime().totalMemory();
                                long usedMem = totalMem - availMem;
                                String cpu_used_percent = String.format("%.2f", (usedMem / (double) totalMem) * 100);
                                Map<String, String> live_data = new LinkedHashMap<String, String>();
                                live_data.put("IP", String.valueOf(IP));
                                live_data.put("SSID", SSID);
                                live_data.put("BSSID", BSSID);
                                live_data.put("Rssi", String.valueOf(Rssi) + " dBm");
                                live_data.put("LinkSpeed", LinkSpeed);
                                live_data.put("Channel", channel);
                                live_data.put("CPU util", cpu_used_percent + " %");
                                live_data.put("DNS1", dns1);
                                live_data.put("DNS2", dns2);
                                live_data.put("DHCP Server", serverAddress);
                                live_data.put("Gateway", gateway);
                                live_data.put("Netmask", netmask);
                                live_data.put("LeaseDuration", String.valueOf(leaseDuration) + " Sec");

//                                Table Heading
                                live_table.setPadding(10, 0, 10, 0);
                                TableRow heading = new TableRow(getActivity());
                                heading.setBackgroundColor(Color.rgb(120, 156, 175));
                                TextView sl_head = new TextView(getActivity());
                                sl_head.setText(" SL. ");
                                sl_head.setTextColor(Color.BLACK);
                                sl_head.setGravity(Gravity.CENTER);
                                heading.addView(sl_head);
                                TextView key_head = new TextView(getActivity());
                                key_head.setText(" KEY ");
                                key_head.setTextColor(Color.BLACK);
                                key_head.setGravity(Gravity.CENTER);
                                heading.addView(key_head);
                                TextView val_head = new TextView(getActivity());
                                val_head.setText(" VALUE ");
                                val_head.setTextColor(Color.BLACK);
                                val_head.setGravity(Gravity.CENTER);
                                heading.addView(val_head);
                                live_table.addView(heading);

                                int i = 1;
                                for (Map.Entry<String, String> entry : live_data.entrySet()) {
                                    TableRow tbrow = new TableRow(getActivity());
                                    if (i % 2 == 0) {
                                        tbrow.setBackgroundColor(Color.rgb(211, 211, 211));
                                    } else {
                                        tbrow.setBackgroundColor(Color.rgb(192, 192, 192));
                                    }

                                    TextView sl_view = new TextView(getActivity());
                                    sl_view.setText(String.valueOf(i) + ".");
                                    sl_view.setTextSize(15);
                                    sl_view.setTextColor(Color.BLACK);
                                    sl_view.setGravity(Gravity.CENTER);
                                    tbrow.addView(sl_view);
                                    TextView key_view = new TextView(getActivity());
                                    key_view.setText(entry.getKey());
                                    key_view.setTextSize(15);
                                    key_view.setTextColor(Color.BLACK);
                                    key_view.setGravity(Gravity.CENTER);
                                    tbrow.addView(key_view);
                                    TextView val_view = new TextView(getActivity());
                                    val_view.setText(entry.getValue());
                                    val_view.setTextSize(15);
                                    val_view.setTextColor(Color.BLACK);
                                    val_view.setGravity(Gravity.CENTER);
                                    tbrow.addView(val_view);
                                    live_table.addView(tbrow);
                                    i = i + 1;
                                }
                                if (live_table_flag == true) {
                                    handler.postDelayed(this, 1000);
                                } else {
                                    handler.removeCallbacks(this);
                                }
                            }
                            }
                        };
                        handler.post(r);
                    }
                });

//              Perform Click on System Info
                live_btn.performClick();

//              Scanning Nearest Wi-Fi
                scan_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scan_table_flag = true;
                        live_table_flag = false;

                        // Do initial update from any saved scan results.
                        _scanCompleted(true);

                        Handler handler = new Handler();
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                // Scan wi-fi
                                if (getActivity() != null){
                                    WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                                    wifiManager.setWifiEnabled(true);
                                    wifiManager.startScan();
                                    //Log.e("log", "startScan called in HomeFragment");
                                }

                                if (scan_table_flag == true) {
                                    // NOTE:  Scans are normally limited to around one every 30 seconds, but
                                    // there is a developer option in Android 10 to allow it to run faster.
                                    // Request poll every 10 seconds.
                                    handler.postDelayed(this, 10000);
                                } else {
                                    handler.removeCallbacks(this);
                                }
                            }
                        };
                        handler.post(runnable);
                    }
                });
            }
        });
        return root;
    }

    public String[] updateBpsDisplay() {

        long now = System.currentTimeMillis();
        double TimeDifference = now - last_bps_time;
        if (TimeDifference == 0) {
            return new String[] {"0", "0", "0", "0"}; // no div by zero error!
        }
        String Tx;
        String Rx;
        long rx_bytes = TrafficStats.getTotalRxBytes();
        long tx_bytes = TrafficStats.getTotalTxBytes();

        LANforgeMgr.setTrafficStats(now,
                rx_bytes, TrafficStats.getTotalRxPackets(),
                tx_bytes, TrafficStats.getTotalTxPackets(),
                TrafficStats.getMobileRxBytes(), TrafficStats.getMobileRxPackets(),
                TrafficStats.getMobileTxBytes(), TrafficStats.getMobileTxPackets());

        double rxDiff = rx_bytes - last_rx_bytes;
        double txDiff = tx_bytes - last_tx_bytes;
        double txbits = ((txDiff) * 1000 / TimeDifference) * 8;
        double rxbits = ((rxDiff) * 1000 / TimeDifference) * 8;

        last_rx_bytes = rx_bytes;
        last_tx_bytes = tx_bytes;
        last_bps_time = now;

        // TODO:  More efficient to test for high numbers first and only assing Rx/Tx string once
        if (rxbits >= 1000) {
            double rxKb = rxbits/1000;
            Rx = String.format("%.2f", rxKb) + " Kbps";
            if(rxKb >= 1000) {
                double rxMb = rxKb/1000;
                Rx = String.format("%.2f", rxMb) + " Mbps";
                if(rxMb >= 1000){
                    double rxGb = rxMb/1000;
                    Rx = String.format("%.2f", rxGb) + " Gbps";
                }
            }
        }
        else {
            Rx = (long)(rxbits) + " bps";
        }

        if (txbits >= 1000) {
            double txKb = txbits / 1000;
            Tx = String.format("%.2f", txKb) + " Kbps";
            if(txKb >= 1000) {
                double txMb = txKb / 1000;
                Tx = String.format("%.2f", txMb) + " Mbps";
                if(txMb >= 1000){
                    double txGb = txKb / 1000;
                    Tx = String.format("%.2f", txGb) + " Gbps";
                }
            }
        }
        else {
            Tx = (long)(txbits) + " bps";
        }

        //System.out.println("count: " + count);
        link_speed.setTextSize(15);
        link_speed.setText(Rx + "/" + Tx);

        String unitRx = Rx.substring(Rx.length()-4);
        double downlink = 0;
        if (unitRx.equals(" bps")){
            downlink = 0;
        }else if (unitRx.equals("Mbps")){
            downlink = Double.parseDouble(Rx.substring(0, Rx.length() - 4));
        }

        String unitTx = Tx.substring(Tx.length()-4);
        double uplink = 0;
        if (unitTx.equals(" bps")){
            uplink = 0;
        }else if (unitTx.equals("Mbps")){
            uplink = Double.parseDouble(Tx.substring(0, Tx.length() - 4));
        }
        return new String[] {String.valueOf((int) downlink), String.valueOf((int) uplink), Rx, Tx};
    }

    public void scanCompleted(boolean success) {
        //Log.e("log", "HomeFragment::scanCompleted: " + success + " scan-table-flag: " + scan_table_flag);

        if (!scan_table_flag)
            return;

        Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            public void run() {
                _scanCompleted(success);
                handler.removeCallbacks(this);
            }
        };
        handler.post(runnable);
    }

    //https://electronics.stackexchange.com/questions/83354/calculate-distance-from-rssi
    static double getDistance(double rssi, int freq_mhz) {
       //https://www.pasternack.com/t-calculator-fspl.aspx
       // Values below assume a 20db txpower
       double A;
       if (freq_mhz < 2500)
          A = -20;
       else if (freq_mhz < 6000)
          A = -27;
       else
          A = -29;

       //double n = 2; // free space path loss
       double n = 2.5; // Gives better results for my tests.

       //RSSI (dBm) = -10n log10(d) + A
       // RSSI - A = -10n log10(d)
       // (RSSI - A) / -10n = log10(d)
       // 10 ^ ((RSSI - A) / -10n) = d
       return Math.pow(10d, (rssi - A) / (-10 * n));
    }

    public void _scanCompleted(boolean success) {
        scan_table.removeAllViews();
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String data = "";
        String conneted_bssid =  wifiManager.getConnectionInfo().getBSSID();

        Map<String, String> scan_data = new LinkedHashMap<String, String>();
        List<ScanResult> scan_result = wifiManager.getScanResults();
        for (int i = 0; i < scan_result.size(); i++) {
            ScanResult sr = scan_result.get(i);
            //Log.e("log", "scan-result[" + i + "]: " + sr.toString() + "\n");

            // The IEs do not implement toString() in useful manner, so we would have to parse
            // the binary info if we cared to report this.  Ignore for now.
            //if (sr.getInformationElements() != null) {
            //   for (ScanResult.InformationElement ie : sr.getInformationElements()) {
            //      Log.e("log", " IE: " + ie.toString() + "\n");
            //   }
            //}

            String ssid = sr.SSID; //Get the SSID
            if(ssid.equals(null) || ssid.equals("")){
                ssid = "*hidden*";
                System.out.println("SSID::= "+ ssid);
            }

            String bssid =  sr.BSSID; //Get the BSSID
            String capability = sr.capabilities; //Get Wi-Fi capabilities
            int centerFreq0 = 0;
            int centerFreq1 = 0;
            int channelWidth = 0;
            if (Build.VERSION.SDK_INT >= 23) {
                centerFreq0 = sr.centerFreq0; //Get centerFreq0
                centerFreq1 = sr.centerFreq1; //Get centerFreq1
                channelWidth = sr.channelWidth; //Get channelWidth
            }
            int level = sr.level; //Get level/rssi
            int frequency = sr.frequency; //Get frequency
            if(conneted_bssid.equals(bssid)){
                ssid += "(connected)";
            }
            // timestamp is usec since boot.
            //java.lang.System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime();
            long age = android.os.SystemClock.elapsedRealtime() - (sr.timestamp / 1000);
            age = age / 1000; //convert to seconds.

            //float dist = (float) Math.pow(10.0d, (27.55d - 40d * Math.log10(frequency) + 6.7d - level) / 20.0d) * 1000;
            double dist = getDistance(level, frequency);
            String dist_in_meters = String.format("%.02f", dist);

            data = "SSID: " + '\"' + ssid + '\"' + "\nbssid: " + bssid + "\ncenterFreq0: " +
                    centerFreq0 + "\tcenterFreq1: " + centerFreq1 + "\nchannelWidth: " + channelWidth +
                    "\t\uD83D\uDCF6 " + level + "\nFrequency " + frequency + "\tage⏱ " + age +
                    "\t\t\tdistance: " + dist_in_meters + "m\n" + "\uD83D\uDD12 " + capability;
            scan_data.put(String.valueOf(i+1), String.valueOf(data));
        }

        scan_table.setPadding(10, 0, 10, 0);
        TableRow heading = new TableRow(getActivity());
        heading.setBackgroundColor(Color.rgb(120, 156, 175));

        TextView val_head = new TextView(getActivity());
        val_head.setText("LIVE WI-FI SCAN");
        val_head.setTextColor(Color.BLACK);
        val_head.setGravity(Gravity.LEFT);
        heading.addView(val_head);
        scan_table.addView(heading);

        int i = 1;
        for (Map.Entry<String, String> entry : scan_data.entrySet()) {
            TableRow tbrow = new TableRow(getActivity());
            if (i % 2 == 0) {
                tbrow.setBackgroundColor(Color.rgb(220, 220, 220));
            } else {
                tbrow.setBackgroundColor(Color.rgb(192, 192, 192));
            }

            TextView val_view = new TextView(getActivity());
            String scan_value = entry.getValue();
            if (scan_value.contains("(connected)")){
                tbrow.setBackgroundColor(Color.rgb(100, 192, 102));
            }
            val_view.setText(scan_value);
            val_view.setTextSize(15);
            val_view.setTextColor(Color.BLACK);
            val_view.setGravity(Gravity.LEFT);
            tbrow.addView(val_view);
            scan_table.addView(tbrow);
            i = i + 1;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        instance = null;
    }
}

//    double dist_in_meters = Math.pow(10.0d, (27.55d - 40d * Math.log10(frequency) + 6.7d - rssi) / 20.0d) * 1000;
//                        System.out.println("dist_in_meters: " + dist_in_meters);
