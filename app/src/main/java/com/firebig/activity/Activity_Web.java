package com.firebig.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by FireBig-CH on 16-8-19.
 * 显示网页类
 */
public class Activity_Web extends BaseActivity {

    /**
     * 网页组件
     */
    private WebView webView;
    /**
     * 加载提示
     */
    private ProgressBar bar;
    /**
     * 加载提示
     */
    private ProgressDialog dialog;
    /**
     * 本地网页路径
     */
    private String url;
    private String[] urls = {"OpenSourceLicense.html", "web_agreement.html", "web_help.html", "content.html"};

    @Override
    public void initUI() {

        webView = (WebView) findViewById(R.id.wap_wap);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/" + urls[getIntent().getExtras().getInt("position")]);
        webView.setWebViewClient(new WebClient());
        bar = (ProgressBar) findViewById(R.id.wap_progress);
        dialog = new ProgressDialog(this);
    }

    @Override
    public void loadData() {
        //webView.set
    }

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_web);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getIntent().getExtras().getString("title"));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View view) {

    }

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        if ((paramInt == KeyEvent.KEYCODE_BACK) && (webView.canGoBack())) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(paramInt, paramKeyEvent);
    }


    public class WebClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            dialog.dismiss();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
