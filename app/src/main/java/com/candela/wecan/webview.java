package com.candela.wecan;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.candela.wecan.tests.HTTPUtils;

public class webview extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        getSupportActionBar().hide();

        WebView webView = findViewById(R.id.web_view);
//        new HTTPUtils("https://www.google.com",webView);
//        new HTTPUtils("https://www.facebook.com",webView);
    }

}