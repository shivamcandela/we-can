package com.candela.wecan.dashboard;

import android.graphics.Color;
import android.util.Log;

import com.candela.wecan.ui.home.HomeFragment;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.concurrent.atomic.AtomicInteger;

public class RealTimeChart implements Runnable{

    AtomicInteger counter = new AtomicInteger(0);
    DataPoint dp;
    public static double prev_data_up= 0;
    public static double prev_data_down = 0;
    public static int prev_data_total = 0;
    @Override
    public void run() {
        String up_down[] = HomeFragment.up_down_data;
        double downlink = Double.parseDouble(up_down[0]);
        double uplink =Double.parseDouble(up_down[1]);
        LineGraphSeries<DataPoint> series_upload = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> series_download = new LineGraphSeries<>();
        LineGraphSeries<DataPoint> series_total = new LineGraphSeries<>();

        series_upload.appendData(new DataPoint(1, 2),true,100);
        series_download.appendData(new DataPoint(1, 2),true,100);
        series_total.appendData(new DataPoint(1, 2),true,100);
        double prev_count = counter.doubleValue() - 1;
//        if (prev_count < 0){
//            prev_count = 0;
//        }
        int count = counter.intValue();
            double prev_data_total = prev_data_up + prev_data_down;

        series_upload = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(prev_count, prev_data_up),
                new DataPoint(count, prev_data_up = uplink)
        });
        series_download = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(prev_count, prev_data_down),
                new DataPoint(count, prev_data_down = downlink)
        });
        series_total = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(prev_count, prev_data_total),
                new DataPoint(count, prev_data_total = prev_data_up + prev_data_down)
        });

//        Log.e("uplink",String.valueOf(series_total));
        series_upload.setTitle("Upload");
        series_upload.setColor(Color.GREEN);
        series_upload.setDrawDataPoints(true);
        series_upload.setDataPointsRadius(0);
        series_upload.setThickness(2);
        series_upload.setDrawBackground(true);
        series_upload.setAnimated(true);
        HomeFragment.graph.addSeries(series_upload);
////
        series_download.setTitle("download");
        series_download.setColor(Color.RED);
        series_download.setDrawDataPoints(true);
        series_download.setDataPointsRadius(0);
        series_download.setThickness(2);
        series_download.setDrawBackground(true);
        series_download.setAnimated(true);
        HomeFragment.graph.addSeries(series_download);
//
        series_total.setTitle("Total");
        series_total.setColor(Color.BLUE);
        series_total.setDrawDataPoints(true);
        series_total.setDataPointsRadius(0);
        series_total.setThickness(2);
        series_total.setDrawBackground(true);
        series_total.setAnimated(true);
        HomeFragment.graph.addSeries(series_total);
//

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
        HomeFragment.handler_graph.postDelayed(HomeFragment.runnable_graph, 1000);
    }
}
