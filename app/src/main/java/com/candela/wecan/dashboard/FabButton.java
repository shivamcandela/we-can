package com.candela.wecan.dashboard;

import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.fragment.app.FragmentActivity;

import com.candela.wecan.ui.home.HomeFragment;

public class FabButton extends FragmentActivity implements View.OnClickListener {
    public Boolean isAllFabsVisible = false;
    @Override
    public void onClick(View v) {
        HomeFragment.computerFab.setVisibility(View.GONE);
        HomeFragment.shareFab.setVisibility(View.GONE);
        HomeFragment.wifiFab.setVisibility(View.GONE);
        HomeFragment.AddFab.bringToFront();

        Float translationY = 100f;
        OvershootInterpolator interpolator = new OvershootInterpolator();
        HomeFragment.AddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllFabsVisible) {

                    HomeFragment.AddFab.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

                    HomeFragment.computerFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
                    HomeFragment.shareFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
                    HomeFragment.wifiFab.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();

                    HomeFragment.computerFab.setVisibility(View.VISIBLE);
                    HomeFragment.shareFab.setVisibility(View.VISIBLE);
                    HomeFragment.wifiFab.setVisibility(View.VISIBLE);


                    isAllFabsVisible = true;
                } else {

                    HomeFragment.AddFab.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

                    HomeFragment.computerFab.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
                    HomeFragment.shareFab.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
                    HomeFragment.wifiFab.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

                    HomeFragment.computerFab.setVisibility(View.GONE);
                    HomeFragment.shareFab.setVisibility(View.GONE);
                    HomeFragment.wifiFab.setVisibility(View.GONE);

                    isAllFabsVisible = false;
                }
            }
        });
    }
}
