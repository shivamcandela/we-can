package com.candela.wecan.dashboard;

import android.util.Log;

import com.candela.wecan.ui.home.HomeFragment;

public class LinkSpeedThread implements Runnable{
    @Override
    public void run() {
        Log.e("LinkSpeedThread running","");

        try{
            HomeFragment.up_down_data = HomeFragment.updateBpsDisplay();
        }
        catch (Exception e){
            e.printStackTrace();
        }
//                Log.e("link_speed_data",up_down_data.toString());
        HomeFragment.handler_link.postDelayed(HomeFragment.runnable_link, 2000);
    }
}
