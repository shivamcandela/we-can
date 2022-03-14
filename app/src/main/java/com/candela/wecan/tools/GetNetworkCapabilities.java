package com.candela.wecan.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class GetNetworkCapabilities {
    private ConnectivityManager connectivity;
    private Network[] networks;
    Context context;

    public GetNetworkCapabilities(Context context){
        this.context = context;
        this.connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.networks = connectivity.getAllNetworks();
    }

    public String GetWifiCapabilities(){
        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivity.getNetworkCapabilities(network);
            Log.e("iron_man_spider", String.valueOf(capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_CONGESTED)));
        }
        return "";
    }

    @SuppressLint("WrongConstant")
    public boolean isWifiNetworkCongested(){
        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivity.getNetworkCapabilities(network);
            if (capabilities.toString().contains("Transports: WIFI")){
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_CONGESTED);
            }
        }
        return false;
    }

    @SuppressLint("WrongConstant")
    public boolean isCellularNetworkCongested(){
        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivity.getNetworkCapabilities(network);
            if (capabilities.toString().contains("Transports: CELLULAR")){
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_CONGESTED);
            }
        }
        return false;
    }

    /*
    TODO : Add methods for getting more capabilities
    [ Transports: CELLULAR Capabilities: IMS&NOT_METERED&TRUSTED&NOT_VPN&VALIDATED&NOT_ROAMING&FOREGROUND&NOT_CONGESTED&NOT_SUSPENDED LinkUpBandwidth>=15000Kbps LinkDnBandwidth>=30000Kbps Specifier: <TelephonyNetworkSpecifier [mSubId = 1]> AdministratorUids: [] RequestorUid: -1 RequestorPackageName: null]
[ Transports: WIFI Capabilities: NOT_METERED&INTERNET&NOT_RESTRICTED&TRUSTED&NOT_VPN&VALIDATED&NOT_ROAMING&FOREGROUND&NOT_CONGESTED&NOT_SUSPENDED LinkUpBandwidth>=49500Kbps LinkDnBandwidth>=49500Kbps SignalStrength: -42 AdministratorUids: [] RequestorUid: -1 RequestorPackageName: null]
     */
}
