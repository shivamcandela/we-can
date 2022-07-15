package com.candela.wecan.dashboard;

import android.graphics.Color;
import android.util.Log;

import com.candela.wecan.ui.home.HomeFragment;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.concurrent.atomic.AtomicInteger;

public class RealTimeChart implements Runnable{

    LineGraphSeries<DataPoint> series_upload;
    LineGraphSeries<DataPoint> series_download;
    LineGraphSeries<DataPoint> series_total;
    boolean initialized = false;
    long started_at_ms = 0;
    int max_data_points = 60 * 5; /* 5 minutes by default */

    @Override
    public void run() {
        String up_down[] = HomeFragment.up_down_data;
        double downlink = Double.parseDouble(up_down[0]);
        double uplink = Double.parseDouble(up_down[1]);

        double data_total = downlink + uplink;

        if (!initialized) {
            boolean animated = false;

            initialized = true;
            started_at_ms = System.currentTimeMillis();
            series_upload = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(0, uplink)
                });
            series_download = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(0, downlink)
                });
            series_total = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(0, data_total)
                });

            series_upload.setTitle("Upload");
            series_upload.setColor(Color.GREEN);
            series_upload.setDrawDataPoints(true);
            series_upload.setDataPointsRadius(0);
            series_upload.setThickness(2);
            series_upload.setDrawBackground(true);
            series_upload.setAnimated(animated);
            HomeFragment.graph.addSeries(series_upload);

            series_download.setTitle("download");
            series_download.setColor(Color.RED);
            series_download.setDrawDataPoints(true);
            series_download.setDataPointsRadius(0);
            series_download.setThickness(2);
            series_download.setDrawBackground(true);
            series_download.setAnimated(animated);
            HomeFragment.graph.addSeries(series_download);

            series_total.setTitle("Total");
            series_total.setColor(Color.BLUE);
            series_total.setDrawDataPoints(true);
            series_total.setDataPointsRadius(0);
            series_total.setThickness(2);
            series_total.setDrawBackground(true);
            series_total.setAnimated(animated);
            HomeFragment.graph.addSeries(series_total);

            // If enabled, this causes only last datapoint to be shown.
            //HomeFragment.graph.getViewport().setScalable(true); // enabling horizontal zooming and scrolling

            GridLabelRenderer gridLabel = HomeFragment.graph.getGridLabelRenderer();
            gridLabel.setHorizontalAxisTitleColor(Color.BLUE);
            gridLabel.setVerticalAxisTitleColor(Color.BLUE);
            // gridLabel.setHorizontalAxisTitle("Time in seconds");
            // gridLabel.setVerticalAxisTitle("Traffic in Mbps");
            HomeFragment.graph.setTitle("Traffic status (Y-axis in Mbps X-axis in Sec)");
        }
        else {
            // add next set of data
            long now = System.currentTimeMillis();
            long count = (now - started_at_ms) / 1000;
            boolean scroll_to_end = false;

            //System.out.println("adding chart datapoint, count: " + count + " uplink: " + uplink
            //                   + " downlink: " + downlink + " max-data-points: " + max_data_points);
            series_upload.appendData(new DataPoint(count, uplink), scroll_to_end, max_data_points, true);
            series_download.appendData(new DataPoint(count, downlink), scroll_to_end, max_data_points, true);
            series_total.appendData(new DataPoint(count, data_total), scroll_to_end, max_data_points, false);
        }

        // Call this again in another second
        HomeFragment.handler_graph.postDelayed(HomeFragment.runnable_graph, 1000);

//      HomeFragment.graph.takeSnapshotAndShare(HomeFragment.home_fragment_activity, "exampleGraph", "GraphViewSnapshot");
//      Bitmap bitmap = HomeFragment.graph.takeSnapshot();

    }
}
