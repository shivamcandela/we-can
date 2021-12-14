package com.candela.wecan.tests.base_tools;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ConfigureWifi {
    String ssid;
    String password;
    String encryption;
    WifiManager wifiManager;
    Context context;
    BroadcastReceiver broadcastReceiver;
    IntentFilter intentFilter;
    public static int CC_Status = 0;
    public static ArrayList<String> cc_data;

    public ConfigureWifi(Context context, WifiManager wifiManager, String ssid, String password, String encryption) {
        this.ssid = ssid;
        this.password = password;
        this.encryption = encryption;
        this.wifiManager = wifiManager;
        this.context = context;
        this.intentFilter = new IntentFilter();
        this.cc_data = new ArrayList<>();
        this.CC_Status = 0;
        this.callback();
        this.connect();
    }
    public void callback(){
        ArrayList cc_data = this.cc_data;
        WifiManager wifi = this.wifiManager;
        String wifi_name = this.ssid;
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                WifiInfo wifiInfo = wifi.getConnectionInfo();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Log.i("device status", timestamp.toString() + " " + wifiInfo.toString());
                cc_data.add(timestamp.toString() + " " + wifiInfo.toString());
                if (wifiInfo.getSupplicantState().toString() == "COMPLETED" && wifi_name == wifiInfo.getSSID()){
                    CC_Status = 1;
                    context.unregisterReceiver(this);
                }

                if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) {

//                    Log.i("device status",  + SupplicantState.ASSOCIATED.toString());
                    if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) {
                        //do stuff
                    } else {
                        // wifi connection was lost
                    }
                }
            }
        };
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        context.registerReceiver(broadcastReceiver, intentFilter);
    }
    private void connect() {
        wifiManager.setWifiEnabled(false);
        Log.e("ssid", this.ssid);
        Log.e("pass", this.password);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration conf : configuredNetworks){

            wifiManager.disableNetwork(conf.networkId);
            wifiManager.removeNetwork(conf.networkId);

        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        wifiConfiguration.SSID = String.format("\"%s\"", this.ssid);
        wifiConfiguration.preSharedKey = String.format("\"%s\"", this.password);
        Log.e("log", wifiConfiguration.toString());
        int wifiID = wifiManager.addNetwork(wifiConfiguration);
        if (this.encryption.equals("open")){
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (this.encryption.equals("psk") || this.encryption.equals("psk2")) {
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

        }
//        wifiManager.setWifiEnabled(true);
//        wifiConfiguration.
        wifiConfiguration.priority = 40;
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            System.out.println(i.SSID);
            if(i.SSID != null && i.SSID.equals("\"" + this.ssid + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
//        wifiID = wifiManager.addNetwork(wifiConfiguration);
//        wifiManager.disconnect();
//        wifiManager.enableNetwork(wifiID, true);
//        wifiManager.reconnect();

    }


}
