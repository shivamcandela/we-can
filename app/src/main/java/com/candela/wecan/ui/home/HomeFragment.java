package com.candela.wecan.ui.home;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.TrafficStats;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.candela.wecan.R;
import com.candela.wecan.dashboard.HomeTableManager;
import com.candela.wecan.dashboard.LinkSpeedThread;
import com.candela.wecan.dashboard.LiveData;
import com.candela.wecan.dashboard.RealTimeChart;
import com.candela.wecan.dashboard.SaveData;
import com.candela.wecan.dashboard.Speedometer;
import com.candela.wecan.dashboard.WebBrowser;
import com.candela.wecan.databinding.FragmentHomeBinding;
import com.cardiomood.android.controls.gauge.SpeedometerGauge;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.jjoe64.graphview.GraphView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import candela.lfresource.LANforgeMgr;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
//    private static final String FILE_NAME = "data.conf";
    public static TextView link_speed;
    public static Boolean live_table_flag = false, scan_table_flag = false, flag = false;
    public static HomeFragment instance = null;
//    public String[] up_down_global;
    public static TableLayout sys_table = null;
    public static TableLayout live_table = null;
    public static TableLayout scan_table = null;
    public static GraphView graph = null;
    public static long last_bps_time = 0;
    public static long last_rx_bytes = 0;
    public static long last_tx_bytes = 0;
    String username = "";
    public static Runnable runnable_live;
    public static Handler handler_link;
    public static Runnable runnable_link;
    public static Runnable runnable_speedometer;
    public static Handler handler_speedometer_thread;
//    public static Runnable runnable_webpage_test;
//    public static Handler handler_webpage_test;

    public static Activity home_fragment_activity;
    public static Handler handler_live_data;
    public static LinearLayout speedometer_linear,up_down;
    public static Button scan_btn, system_info_btn, live_btn, speedometer_btn,chart_btn;
    private ImageView share_btn;
    private Switch switch_btn;
    public static View hView;
    TextView nav_server;
    TextView nav_resource_realm;
    private NavigationView navigationView;
    private onFragmentBtnSelected listener;
    public static SpeedometerGauge speedometerup;
    public static SpeedometerGauge speedometerdown;
    public static Handler handler_save_data;
    public static Runnable runnable_save_data;
    public static LinearLayout legend;
    public static Handler handler_graph;
    public static RealTimeChart runnable_graph;



    private HomeTableManager homeTableManager;
    public static String[] up_down_data;
    public static ActivityManager actvityManager;
    public static FloatingActionButton AddFab, computerFab, shareFab, wifiFab;
    public static Boolean isAllFabsVisible;
    public static Button webpage_test_btn;
    public static WebView webpage_view;
    public static PackageManager pkgmgr;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        handler_live_data = new Handler();
        handler_link = new Handler();
        handler_speedometer_thread = new Handler();
        handler_graph = new Handler();
//        handler_webpage_test = new Handler();
        handler_save_data = new Handler();
        actvityManager = (ActivityManager) getActivity().getApplicationContext().getSystemService( Context.ACTIVITY_SERVICE );
        pkgmgr = getActivity().getApplicationContext().getPackageManager();

        runnable_save_data = new SaveData();
        runnable_live = new LiveData();
        runnable_speedometer =  new Speedometer();
        runnable_link = new LinkSpeedThread();
        runnable_graph = new RealTimeChart();
//        runnable_webpage_test = new WebBrowser();

        home_fragment_activity = this.getActivity();
        navigationView = view.findViewById(R.id.nav_view);
//        scan_btn = view.findViewById(R.id.scan_data);
        system_info_btn = view.findViewById(R.id.system_info_btn);
        live_btn = view.findViewById(R.id.rxtx_btn);
        speedometer_btn = view.findViewById(R.id.speedometer);
        share_btn = view.findViewById(R.id.share_btn);
        speedometer_linear = view.findViewById(R.id.speedometer_linear);
        up_down = view.findViewById(R.id.up_down);
        switch_btn = view.findViewById(R.id.save_data_switch);
        link_speed = view.findViewById(R.id.link_speed);
        sys_table = view.findViewById(R.id.table);
        live_table = view.findViewById(R.id.table);
        scan_table = view.findViewById(R.id.table);
        speedometerup = view.findViewById(R.id.speedometerup);
        speedometerdown = view.findViewById(R.id.speedometerdown);
        chart_btn = view.findViewById(R.id.chart_btn);
        graph = (GraphView) view.findViewById(R.id.graph);
        legend = view.findViewById(R.id.legend);
        webpage_test_btn = view.findViewById(R.id.webpage_test_btn);
        webpage_view = view.findViewById(R.id.webpage_view);

        last_bps_time = System.currentTimeMillis();
        last_tx_bytes = TrafficStats.getTotalTxBytes();
        last_rx_bytes = TrafficStats.getTotalRxBytes();
        speedometer_linear.setVisibility(View.GONE);
        up_down.setVisibility(View.GONE);
        graph.setVisibility(View.GONE);
        legend.setVisibility(View.GONE);
        homeTableManager = new HomeTableManager();
        share_btn.setOnClickListener(homeTableManager);
        switch_btn.setOnClickListener(homeTableManager);

        live_btn.setOnClickListener(homeTableManager);

