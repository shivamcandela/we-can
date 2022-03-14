package com.candela.wecan.tests.base_tools;

import android.annotation.SuppressLint;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class GetPhoneWifiInfo {
    public static final String DeviceData = "{\"mac\":\"\", \"os\": \"android\", \"model\" : \"\", \"manufacturer\" : \"\"}";
    public GetPhoneWifiInfo() {
//        String s = this.GetDeviceInfo();
    }

    public static String GetDeviceInfo() {
        String device_model = Build.MANUFACTURER;
//        String device_model = Build.;
        return device_model;
    }

    @SuppressLint("MissingPermission")
    public static JSONObject GetWifiData(WifiManager wifi) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(DeviceData);
            jsonObject.put("mac", getMacAddr());
            jsonObject.put("manufacturer", Build.MANUFACTURER);
            jsonObject.put("model", Build.DEVICE);
//            Log.i("GetWifiData", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }



        return jsonObject;
    }

    public static String getMacAddr() {
        try {

            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface nif : all) {

                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

}
