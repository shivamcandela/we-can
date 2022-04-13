package com.candela.wecan.dashboard;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
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

public class WebBrowser implements Runnable {

    public WebBrowser(){

    }

    @Override
    public void run() {
        WebSettings settings = HomeFragment.webpage_view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        //settings.setUseWideViewPort(true);造成文字太小
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
//        settings.setAppCachePath(getCacheDir().getAbsolutePath() + "/webViewCache");
        settings.setAppCacheEnabled(false);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        HomeFragment.webpage_view.setWebChromeClient(new WebChromeClient(){
//             @Override
//             public void onProgressChanged(WebView view, int newProgress) {
//                 super.onProgressChanged(view, newProgress);
//                 System.out.println("shivam-candela" + String.valueOf(newProgress));
//                 if (newProgress == 100){
//                     view.clearView();
//                     view.clearCache(true);
//                     HomeFragment.webpage_view.loadUrl("https://www.google.com");
//                 }
//             }
//
//             @Nullable
//             @Override
//             public View getVideoLoadingProgressView() {
//                 return super.getVideoLoadingProgressView();
//             }
//
//             @Override
//             public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
//                 return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
//             }
//
//             @Override
//             public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
//                 System.out.println("message: "+ consoleMessage.message());
//                 return super.onConsoleMessage(consoleMessage);
//
//             }
//
//        }
//        );
        String frameVideo = "<html><body>Youtube video .. <br> <iframe width=\"320\" height=\"315\" src=\"https://www.youtube.com/\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

        HomeFragment.webpage_view.loadData(frameVideo, "text/html", "utf-8");
        HomeFragment.webpage_view.setWebViewClient(new WebViewClient(){


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                HomeFragment.webpage_view.clearCache(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.clearView();
                HomeFragment.webpage_view.clearCache(true);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                System.out.println(request.getUrl().toString());
                return super.shouldInterceptRequest(view, request);
            }

        });
        HomeFragment.webpage_view.loadUrl("https://www.youtube.com/watch?v=o9ZyXWTkYww&list=RDo9ZyXWTkYww&start_radio=1");
//        HomeFragment.handler_webpage_test.postDelayed(HomeFragment.runnable_webpage_test,5000);
    }
}
