package com.candela.wecan.dashboard;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Printer;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.candela.wecan.ui.home.HomeFragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class WebBrowser extends WebViewClient{

    public static boolean STOP=false;
    public static int totalUrls = 0;
    public boolean RUNNING=false;
    public static Timestamp START_TIME;
    public static Timestamp END_TIME;
    public long initial_bytes = 0;
    public long current_bytes = 0;
    public long totalBytes = 0;
    public static List ERRORS;


    public WebBrowser() throws InterruptedException {
        WebSettings settings = HomeFragment.webpage_view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        HomeFragment.webpage_view.setWebViewClient(this);
        HomeFragment.webpage_view.clearCache(true);
        HomeFragment.webpage_view.clearView();
        ERRORS = new ArrayList();

    }
    public static void startTest(String URL){
        ERRORS = new ArrayList();
        HomeFragment.webpage_view.loadUrl(URL);
    }
    public void stopTest(){
            STOP = true;

    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        totalUrls =  0;
        initial_bytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
        System.out.println("Page Load Started");
        RUNNING=true;
        ERRORS = new ArrayList();
        START_TIME = new Timestamp(System.currentTimeMillis());
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        current_bytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
        totalBytes = current_bytes - initial_bytes;
        System.out.println("Page Load Finished: Stop Test: "+ STOP);
        RUNNING=false;
        totalUrls =  1;
        END_TIME = new Timestamp(System.currentTimeMillis());
        if (!STOP) {
            synchronized (this) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            HomeFragment.webpage_test_btn.performClick();
            HomeFragment.webpage_view.clearCache(true);
            HomeFragment.webpage_view.clearView();
            HomeFragment.webpage_view.loadUrl(url);
        }
    }
    @Override
    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        current_bytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
        totalBytes = current_bytes - initial_bytes;
        System.out.println("Loading Resource");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println(timestamp);
        END_TIME = timestamp;
        System.out.println(totalBytes);
    }
    @SuppressLint("NewApi")
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        current_bytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
        totalBytes = current_bytes - initial_bytes;
        System.out.println("Page Load Received Error");
        ERRORS.add(error.getErrorCode());
    }



}
