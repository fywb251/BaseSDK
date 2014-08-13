package com.zdnst.chameleon.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zdnst.chameleon.base.BaseFragment;

public class ComponentsMainFragment extends BaseFragment{
	
	public Boolean openLayer = false;
	public int padding;
	public int width;
	private int isVisible;
	public int hight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		return view;
	}

	/**
	 * 加载主页面内容
	 * @param url
	 */
	public void loadMainContent(String url){
		loadWebViewUrl(url);
	}
	
	/**
	 * 设置主页面效果
	 * @param javaScript
	 */
	public void setMainJavaScript(String javaScript){
		setWebViewScript(javaScript);
	}
	
	
	public Boolean getOpenLayer() {
		return openLayer;
	}

	public void setOpenLayer(Boolean openLayer) {
		this.openLayer = openLayer;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHight() {
		return hight;
	}

	public void setHight(int hight) {
		this.hight = hight;
	}

	public int getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	

}
