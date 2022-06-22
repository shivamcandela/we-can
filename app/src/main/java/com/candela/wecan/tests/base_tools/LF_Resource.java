package com.candela.wecan.tests.base_tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.util.Vector;

import candela.lfresource.AndroidUI;
import candela.lfresource.LANforgeMgr;
import candela.lfresource.PlatformInfo;
import candela.lfresource.Port;
import candela.lfresource.StringKeyVal;
import candela.lfresource.lfresource;
import com.candela.wecan.StartupActivity;

//LF_Resource p = new LF_Resource(143);
//        p.start();
public class LF_Resource extends Thread {

    private String realm_id;
    protected StartupActivity startup_activity;

    public lfresource lfresource;
    public PlatformInfo pi;
    public String ip_address;
    public String resource;
    public Context context;
    public ResourceUtils ru;
    public boolean do_run = true;

    @SuppressLint("NewApi")
    public LF_Resource(StartupActivity act, String ip_address, String resource, String realm_id, String username,
                       Context context) {
        startup_activity = act;
        this.context = context;
        this.lfresource = new lfresource();
        this.ip_address = ip_address;
        this.resource = resource;
        this.realm_id = realm_id;
        this.pi = new PlatformInfo();

        this.pi.manufacturer = "samsung";
        this.pi.model = "a11";
        this.pi.wifi_capabilities = new Vector<>();
//        this.pi.dhcp_info = new Vector<>();
        this.pi.wecan_user_name = username;

        this.ru = new ResourceUtils(startup_activity, this.context);
        this.pi = ru.requestPlatformUpdate();

        LANforgeMgr.setUI(ru);
        LANforgeMgr.setPlatformInfo(this.pi);
    }

    public void updateConfig(String ip, String resource_id, String _realm_id) {
        ip_address = ip;
        resource = resource_id;
        realm_id = _realm_id;
    }

    public String getRemoteHost() {
        return LANforgeMgr.getProto().getRemoteHost();
    }

    public String getResource(){
        return String.valueOf(LANforgeMgr.getResourceId());

    }

    public String getRealm(){
        return String.valueOf(LANforgeMgr.getRealmId());

    }
    public boolean getConnState(){
        return LANforgeMgr.isConnected();
    }

    public void run() {
        String[] args = new String[8];
        args[0] = "-s";
        args[1] = this.ip_address; //.put("-s", "192.168.100.222");
        args[2] = "--resource"; //.put("-s", "192.168.100.222");
        args[3] = this.resource;
        args[4] = "--realm"; //.put("-s", "192.168.100.222");
        args[5] = this.realm_id;
        args[6] = "--mgt_dev"; //.put("-s", "192.168.100.222");
        args[7] = "wlan0";
        this.lfresource.init(false, args);

//
        //while (do_run) {
        //    // Poll things
        //}
    }

}
