package com.candela.wecan.dashboard;

import android.util.Log;

import com.candela.wecan.ui.home.HomeFragment;

public class LinkSpeedThread implements Runnable{
    @Override
    public void run() {
        try{
            HomeFragment.up_down_data = HomeFragment.updateBpsDisplay();
            Log.e("link_speed", HomeFragment.up_down_data[0]);
        }
        catch (Exception e){
            e.printStackTrace();
        }
//                Log.e("link_speed_data",up_down_data.toString());
        HomeFragment.handler_link.postDelayed(HomeFragment.runnable_link, 2000);
    }
}
