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
    //    public static String URL;
//    public boolean FINISHED=false;
//

//    public List CALL_BACKS;



//    public static boolean timeout;

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
    public static void stopTest(){
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
        System.out.println("Page Load Finished");
        RUNNING=false;
        totalUrls =  1;
        END_TIME = new Timestamp(System.currentTimeMillis());
        if (!STOP) {
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

//    @Override
//    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//        super.onReceivedHttpError(view, request, errorResponse);
//    }
//
//    @Override
//    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
//        super.onReceivedHttpAuthRequest(view, handler, host, realm);
//    }


//
//    @Override
//    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
//        return super.onRenderProcessGone(view, detail);
//    }
//
//    @Override
//    public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//        return super.shouldOverrideKeyEvent(view, event);
//    }
//
//    @Override
//    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
//        super.onReceivedClientCertRequest(view, request);
//
//    }



//    @RequiresApi(api = Build.VERSION_CODES.P)
//    public void StartTest() {
////        System.out.println(this.urlList);
//        WebSettings settings = HomeFragment.webpage_view.getSettings();
//
//        settings.setJavaScriptEnabled(true);
//        settings.setDatabaseEnabled(true);
//        settings.setDomStorageEnabled(true);
//        settings.setLoadWithOverviewMode(true);
//        settings.setBuiltInZoomControls(true);
//        settings.setUseWideViewPort(true);
//        settings.setAppCacheEnabled(false);
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
//        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//
////        HomeFragment.webpage_view.loadUrl(this.urlList.get(0));
//
//        List<BrowserUtils> test_load = new ArrayList<BrowserUtils>();
//        List main_test = new ArrayList();
//        HomeFragment.webpage_view.getHitTestResult();
//        HomeFragment.webpage_view.setWebViewClient(new WebViewClient(){
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                System.out.println("Lota party" + android.os.Process.myPid());
//                System.out.println("Lota party" + android.os.Process.myUid());
//                initial_bytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
//                HomeFragment.webpage_view.clearCache(true);
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                System.out.println("Test Started: " + timestamp);
//                main_test.add(timestamp);
//                WebBrowser.timestamps.add(timestamp);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                view.clearView();
//
//                HomeFragment.webpage_view.clearCache(true);
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                System.out.println("Test Ended: " + timestamp);
//                WebBrowser.timestamps.add(timestamp);
//                main_test.add(timestamp);
//            }
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//
//                super.onReceivedError(view, request, error);
//            }
//
//            @Override
//            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//                super.onReceivedHttpError(view, request, errorResponse);
//            }
//
//            @Override
//            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
//                super.onReceivedHttpAuthRequest(view, handler, host, realm);
//            }
//
//
//
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Nullable
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
////                System.out.println("Intercept: " + timestamp + " " + request.getUrl().toString());
////                System.out.println("IRON_HULK: " + request.getMethod());
////                System.out.println("IRON_HULK: " + request.getRequestHeaders().values().toString());
//
//                    BrowserUtils bu = new BrowserUtils();
//                    bu.start_time = timestamp;
//                    bu.end_time = null;
//                    bu.time_diff = 0;
//                    bu.url = request.getUrl().toString();
//                    test_load.add(bu);
//
//                return super.shouldInterceptRequest(view, request);
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onLoadResource(WebView view, String url) {
//                super.onLoadResource(view, url);
////                current_bytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
////                long totalBytes = current_bytes - initial_bytes;
////                Log.e("bytes-gogo", String.valueOf(totalBytes));
////                System.out.println("SPD_POWER" + HomeFragment.webpage_view.getWebViewLooper());
//                final_bytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
//                Log.e("final bytes: ", String.valueOf(final_bytes-initial_bytes));
//                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//                System.out.println("onLoadResource: " + timestamp + " " + url);
//                for (int i=0;i<test_load.size();i++){
////                    System.out.println(test_load.get(i).url);
//                    if (url.equals(test_load.get(i).url)){
//                        test_load.get(i).end_time = timestamp;
//                        test_load.get(i).time_diff = test_load.get(i).end_time.getTime() - test_load.get(i).start_time.getTime();
//                    }
//                }
////                for (BrowserUtils bu: test_load){
////                    System.out.println(bu.url);
////                    if (bu.url.equals(url)){
////                        bu.end_time = timestamp;
////                    }
////                }
////                System.out.println("kabutops " + timestamp + url);
//            }
//
//            @Override
//            public void onPageCommitVisible(WebView view, String url) {
//                super.onPageCommitVisible(view, url);
////                System.out.println("onPageCommitVisible " + url);
//            }
//
//            @Override
//            public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
//                System.out.println("HOHOHOHOHO");
//                return super.onRenderProcessGone(view, detail);
//            }
//
//            @Override
//            public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
//                return super.shouldOverrideKeyEvent(view, event);
//            }
//
//            @Override
//            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
//                super.onReceivedClientCertRequest(view, request);
//
//            }
//        });
//        Handler handler = new Handler();
//        length_old = test_load.size();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                boolean repeat = false;
//                for (int i=0;i<test_load.size();i++) {
//                    System.out.println(test_load.get(i).url);
//                    System.out.println(test_load.get(i).start_time);
//                    System.out.println(test_load.get(i).end_time);
//                    System.out.println(test_load.get(i).url);
//                        if (test_load.get(i).end_time == null){
//                        repeat = true;
//                    }
//                }
//                if (repeat || test_load.size() > length_old){
//                    length_old = test_load.size();
//                    handler.postDelayed(this,2000);
//                }
//                else {
//                    System.out.println("DONE");
//                    for(BrowserUtils bu: test_load){
//                        System.out.println(bu.url);
//                        System.out.println(bu.start_time);
//                        System.out.println(bu.end_time);
//                        System.out.println(bu.time_diff);
//                    }
////                    HomeFragment.webpage_view.loadUrl(urlList.get(0));
////                    handler.postDelayed(this,10000);
//
//                }
//            }
//        };
//        handler.postDelayed(runnable,10000);
//
//
//
//
//
//    }



//    @Override
//    public void run() {
//        WebSettings settings = HomeFragment.webpage_view.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        settings.setLoadWithOverviewMode(true);
//        settings.setBuiltInZoomControls(true);
//        //settings.setUseWideViewPort(true);造成文字太小
//        settings.setDomStorageEnabled(true);
//        HomeFragment.webpage_view.loadUrl("https://www.youtube.com");
//        settings.setDatabaseEnabled(true);
////        settings.setAppCachePath(getCacheDir().getAbsolutePath() + "/webViewCache");
//        settings.setAppCacheEnabled(false);
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
////        HomeFragment.webpage_view.setWebChromeClient(new WebChromeClient(){
////             @Override
////             public void onProgressChanged(WebView view, int newProgress) {
////                 super.onProgressChanged(view, newProgress);
////                 System.out.println("shivam-candela" + String.valueOf(newProgress));
////                 if (newProgress == 100){
////                     view.clearView();
////                     view.clearCache(true);
////                     HomeFragment.webpage_view.loadUrl("https://www.google.com");
////                 }
////             }
////
////             @Nullable
////             @Override
////             public View getVideoLoadingProgressView() {
////                 return super.getVideoLoadingProgressView();
////             }
////
////             @Override
////             public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
////                 return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
////             }
////
////             @Override
////             public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
////                 System.out.println("message: "+ consoleMessage.message());
////                 return super.onConsoleMessage(consoleMessage);
////
////             }
////
////        }
////        );
////        String frameVideo = "<html><body>Youtube video .. <br> <iframe width=\"320\" height=\"315\" src=\"https://www.youtube.com/\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
////
////        HomeFragment.webpage_view.loadData(frameVideo, "text/html", "utf-8");
//        HomeFragment.webpage_view.setWebViewClient(new WebViewClient(){
//
//
//            @Override
//            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                super.onPageStarted(view, url, favicon);
//                HomeFragment.webpage_view.clearCache(true);
//            }
//
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                super.onPageFinished(view, url);
//                view.clearView();
//                HomeFragment.webpage_view.clearCache(true);
//                System.out.println("katwalk");
////                HomeFragment.webpage_view.loadUrl("https://www.youtube.com");
//            }
//
//            @Override
//            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                super.onReceivedError(view, request, error);
//            }
//
//            @Override
//            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
//                super.onReceivedHttpError(view, request, errorResponse);
//            }
//
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Nullable
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                System.out.println(request.getUrl().toString());
//                return super.shouldInterceptRequest(view, request);
//            }
//
//            @Override
//            public void onLoadResource(WebView view, String url) {
//                super.onLoadResource(view, url);
//                System.out.println("kabutops " + url);
//            }
//        });
//
////        HomeFragment.handler_webpage_test.postDelayed(HomeFragment.runnable_webpage_test,5000);
//    }


}