//        scan_btn.setOnClickListener(homeTableManager);
        switch_btn.setOnCheckedChangeListener(homeTableManager);
        chart_btn.setOnClickListener(homeTableManager);
        webpage_test_btn.setOnClickListener(homeTableManager);

        system_info_btn.setEnabled(false);
        speedometer_btn.setEnabled(false);
//        AddFab = (FloatingActionButton) view.findViewById(R.id.add_fab);
//        computerFab = (FloatingActionButton) view.findViewById(R.id.computer_fab);
//        shareFab = (FloatingActionButton) view.findViewById(R.id.share_fab);
//        wifiFab = (FloatingActionButton) view.findViewById(R.id.wifi_fab);
//
//        FabButton fbtn = new FabButton();
//        AddFab.setOnClickListener(fbtn);
//        computerFab.setVisibility(View.GONE);
//        shareFab.setVisibility(View.GONE);
//        wifiFab.setVisibility(View.GONE);
//        AddFab.bringToFront();
//        isAllFabsVisible = false;
//        Float translationY = 100f;
//        OvershootInterpolator interpolator = new OvershootInterpolator();
//        AddFab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isAllFabsVisible) {
//
//                    AddFab.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();
//
//                    computerFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
//                    shareFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
//                    wifiFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
//
//                    computerFab.setVisibility(View.VISIBLE);
//                    shareFab.setVisibility(View.VISIBLE);
//                    wifiFab.setVisibility(View.VISIBLE);
//
//
//                    isAllFabsVisible = true;
//                } else {
//
//                    AddFab.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();
//
//                    computerFab.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
//                    shareFab.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
//                    wifiFab.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
//
//                    computerFab.setVisibility(View.GONE);
//                    shareFab.setVisibility(View.GONE);
//                    wifiFab.setVisibility(View.GONE);
//
//                    isAllFabsVisible = false;
//                }
//            }
//        });
//
        handler_link.post(runnable_link);
        live_btn.performClick();
        Handler handler_ready_state = new Handler();
        Runnable runnable_ready_state = new Runnable() {
            @Override
            public void run() {
                speedometer_btn.setEnabled(true);
                system_info_btn.setEnabled(true);
                speedometer_btn.setOnClickListener(homeTableManager);
                system_info_btn.setOnClickListener(homeTableManager);
                Toast.makeText(getContext(), "Connected to LANforge Server", Toast.LENGTH_LONG).show();
            }
        };
        handler_ready_state.postDelayed(runnable_ready_state, 1000);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof onFragmentBtnSelected){
            listener = (onFragmentBtnSelected) context;
        }
        else {
            throw new ClassCastException(context.toString() + "must Implement Listener");
        }
        super.onAttach(context);
    }

    public interface onFragmentBtnSelected{
        public void onButtonSelected();
    }

//    public void scanCompleted(boolean success) {
//        //Log.e("log", "HomeFragment::scanCompleted: " + success + " scan-table-flag: " + scan_table_flag);
//
//        if (!scan_table_flag)
//            return;
//
//        Handler handler = new Handler();
//        final Runnable runnable = new Runnable() {
//            public void run() {
//                _scanCompleted(success);
//                handler.removeCallbacks(this);
//            }
//        };
//        handler.post(runnable);
//    }

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

