package com.zdnst.chameleon.fragment;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.zdnst.chameleon.base.BaseFragment;

public class ComponentsDetailFragment extends BaseFragment{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		if(getArguments() != null){
			String url = getArguments().getString("url");
			loadDetailContent(url);
		}
		if (Build.VERSION.SDK_INT > 11) {
			mCordovaWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		}
		
		return view;
	}

	//加载详情页面的内容
	public void loadDetailContent(String url){
		loadWebViewUrl(url);
	}
	
	//设置详情页面的效果
	public void setDetailJavaScript(String javaScript){
		setWebViewScript(javaScript);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

}
