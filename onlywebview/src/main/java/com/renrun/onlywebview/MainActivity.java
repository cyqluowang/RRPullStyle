package com.renrun.onlywebview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
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

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;

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
        myWebView.setWebChromeClient(new MyWebClient() {});

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


    public class MyWebClient extends WebChromeClient {
        // For Android < 3.0
        public void openFileChooser(ValueCallback<Uri> valueCallback) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android  >= 3.0
        public void openFileChooser(ValueCallback valueCallback, String acceptType) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        //For Android  >= 4.1
        public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
            uploadMessage = valueCallback;
            openImageChooserActivity();
        }

        // For Android >= 5.0
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            uploadMessageAboveL = filePathCallback;
            openImageChooserActivity();
            return true;
        }

    }
    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
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
