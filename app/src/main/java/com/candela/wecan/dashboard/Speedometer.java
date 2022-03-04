package com.candela.wecan.dashboard;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.Log;

import com.candela.wecan.tests.base_tools.HomeTableManager;
import com.candela.wecan.ui.home.HomeFragment;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Speedometer implements Runnable{
    static AtomicInteger counter = new AtomicInteger(0);
    private LineGraphSeries<DataPoint> mSeries1;
    private int prev_data_up= 0;
    private int prev_data_down = 0;
    private int prev_data_total = 0;
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
            int up = uplink;
            Random rand = new Random();
            int prev_count = counter.intValue() - 1;
            if (prev_count < 0){
                prev_count = 0;
            }
            int count = counter.intValue();
//            int prev_data_total = prev_data_up + prev_data_down;
            LineGraphSeries<DataPoint> series_up = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(prev_count, prev_data_up),
                    new DataPoint(count, prev_data_up = uplink)
            });
            LineGraphSeries<DataPoint> series_down = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(prev_count, prev_data_down),
                    new DataPoint(count, prev_data_down = downlink)
            });
            LineGraphSeries<DataPoint> series_total = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(prev_count, prev_data_total),
                    new DataPoint(count, prev_data_total = prev_data_up + prev_data_down)
            });

            series_up.setTitle("Upload");
            series_up.setColor(Color.GREEN);
            series_up.setDrawDataPoints(true);
            series_up.setDataPointsRadius(0);
            series_up.setThickness(2);
            series_up.setDrawBackground(true);
//            series_up.setAnimated(true);
            HomeFragment.graph.addSeries(series_up);

            series_down.setTitle("download");
            series_down.setColor(Color.RED);
            series_down.setDrawDataPoints(true);
            series_down.setDataPointsRadius(0);
            series_down.setThickness(2);
            series_down.setDrawBackground(true);
//            series_down.setAnimated(true);
            HomeFragment.graph.addSeries(series_down);

            series_total.setTitle("Total");
            series_total.setColor(Color.BLUE);
            series_total.setDrawDataPoints(true);
            series_total.setDataPointsRadius(0);
            series_total.setThickness(2);
            series_total.setDrawBackground(true);
//            series_total.setAnimated(true);
            HomeFragment.graph.addSeries(series_total);

//            HomeFragment.graph.getLegendRenderer().setVisible(true);
//            HomeFragment.graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
//            HomeFragment.graph.getLegendRenderer().setVisible(false);
            counter.incrementAndGet();
            HomeFragment.graph.getViewport().scrollToEnd();
            HomeFragment.graph.getViewport().setScalable(true); // enabling horizontal zooming and scrolling

//            HomeFragment.graph.takeSnapshotAndShare(HomeFragment.home_fragment_activity, "exampleGraph", "GraphViewSnapshot");
//            Bitmap bitmap = HomeFragment.graph.takeSnapshot();

            GridLabelRenderer gridLabel = HomeFragment.graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitleColor(Color.BLUE);
            gridLabel.setVerticalAxisTitleColor(Color.BLUE);
//            gridLabel.setHorizontalAxisTitle("Time in seconds");
//            gridLabel.setVerticalAxisTitle("Traffic in Mbps");
            HomeFragment.graph.setTitle("Traffic status (X-axis in Mbps Y-axis in Sec)");
        }
        catch(Exception e){
            e.printStackTrace();
        }

//
        HomeFragment.handler_speedometer_thread.postDelayed(this, 1000);
    }

}
