package com.candela.wecan.dashboard;

import android.graphics.Color;

import com.candela.wecan.ui.home.HomeFragment;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.concurrent.atomic.AtomicInteger;

public class Speedometer implements Runnable{
    static AtomicInteger counter = new AtomicInteger(0);
    private LineGraphSeries<DataPoint> mSeries1;
    public static int prev_data_up= 0;
    public static int prev_data_down = 0;
    public static int prev_data_total = 0;
    @Override
    public void run() {
        HomeFragment.handler_live_data.removeCallbacks(HomeFragment.runnable_live);
        try {
            String up_down[] = HomeFragment.up_down_data;
            HomeTableManager.up_down_global = up_down;
            int downlink = Integer.parseInt(up_down[0]);
            int uplink = Integer.parseInt(up_down[1]);
//                  Configure upload value range colors
            HomeFragment.speedometerup.setLabelTextSize(10);
            HomeFragment.speedometerup.setMaxSpeed(100);
            HomeFragment.speedometerup.setMajorTickStep(10);
            HomeFragment.speedometerup.addColoredRange(0, 25, Color.RED);
            HomeFragment.speedometerup.addColoredRange(25, 100, Color.YELLOW);
            HomeFragment.speedometerup.addColoredRange(100, 500, Color.GREEN);
//                        Set the uplink value
            HomeFragment.speedometerup.setSpeed(uplink);

//                      Download Starts here
//                      Configure download value range colors
            HomeFragment.speedometerdown.setLabelTextSize(10);
            HomeFragment.speedometerdown.setMaxSpeed(100);
            HomeFragment.speedometerdown.setMajorTickStep(10);
            HomeFragment.speedometerdown.addColoredRange(0, 25, Color.RED);
            HomeFragment.speedometerdown.addColoredRange(25, 100, Color.YELLOW);
            HomeFragment.speedometerdown.addColoredRange(100, 500, Color.GREEN);
//                        Set the downlink value
            HomeFragment.speedometerdown.setSpeed(downlink);
            int prev_count = counter.intValue() - 1;
            if (prev_count < 0){
                prev_count = 0;
            }
            int count = counter.intValue();
//            int prev_data_total = prev_data_up + prev_data_down;
            }
        catch(Exception e){
            e.printStackTrace();
        }

//
        HomeFragment.handler_speedometer_thread.postDelayed(this, 1000);
    }

}
