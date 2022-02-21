package com.candela.wecan.dashboard;

import android.content.Context;
import android.graphics.Color;
import android.net.DhcpInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.candela.wecan.tools.GetNetworkCapabilities;
import com.candela.wecan.ui.home.HomeFragment;

import java.util.LinkedHashMap;
import java.util.Map;

public class LiveData implements Runnable{
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {
        HomeFragment.handler_speedometer_thread.removeCallbacks(HomeFragment.runnable_speedometer);
        if (HomeFragment.live_table_flag == false){
            HomeFragment.handler_live_data.removeCallbacks(HomeFragment.runnable_live);
        }
        HomeFragment.live_table.removeAllViews();
        HomeFragment.system_info_btn.setTextColor(Color.WHITE);
        HomeFragment.live_btn.setTextColor(Color.GREEN);
        HomeFragment.scan_btn.setTextColor(Color.WHITE);
        HomeFragment.speedometer_btn.setTextColor(Color.WHITE);
        GetNetworkCapabilities networkSniffTask = new GetNetworkCapabilities(HomeFragment.home_fragment_activity.getApplicationContext());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(HomeFragment.home_fragment_activity);
        if (HomeFragment.home_fragment_activity != null && HomeFragment.live_table_flag != false) {
            HomeFragment.live_btn.setTextColor(Color.GREEN);
            WifiManager wifiManager = (WifiManager) HomeFragment.home_fragment_activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
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

            int leaseDuration = Dhcp_details.leaseDuration;

            long availMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long totalMem = Runtime.getRuntime().totalMemory();
            long usedMem = totalMem - availMem;
            String cpu_used_percent = String.format("%.2f", (usedMem / (double) totalMem) * 100);
            Map<String, String> live_data = new LinkedHashMap<String, String>();

            live_data.put("IP", String.valueOf(IP));
            live_data.put("SSID", SSID);
            live_data.put("BSSID", BSSID);
            live_data.put("Signal", Rssi + " dBm");
            live_data.put("LinkSpeed", LinkSpeed);
            live_data.put("Channel", channel);
            live_data.put("CPU util", cpu_used_percent + " %");
            live_data.put("DNS1", dns1);
            live_data.put("DNS2", dns2);
            live_data.put("DHCP Server", serverAddress);
            live_data.put("Gateway", gateway);
            live_data.put("LeaseDuration", leaseDuration + " Sec");
            if (networkSniffTask.isWifiNetworkCongested()){
                live_data.put("WIFI Congested","NO");
            }
            if (!networkSniffTask.isWifiNetworkCongested()){
                live_data.put("WIFI Congested","YES");
            }
            if(networkSniffTask.isCellularNetworkCongested()) {
                live_data.put("Cellular Congested","NO");

            }
            if(!networkSniffTask.isCellularNetworkCongested()) {
                live_data.put("Cellular Congested","YES");

            }

            HomeFragment.live_table.setPadding(10, 0, 10, 0);
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
            if (HomeFragment.live_table_flag) {
                HomeFragment.live_btn.setTextColor(Color.GREEN);
                HomeFragment.live_table.addView(heading);
            }

            int i = 1;
            for (Map.Entry<String, String> entry : live_data.entrySet()) {
                TableRow tbrow = new TableRow(HomeFragment.home_fragment_activity);
                if (i % 2 == 0) {
                    tbrow.setBackgroundColor(Color.rgb(211, 211, 211));
                } else {
                    tbrow.setBackgroundColor(Color.rgb(192, 192, 192));
                }

                TextView sl_view = new TextView(HomeFragment.home_fragment_activity);
                sl_view.setText(i + ".");
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
                if (HomeFragment.live_table_flag) {
                    HomeFragment.live_btn.setTextColor(Color.GREEN);
                    HomeFragment.live_table.addView(tbrow);
                }
                i = i + 1;
            }
            if (HomeFragment.live_table_flag) {
                HomeFragment.handler_live_data.postDelayed(HomeFragment.runnable_live, 1000);
            } else {
                HomeFragment.handler_live_data.removeCallbacks(HomeFragment.runnable_live);
            }
        }
    }
}