//    public void _scanCompleted(boolean success) {
//        scan_table.removeAllViews();
//        WifiManager wifiManager = (WifiManager) view.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        String data = "";
//        String conneted_bssid = wifiManager.getConnectionInfo().getBSSID();
//
//        Map<String, String> scan_data = new LinkedHashMap<String, String>();
//        List<ScanResult> scan_result = wifiManager.getScanResults();
//        for (int i = 0; i < scan_result.size(); i++) {
//            ScanResult sr = scan_result.get(i);
//            //Log.e("log", "scan-result[" + i + "]: " + sr.toString() + "\n");
//
//            // The IEs do not implement toString() in useful manner, so we would have to parse
//            // the binary info if we cared to report this.  Ignore for now.
//            //if (sr.getInformationElements() != null) {
//            //   for (ScanResult.InformationElement ie : sr.getInformationElements()) {
//            //      Log.e("log", " IE: " + ie.toString() + "\n");
//            //   }
//            //}
//
//            String ssid = sr.SSID; //Get the SSID
//            if (ssid.equals(null) || ssid.equals("")) {
//                ssid = "*hidden*";
//                System.out.println("SSID::= " + ssid);
//            }
//
//            String bssid = sr.BSSID; //Get the BSSID
//            String capability = sr.capabilities; //Get Wi-Fi capabilities
//            int centerFreq0 = 0;
//            int centerFreq1 = 0;
//            int channelWidth = 0;
//            if (Build.VERSION.SDK_INT >= 23) {
//                centerFreq0 = sr.centerFreq0; //Get centerFreq0
//                centerFreq1 = sr.centerFreq1; //Get centerFreq1
//                channelWidth = sr.channelWidth; //Get channelWidth
//            }
//            int level = sr.level; //Get level/rssi
//            int frequency = sr.frequency; //Get frequency
//            if (conneted_bssid.equals(bssid)) {
//                ssid += "(connected)";
//            }
//            // timestamp is usec since boot.
//            //java.lang.System.currentTimeMillis() - android.os.SystemClock.elapsedRealtime();
//            long age = android.os.SystemClock.elapsedRealtime() - (sr.timestamp / 1000);
//            age = age / 1000; //convert to seconds.
//
//            //float dist = (float) Math.pow(10.0d, (27.55d - 40d * Math.log10(frequency) + 6.7d - level) / 20.0d) * 1000;
//            double dist = getDistance(level, frequency);
//            String dist_in_meters = String.format("%.02f", dist);
//
//            data = "SSID: " + '\"' + ssid + '\"' + "\nbssid: " + bssid + "\ncenterFreq0: " +
//                    centerFreq0 + "\tcenterFreq1: " + centerFreq1 + "\nchannelWidth: " + channelWidth +
//                    "\t\uD83D\uDCF6 " + level + "\nFrequency " + frequency + "\tage⏱ " + age +
//                    "\t\t\tdistance: " + dist_in_meters + "m\n" + "\uD83D\uDD12 " + capability;
//            scan_data.put(String.valueOf(i + 1), data);
//        }
//
//        scan_table.setPadding(10, 0, 10, 0);
//        TableRow heading = new TableRow(view);
//        heading.setBackgroundColor(Color.rgb(120, 156, 175));
//
//        TextView val_head = new TextView(view);
//        val_head.setText("LIVE WI-FI SCAN");
//        val_head.setTextColor(Color.BLACK);
//        val_head.setGravity(Gravity.LEFT);
//        heading.addView(val_head);
//        scan_table.addView(heading);
//
//        int i = 1;
//        for (Map.Entry<String, String> entry : scan_data.entrySet()) {
//            TableRow tbrow = new TableRow(view);
//            if (i % 2 == 0) {
//                tbrow.setBackgroundColor(Color.rgb(220, 220, 220));
//            } else {
//                tbrow.setBackgroundColor(Color.rgb(192, 192, 192));
//            }
//
//            TextView val_view = new TextView(view);
//            String scan_value = entry.getValue();
//            if (scan_value.contains("(connected)")) {
//                tbrow.setBackgroundColor(Color.rgb(100, 192, 102));
//            }
//            val_view.setText(scan_value);
//            val_view.setTextSize(15);
//            val_view.setTextColor(Color.BLACK);
//            val_view.setGravity(Gravity.LEFT);
//            tbrow.addView(val_view);
//            scan_table.addView(tbrow);
//            i = i + 1;
//        }
//    }

    @Override
    public void setRetainInstance(boolean retain) {
        super.setRetainInstance(retain);
    }

    @Override
    public void setInitialSavedState(@Nullable SavedState state) {
        super.setInitialSavedState(state);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPrimaryNavigationFragmentChanged(boolean isPrimaryNavigationFragment) {
        super.onPrimaryNavigationFragmentChanged(isPrimaryNavigationFragment);
    }

    public static String[] updateBpsDisplay() {

        long now = System.currentTimeMillis();
        double TimeDifference = now - HomeFragment.last_bps_time;
        if (TimeDifference == 0) {
            return new String[]{"0", "0", "0", "0"}; // no div by zero error!
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

        double rxDiff = rx_bytes - HomeFragment.last_rx_bytes;
        double txDiff = tx_bytes - HomeFragment.last_tx_bytes;
        double txbits = ((txDiff) * 1000 / TimeDifference) * 8;
        double rxbits = ((rxDiff) * 1000 / TimeDifference) * 8;

        HomeFragment.last_rx_bytes = rx_bytes;
        HomeFragment.last_tx_bytes = tx_bytes;
        HomeFragment.last_bps_time = now;

        // TODO:  More efficient to test for high numbers first and only assing Rx/Tx string once
        if (rxbits >= 1000) {
            double rxKb = rxbits / 1000;
            Rx = String.format("%.2f", rxKb) + " Kbps";
            if (rxKb >= 1000) {
                double rxMb = rxKb / 1000;
                Rx = String.format("%.2f", rxMb) + " Mbps";
                if (rxMb >= 1000) {
                    double rxGb = rxMb / 1000;
                    Rx = String.format("%.2f", rxGb) + " Gbps";
                }
            }
        } else {
            Rx = (long) (rxbits) + " bps";
        }

        if (txbits >= 1000) {
            double txKb = txbits / 1000;
            Tx = String.format("%.2f", txKb) + " Kbps";
            if (txKb >= 1000) {
                double txMb = txKb / 1000;
                Tx = String.format("%.2f", txMb) + " Mbps";
                if (txMb >= 1000) {
                    double txGb = txKb / 1000;
                    Tx = String.format("%.2f", txGb) + " Gbps";
                }
            }
        } else {
            Tx = (long) (txbits) + " bps";
        }


        //System.out.println("count: " + count);
        HomeFragment.link_speed.setTextSize(15);
        HomeFragment.link_speed.setText(Rx + "/" + Tx);

        String unitRx = Rx.substring(Rx.length() - 4);
        double downlink = 0;
        if (unitRx.equals(" bps")) {
            downlink = 0;
        } else if (unitRx.equals("Mbps")) {
            downlink = Double.parseDouble(Rx.substring(0, Rx.length() - 4));
        }
        else if (unitRx.equals("Kbps")){
            downlink = Double.parseDouble(Rx.substring(0, Rx.length() - 6));
            downlink = downlink *0.001;
        }

        String unitTx = Tx.substring(Tx.length() - 4);
        double uplink = 0;
        if (unitTx.equals(" bps")) {
            uplink = 0;
        } else if (unitTx.equals("Mbps")) {
            uplink = Double.parseDouble(Tx.substring(0, Tx.length() - 4));
        }
        else if (unitRx.equals("Kbps")){
            uplink = Double.parseDouble(Rx.substring(0, Rx.length() - 6));
            uplink = uplink *0.001;
        }
        return new String[]{String.valueOf((double) downlink), String.valueOf((double) uplink), Rx, Tx};
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

    public void _scanCompleted(boolean success) {
        scan_table.removeAllViews();
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String data = "";
        String conneted_bssid = wifiManager.getConnectionInfo().getBSSID();

        Map<String, String> scan_data = new LinkedHashMap<String, String>();
        List<ScanResult> scan_result = wifiManager.getScanResults();
        for (int i = 0; i < scan_result.size(); i++) {
            ScanResult sr = scan_result.get(i);
            Log.e("log", "scan-result[" + i + "]: " + sr.toString() + "\n");

            // The IEs do not implement toString() in useful manner, so we would have to parse
            // the binary info if we cared to report this.  Ignore for now.
            //if (sr.getInformationElements() != null) {
            //   for (ScanResult.InformationElement ie : sr.getInformationElements()) {
            //      Log.e("log", " IE: " + ie.toString() + "\n");
            //   }
            //}

            String ssid = sr.SSID; //Get the SSID
            if (ssid.equals(null) || ssid.equals("")) {
                ssid = "*hidden*";
                System.out.println("SSID::= " + ssid);
            }

            String bssid = sr.BSSID; //Get the BSSID
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
            if (conneted_bssid.equals(bssid)) {
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
            scan_data.put(String.valueOf(i + 1), String.valueOf(data));
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
            if (scan_value.contains("(connected)")) {
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

}
