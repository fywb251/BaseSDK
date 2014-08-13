package com.zdnst.juju;

import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.IceCreamCordovaWebViewClient;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.zdnst.chameleon.base.BaseFragmentActivity;

public class CubeSettingActivity extends BaseFragmentActivity {
	private String path = null;
	private String identify = null;
	private String mainUrl = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		path = getIntent().getStringExtra("path");
		identify = getIntent().getStringExtra("identify");
		mainUrl = "file:///" + path + "/www/" + identify + "/index.html";
		
		loadMainContent(mainUrl);
		setMainWebViewClient(webviewClient);
	}
	
//	private void setWebViewClient(){
//		WebViewClient  webviewClient = null;;
//		if (Build.VERSION.SDK_INT < 11) {
//			
//		}else{
//			
//		}
//		
//	}
	
	
	CordovaWebViewClient webviewClient = new CordovaWebViewClient(this){
		public boolean shouldOverrideUrlLoading(WebView webview, String url) {
			if(interceptUrl(webview, url)){
				return true;
			}return super.shouldOverrideUrlLoading(webview, url);
		};
	};
	
	IceCreamCordovaWebViewClient iceCreamCordovaWebView = new IceCreamCordovaWebViewClient(this){
		public boolean shouldOverrideUrlLoading(WebView webview, String url) {
			if(interceptUrl(webview, url)){
				return true;
			}
			return super.shouldOverrideUrlLoading(webview, url);
		};
	};
	
	private boolean interceptUrl(WebView view,String url){
		if (url.contains("cube-action=push")) {
			url = subUrl(url);
			Intent intent = new Intent(CubeSettingActivity.this,
					CubeSettingActivity.class);
			intent.putExtra("from", "web");
			intent.putExtra("url", url);
			startActivity(intent);
			return true;
		} else if (url.contains("cube-action=pop")) {
			finish();
			return true;

		} else if (url.endsWith("cube://exit")) {
			finish();
			return true;
		}
		return false;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	public String subUrl(String url) {
		int start = url.indexOf("cube-action=push");
		int end = start + 16;
		String newUrl = "";
		if (url.indexOf("&") == -1 && start != -1) {
			newUrl = url.substring(0, (url.indexOf("cube-action=push") - 1))
					+ url.subSequence(end, url.length());
		} else if (url.indexOf("&") > -1 && start < url.indexOf("&")) {
			newUrl = url.substring(0, (url.indexOf("cube-action=push")))
					+ url.subSequence(end + 1, url.length());
		} else if (url.indexOf("&") > -1 && start > url.indexOf("&")) {
			newUrl = url.substring(0, url.indexOf("&"))
					+ url.subSequence(end, url.length());
		}
		return newUrl;
	}
}