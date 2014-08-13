package com.zdnst.chameleon.base;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.IceCreamCordovaWebViewClient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.zdnst.juju.R;
import com.zdnst.chameleon.support.CordovaInterfaceForSupportFragment;

@SuppressLint("NewApi")
public class BaseFragment extends Fragment implements CordovaInterfaceForSupportFragment{
	protected ExecutorService threadPool = Executors.newCachedThreadPool();
	protected RelativeLayout mViewContain;
	protected CordovaWebView mCordovaWebView;
	protected String contentUrl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Config.init(getActivity());
		
		View view = inflater.inflate(R.layout.components_main, container, false);
		mCordovaWebView = (CordovaWebView) view.findViewById(R.id.components_main_mCordovaWebView);
		mViewContain = (RelativeLayout) view.findViewById(R.id.components_main_viewContain);
		resetCordovaInterface(mCordovaWebView);
		return view;
		
	}
	
	/**
	 * 加载数据
	 * @param url
	 */
	protected void loadWebViewUrl(String url){
		if(mCordovaWebView != null && url != null){
			mCordovaWebView.loadUrl(url);
			this.contentUrl = url;
		}
	}
	
	/**
	 * 设置效果
	 * @param javaScript
	 */
	protected void setWebViewScript(String javaScript){
		if(mCordovaWebView != null && javaScript != null){
			mCordovaWebView.sendJavascript(javaScript);
		}
	}
	
	/**
	 * 添加控件
	 * @param view
	 * @param params
	 */
	protected void addView(View view,LayoutParams params){
		if(view == null){
			return;
		}
		if(mViewContain != null){
			mViewContain.addView(view, params);
		}
	}
	
	/**
	 *	获取webview 
	 * @return
	 */
	protected CordovaWebView getCordovaWebView(){
		return mCordovaWebView;
	}
	
	
	/**
	 * 设置 CordovaWebViewClient
	 * @param webViewClient
	 */
	public void setWebViewClient(CordovaWebViewClient webViewClient){
		mCordovaWebView.setWebViewClient(webViewClient);
		webViewClient.setWebView(mCordovaWebView);
	}
	
	
	/**
	 * 设置CordovaChromeClient
	 * @param webChromeClient
	 */
	public void setWebChromeClient(CordovaChromeClient webChromeClient){
		mCordovaWebView.setWebChromeClient(webChromeClient);
		webChromeClient.setWebView(mCordovaWebView);
	}
	
	/***
	 * 设置 CordovaWebViewClient �� CordovaChromeClient
	 * @param webViewClient
	 * @param webChromeClient
	 */
	protected void initWebClient(CordovaWebViewClient webViewClient, CordovaChromeClient webChromeClient) {
		mCordovaWebView.setWebViewClient(webViewClient);
		mCordovaWebView.setWebChromeClient(webChromeClient);
		webViewClient.setWebView(mCordovaWebView);
		webChromeClient.setWebView(mCordovaWebView);
	}
	
	
	/**
	 * Construct the client for the default web view object.
	 * This is intended to be overridable by subclasses of CordovaIntent which require a more specialized web view.
	 * @param webView
	 *            the default constructed web view object
	 */
	protected CordovaWebViewClient makeWebViewClient(CordovaWebView webView) {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
			return new CordovaWebViewClient(this, webView){
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
				}
			};
		} else {
			return new IceCreamCordovaWebViewClient(this, webView){
				@Override
				public void onPageFinished(WebView arg0, String url) {
					super.onPageFinished(arg0, url);
				}
			};
		}
	}

	/**
	 * Construct the chrome client for the default web view object.
	 * This is intended to be overridable by subclasses of CordovaIntent which require a more specialized web view.
	 * @param webView
	 *            the default constructed web view object
	 */
	protected CordovaChromeClient makeChromeClient(CordovaWebView webView) {
		return new CordovaChromeClient(this, webView);
	}

	//重设置CordovaInterface
	protected void resetCordovaInterface(CordovaWebView webView) {
		try {

			Class cordovaWebView = CordovaWebView.class;
			Method loadConfig = cordovaWebView.getDeclaredMethod("loadConfiguration", null);
			Method setup = cordovaWebView.getDeclaredMethod("setup", null);
			Field cordova = cordovaWebView.getDeclaredField("cordova");
			cordova.setAccessible(true);
			loadConfig.setAccessible(true);
			setup.setAccessible(true);

			cordova.set(webView, this);
			loadConfig.invoke(webView, null);
			setup.invoke(webView, null);

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ExecutorService getThreadPool() {
		return threadPool;
	}

	@Override
	public Object onMessage(String arg0, Object arg1) {
		return null;
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin arg0) {
		
	}

	@Override
	public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {
		
	}

	@Override
	public Fragment getFragment() {
		return this;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mCordovaWebView != null) {
			mCordovaWebView.handleDestroy();
	     }
	}

	
}
