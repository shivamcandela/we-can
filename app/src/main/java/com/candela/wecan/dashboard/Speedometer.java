package com.candela.wecan.dashboard;

import android.graphics.Color;
import android.util.Log;

import com.candela.wecan.tests.base_tools.HomeTableManager;
import com.candela.wecan.ui.home.HomeFragment;

public class Speedometer implements Runnable{
    @Override
    public void run() {
        Log.e("Speedometer Thread running","");

        HomeFragment.handler_live_data.removeCallbacks(HomeFragment.runnable_live);
        try {
            String up_down[] = HomeFragment.up_down_data;
            HomeTableManager.up_down_global = up_down;
            int downlink = Integer.parseInt(up_down[0]);
            int uplink = Integer.parseInt(up_down[1]);
//                  Configure upload value range colors
            HomeFragment.speedometerup.setLabelTextSize(10);
            HomeFragment.speedometerup.setMaxSpeed(500);
            HomeFragment.speedometerup.setMajorTickStep(25);
            HomeFragment.speedometerup.addColoredRange(0, 25, Color.RED);
            HomeFragment.speedometerup.addColoredRange(25, 100, Color.YELLOW);
            HomeFragment.speedometerup.addColoredRange(100, 500, Color.GREEN);
//                        Set the uplink value
            HomeFragment.speedometerup.setSpeed(uplink);

//                      Download Starts here
//                      Configure download value range colors
            HomeFragment.speedometerdown.setLabelTextSize(10);
            HomeFragment.speedometerdown.setMaxSpeed(500);
            HomeFragment.speedometerdown.setMajorTickStep(25);
            HomeFragment.speedometerdown.addColoredRange(0, 25, Color.RED);
            HomeFragment.speedometerdown.addColoredRange(25, 100, Color.YELLOW);
            HomeFragment.speedometerdown.addColoredRange(100, 500, Color.GREEN);
//                        Set the downlink value
            HomeFragment.speedometerdown.setSpeed(downlink);
        }
        catch(Exception e){
            e.printStackTrace();
        }

//
        HomeFragment.handler_speedometer_thread.postDelayed(this, 1000);
    }

}
