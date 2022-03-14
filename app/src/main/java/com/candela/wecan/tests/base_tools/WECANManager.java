package com.candela.wecan.tests.base_tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class WECANManager {

    public static String TEST_NETWORK_SSID;
    public static String TEST_NETWORK_PASSKEY;
    public static String TEST_NETWORK_ENCRYPTION;

    public WECANManager(){

    }

    public static void getTestNetwork(SharedPreferences sharedpreferences){
        Map<String,?> keys = sharedpreferences.getAll();
        TEST_NETWORK_SSID = (String) keys.get("ssid");
        TEST_NETWORK_PASSKEY = (String) keys.get("passkey");
        TEST_NETWORK_ENCRYPTION = (String) keys.get("encryption");
    }


    public static void setTestNetwork(SharedPreferences sharedpreferences, String ssid, String passkey, String encryption){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("ssid", ssid);
        editor.putString("passkey", passkey);
        editor.putString("encryption", encryption);
        editor.commit();
        editor.apply();
    }

}
