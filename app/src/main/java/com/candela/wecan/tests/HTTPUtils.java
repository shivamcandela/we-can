package com.candela.wecan.tests;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Timestamp;

public class HTTPUtils {
    public static WebView webview;
    private static String url;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public HTTPUtils(String url, WebView webView){
        this.url = url;
        this.webview = webView;
        this.RunTraffic();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void RunTraffic(){
        Uri webpage = Uri.parse("");
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.supportMultipleWindows();
        settings.setUseWideViewPort(false);

        webview.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Start timer here
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println("Iron_start: " + timestamp.toString());
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                System.out.println("Iron_stop: " + timestamp.toString());
                System.out.println("url_iron:  " + url);
                // stop timer here
            }
        });
        settings.setJavaScriptEnabled(true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                webview.loadUrl(url);
                handler.removeCallbacks(this);
            }
        },1000);
    }


}
