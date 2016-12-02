package com.renrun.onlywebview;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.renrun.pullStyle.PtrlRRFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.myWebview)WebView myWebView;
    @BindView(R.id.ptr_frame)PtrlRRFrameLayout ptrFrame;
    private String  url = "http://www.renrunyun.com/chexinbao/login.html";
//    private String  url = "http://demo3.renrunkeji.com:8816/chexinbao/login.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setWebView();

        ptrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, myWebView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                myWebView.loadUrl(url);
            }
        });

        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh();
            }
        }, 100);
    }

    private void setWebView(){
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setUseWideViewPort(true);// 设置是当前html界面自适应屏幕
        myWebView.getSettings().setLoadWithOverviewMode(true);//设置加载进来的页面自适应手机屏幕
        myWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setAllowFileAccess(true);

        if(android.os.Build.VERSION.SDK_INT >= 11){
            myWebView.getSettings().setDisplayZoomControls(false);
        }
        myWebView.getSettings().setBuiltInZoomControls(true); //显示放大缩小 controler
        myWebView.getSettings().setSupportZoom(true); //可以缩放
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(false);
        //开启 database storage API 功能
        myWebView.getSettings().setDatabaseEnabled(false);
        myWebView.getSettings().setAppCacheEnabled(false);
        myWebView.getSettings().setDefaultTextEncodingName("utf-8");
        myWebView.addJavascriptInterface(new JsInteration(), "control");
        myWebView.setWebChromeClient(new WebChromeClient() {});

        myWebView.setSaveEnabled(false);
        myWebView.requestFocus();

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                MainActivity.this.url = url;
                ptrFrame.refreshComplete();
            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
            myWebView.goBack();// 返回前一个页面
            return true;
        } else {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    public class JsInteration {
        /**
         * js调用客户端函数
         * 关闭网页
         */
        @JavascriptInterface
        public void back() {
           if(myWebView.canGoBack()){
               myWebView.goBack();
           }
        }
    }
}
