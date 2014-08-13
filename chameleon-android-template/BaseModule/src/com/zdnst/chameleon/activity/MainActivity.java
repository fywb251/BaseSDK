package com.zdnst.chameleon.activity;

import org.apache.cordova.CordovaWebViewClient;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;

import com.zdnst.chameleon.base.BaseFragmentActivity;

public class MainActivity extends BaseFragmentActivity {
	private static final String TAG = "MainActivity";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        //加载主页面数据
		loadMainContent("http://www.baidu.com/");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				//加载详情页面数据
				loadDetailContent("http://www.baidu.com/",false,null);
			}
		}, 3000);
		
		//设置主页面的WebViewClient
		setMainWebViewClient(new CordovaWebViewClient(this){
			@Override
			public void onPageFinished(WebView arg0, String arg1) {
				super.onPageFinished(arg0, arg1);
				Log.i(TAG, "setMainWebViewClient ====================onPageFinished");
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Log.i(TAG, "setMainWebViewClient ====================onPageStarted");
			}
		});
		
		//设置详情页面的WebViewClient
		setDetailWebViewClient(new CordovaWebViewClient(this){
			@Override
			public void onPageFinished(WebView arg0, String arg1) {
				super.onPageFinished(arg0, arg1);
				Log.i(TAG, "setDetailWebViewClient ====================onPageFinished");
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				Log.i(TAG, "setDetailWebViewClient ====================onPageStarted");
			}
		});

    }
	



}
