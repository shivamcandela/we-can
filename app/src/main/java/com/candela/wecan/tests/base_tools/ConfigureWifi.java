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
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.candela.wecan.StartupActivity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
        Log.d(StartupActivity.TAG, "Configure Wifi Credentials: ssid: " + ssid + "\tpassword: " + password + "\tencryption: " + encryption);
        Toast.makeText(context, "Configuring Wifi", Toast.LENGTH_LONG).show();
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
                Log.i(StartupActivity.TAG, timestamp.toString() + " " + wifiInfo.toString());
                cc_data.add(timestamp.toString() + "-:-" + wifiInfo.toString());
                if (wifiInfo.getSupplicantState().toString().equals("DISCONNECTED") && ("\"" + wifi_name + "\"").equals(wifiInfo.getSSID())){

                }
                if (wifiInfo.getSupplicantState().toString().equals("COMPLETED") && ("\"" + wifi_name + "\"").equals(wifiInfo.getSSID())){
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
    public boolean isConnectedto(String ssid){
        if (wifiManager.getConnectionInfo().getSSID().toString().equals(ssid)){
            return true;
        }
        return false;
    }
    private void connect() {

        wifiManager.setWifiEnabled(false);
        wifiManager.setWifiEnabled(true);
        if (!wifiManager.isWifiEnabled()) {
            Log.d(StartupActivity.TAG, "Enabling Wifi!");
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo connectionInfo = wifiManager.getConnectionInfo();
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(StartupActivity.TAG, "Enable Permission Please!");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        List<WifiConfiguration> list1 = wifiManager.getConfiguredNetworks();
        if (list1 == null) {
           Log.d(StartupActivity.TAG, "Existing Wifi Configurations is null in connect.");
        }
        else {
           Log.d(StartupActivity.TAG, "Searching for Existing Wifi Configurations..." + list1.toString());
           int j = 10;
           for( WifiConfiguration i : list1 ) {
              j = j++;
              i.priority = j++;
              wifiManager.updateNetwork(i);
              wifiManager.saveConfiguration();
              if ((i.SSID != null && i.SSID.equals("\"" + this.ssid + "\""))) {
                 Log.d(StartupActivity.TAG, "SSID Found in saved Networks: " + i.SSID);
                 i.priority = 9999;
                 wifiManager.updateNetwork(i);
                 wifiManager.saveConfiguration();
                 wifiManager.disconnect();
                 this.callback();
                 wifiManager.setWifiEnabled(true);
                 wifiManager.enableNetwork(i.networkId, true);
                 wifiManager.reconnect();
                 return;
              }
           }
        }

        Log.d(StartupActivity.TAG, "SSID was not available in Existing Configuration! Making a new entry!");
        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        wifiConfiguration.SSID = String.format("\"%s\"", this.ssid);

        Log.i(StartupActivity.TAG, "ssid -:" + ssid + ":- password -:" + password + ":-");
        Log.i(StartupActivity.TAG, "wifiConfiguration: " + wifiConfiguration.toString());

        StringTokenizer st = new StringTokenizer(encryption, "|");
        while (st.hasMoreTokens()) {
           String tok = st.nextToken();
           Log.d(StartupActivity.TAG, "crypt token: " + tok);
           if (tok.equals("open")) {
              wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
           }
           else if (tok.equals("psk")) {
              wifiConfiguration.preSharedKey = String.format("\"%s\"", this.password);
              wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
              wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
              wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
              wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); // TODO:  Not sure this is correct.
           }
           else if (tok.equals("psk2")) {
              wifiConfiguration.preSharedKey = String.format("\"%s\"", this.password);
              wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
              wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
              wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
              wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
           }
           else if (tok.equals("owe")) {
              wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.OWE);
              wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
              // TODO:  Verify
           }
           else if (tok.equals("ieee8021x")) {
              wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
              wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
              wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
              // TODO: More would need to be set I guess.
           }
           else if (tok.equals("sae")) {
              wifiConfiguration.preSharedKey = String.format("\"%s\"", this.password);
              wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
              wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.SAE);
              wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.SUITE_B_192);
              wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.GCMP_128);
              wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.GCMP_256);
           }
           else if (tok.equals("wep")) {
              //https://developer.android.com/reference/android/net/wifi/WifiConfiguration.KeyMgmt
              //https://www.programcreek.com/java-api-examples/?api=android.net.wifi.WifiConfiguration
              wifiConfiguration.wepKeys[0] = String.format("\"%s\"", this.password);
              wifiConfiguration.wepTxKeyIndex = 0;
              wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE); // TODO:  Verify this config
              wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
              wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
           }
        }// for all security types

        // https://stackoverflow.com/questions/17123243/setting-priority-for-wifi-configuration-in-android
        // priority needs to be highest in the system in order for it to be selected.
        // TODO:  Maybe record current connection's priority and ID, set it to some lower value if
        // currently at 99999, and then when we stop this App we could reset to old settings?
        wifiConfiguration.priority = 99999;

        int wifiID = wifiManager.addNetwork(wifiConfiguration);

        Log.i(StartupActivity.TAG, "wifiID: " + wifiID);


        wifiManager.disconnect();
        wifiManager.enableNetwork(wifiID, true);
        wifiManager.reconnect();

        wifiManager.setWifiEnabled(true);

//        wifiConfiguration.
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if (list != null) {
           for( WifiConfiguration i : list ) {
              System.out.println(i.SSID);
              if ((i.SSID != null && i.SSID.equals("\"" + this.ssid + "\""))) {
                 Log.d(StartupActivity.TAG, "Configuring the Target SSID, Sit back and Relax!");
                 wifiManager.disconnect();
                 // this.callback();
                 wifiManager.setWifiEnabled(true);
                 wifiManager.enableNetwork(i.networkId, true);
                 wifiManager.reconnect();
                 break;
              }
           }
        }

//        wifiID = wifiManager.addNetwork(wifiConfiguration);
//        wifiManager.disconnect();
//        wifiManager.enableNetwork(wifiID, true);
//        wifiManager.reconnect();

    }
}
