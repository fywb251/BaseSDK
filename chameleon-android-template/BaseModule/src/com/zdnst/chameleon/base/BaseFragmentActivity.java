package com.zdnst.chameleon.base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zdnst.juju.R;
import com.zdnst.chameleon.fragment.ComponentsDetailFragment;
import com.zdnst.chameleon.fragment.ComponentsMainFragment;
import com.zdnst.chameleon.fragment.PageNavigationFragment;
import com.zdnst.chameleon.support.CordovaInterfaceForSupportFragment;
import com.zdnst.chameleon.util.CommonUtil;

public class BaseFragmentActivity extends FragmentActivity implements CordovaInterfaceForSupportFragment{
	protected ExecutorService threadPool = Executors.newCachedThreadPool();
	
	private ComponentsMainFragment mMainFragment;
	private ComponentsDetailFragment mDetailFragment;
	private FrameLayout mDetailContain;
	
	private View contentView;
	private Handler handler;
	// 窗口移动的速度
	private final static int MOVEMENT_SPEED = 16;
	private int xMove = 0;
	// 只做一次业务
	private	Boolean firstTime = true;
	private int touchX = 0;
	private int temp;
	protected String url = null;
	public  RelativeLayout autodownloadlayout;
	public  TextView progress;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.page_main);
		
		initView();
		makeContentHalf();
	}
	
	private void initView(){
		if(CommonUtil.isPad(getActivity())){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else{
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		mMainFragment = (ComponentsMainFragment) this.getSupportFragmentManager()
				.findFragmentById(R.id.page_main_frament);
		mDetailContain = (FrameLayout) findViewById(R.id.page_main_detail_frame_layout);
		progress = (TextView) findViewById(R.id.progress);
		autodownloadlayout = (RelativeLayout) findViewById(R.id.autodownloadlayout);
		// 动态拖拉窗口效果
		mDetailContain.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						// 向右弹
						if (v.getX() < mMainFragment.getWidth() / 4 && v.getX() > 0) {
							sendHandlerMessage(0, 0);//
							// 向左弹
						} else if (v.getX() > mMainFragment.getWidth() / 4 && v.getX() < mMainFragment.getWidth() / 2) {
							sendHandlerMessage(21,
							mMainFragment.getWidth() / 2 - 24);
						} else if (v.getX() < 0) {
							sendHandlerMessage(0, 0);
						} else if (v.getX() > mMainFragment.getWidth() / 2 && v.getX() < (mMainFragment.getWidth() / 4) * 3) {
							sendHandlerMessage(21,
							mMainFragment.getWidth() / 2 - 24);
						} else if (v.getX() > (mMainFragment.getWidth() / 3)) {
							sendHandlerMessage(0, mMainFragment.getWidth());
							mDetailContain.setVisibility(View.GONE);
						}
						firstTime = true;
					}else if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (firstTime) {// 记录第一次点下的坐标
							touchX = (int) event.getRawX();
							xMove = (int) v.getX();
							firstTime = false;
						} else {
							int x = (int) event.getRawX();
							temp = x - touchX;
							v.setX(xMove + temp);// 通过设备view X坐标的值 达到移动空窗口的目的
						}
					}
				return true;
				}
			});
				
			// handler接收信息,主要为了窗口移动动画
			handler = new Handler() {
				@SuppressLint("NewApi")
				public void handleMessage(Message msg) {
				int toX = msg.arg2;
				int distance = msg.arg1;
				int animRate = 0;
				// 判断是向左移 还是向右移
				if (toX < mDetailContain.getX()) {
					animRate = -MOVEMENT_SPEED;
					mDetailContain.setX(distance);
					// 判断是否移到指定位置
					if (mDetailContain.getX() > toX) {
						Message m = new Message();
						m.arg2 = toX;
						m.arg1 = (int) mDetailContain.getX() + animRate;// 循环累加
						handler.sendMessageDelayed(m, 0);// 自己发信息 自己接收,0秒延时,实现循环
					} else {
						mDetailContain.setX(toX);
					}
				} else if (toX > mDetailContain.getX()) {
					animRate = MOVEMENT_SPEED;
					mDetailContain.setX(distance);
					if (mDetailContain.getX() < toX) {
						Message m = new Message();
						m.arg2 = toX;
						m.arg1 = (int) mDetailContain.getX() + animRate;
						handler.sendMessageDelayed(m, 0);
					} else {
						mDetailContain.setX(toX);
					}
				}
			}
		};
	}

	
	/**
	 * 加载主页面数据
	 * @param url
	 */
	public void loadMainContent(String url){
		if(mMainFragment != null){
			mMainFragment.loadMainContent(url);
		}
	}
	
	/**
	 * 设置主页面的效果
	 * @param url
	 */
	public void setMainJavaScript(String javaScript){
		if(mMainFragment != null){
			mMainFragment.setMainJavaScript(javaScript);
		}
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
	
	
	/**
	 * 加载详情页面的数据
	 * @param url
	 */
	public void loadDetailContent(String value,boolean isLocal,Bundle budle){
		recompute();
		url = value;
		//获取fragment
		removeAllDetailFragment();
		PageNavigationFragment navigationFragment = new PageNavigationFragment();
		navigationFragment.init(getNewDetailFragment(value,isLocal,budle,true));
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.page_main_detail_frame_layout, navigationFragment);
		fragmentTransaction.commitAllowingStateLoss();
		//设置详情页面的WebViewClient
		
	}
	
	/**
	 * 获取跳转的Fragment
	 * @param value
	 * @param isLocal
	 * @param budle
	 * @param isRootFragment
	 * @return
	 */
	private Fragment getNewDetailFragment(String value,boolean isLocal,Bundle budle,boolean isRootFragment){
		Fragment fragment = null;
		if(isLocal){
			String fragmentClassName = value;
			fragment = Fragment.instantiate(this, fragmentClassName);
			if(budle != null){
				fragment.setArguments(budle);
			}
			return fragment;
		}else{
				mDetailFragment = null;
				mDetailFragment = new ComponentsDetailFragment();
				Bundle bundle = new Bundle();
				bundle.putString("url", value);
				mDetailFragment.setArguments(bundle);
				return mDetailFragment;
		}
	}
	
	/**
	 * 详情页面推送到下一个页面
	 * @param value
	 * @param isLocal
	 * @param budle
	 */
	public void pushDetailFragment(String value,boolean isLocal,Bundle budle){
		PageNavigationFragment navigationFragment = (PageNavigationFragment) getSupportFragmentManager().findFragmentById(R.id.page_main_detail_frame_layout);
		if(navigationFragment != null){
			navigationFragment.push(getNewDetailFragment(value,isLocal,budle,false));
		}
	}
	
	/**
	 * 返回到上一个页面
	 * @return
	 */
	protected boolean popFragment(){
		boolean isBack = false;
		PageNavigationFragment navigationFragment = (PageNavigationFragment) getSupportFragmentManager().findFragmentById(R.id.page_main_detail_frame_layout);
		if(navigationFragment != null){
			isBack = navigationFragment.handleBack();
		}
		return isBack;
	}
	
	/**
	 * 移除详情模块的所有页面
	 */
	private void removeAllDetailFragment(){
		Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.page_main_detail_frame_layout);
		if(fragment != null){
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.remove(fragment);
			fragmentTransaction.commitAllowingStateLoss();
		}
	}
	
	
	/**
	 * 设置详情页面的效果
	 * @param javaScript
	 */
	public void setDetailJavaScript(String javaScript){
		if(mDetailFragment != null){
			mDetailFragment.setDetailJavaScript(javaScript);
		}
	}
	
	public void showDetailContent(boolean isShow){
		if(isShow){
			mDetailContain.setVisibility(View.VISIBLE);
		}else{
			mDetailContain.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 获取主页面的链接
	 * @return
	 */
	protected String getMainContentUrl(){
		String url = "";
		if(mMainFragment != null){
			url =  mMainFragment.contentUrl;
		}
		return url;
	}
	
	/**
	 * 获取详情页面的链接
	 * @return
	 */
	protected String getDetailContentUrl(){
		String url = "";
		if(mDetailFragment != null){
			url =  mDetailFragment.contentUrl;
		}
		return url;
	}
	
	
	/**
	 * 设置主页面的WebViewClient
	 * @param webViewClient
	 */
	protected void setMainWebViewClient(CordovaWebViewClient webViewClient){
		if(mMainFragment != null){
			mMainFragment.setWebViewClient(webViewClient);
		}
	}
	
	
	/**
	 * 设置主页面 ChromeClient
	 * @param webChromeClient
	 */
	protected void setMainWebChromClient(CordovaChromeClient webChromeClient){
		if(mMainFragment != null){
			mMainFragment.setWebChromeClient(webChromeClient);
		}
	}
	
	/**
	 * 设置详情页面的WebViewClient
	 * @param webViewClient
	 */
	protected void setDetailWebViewClient(CordovaWebViewClient webViewClient){
		if(mDetailFragment != null){
			mDetailFragment.setWebViewClient(webViewClient);
		}
	}
	
	/**
	 * 设置详情页面的 ChromeClient
	 * @param webChromeClient
	 */
	protected void setDetailWebChromClient(CordovaChromeClient webChromeClient){
		if(mDetailFragment != null){
			mDetailFragment.setWebChromeClient(webChromeClient);
		}
	}
	

	/**
	 * 获取主页面的webview
	 * @return
	 */
	protected CordovaWebView getMainWebView(){
		return mMainFragment.getCordovaWebView();
	}
	
	/**
	 * 判断是否为Pad 
	 * @return
	 */
	protected boolean isPad(){
		return CommonUtil.isPad(this);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	/**
	 *组装handler信息
	 * @param padding
	 * @param toX
	 */
	@SuppressLint("NewApi")
	private void sendHandlerMessage(int padding, int toX) {
		mDetailContain.setPadding(padding, 0, 0, 0);
		Message m = new Message();
		m.arg2 = toX;
		m.arg1 = (int) mDetailContain.getX();
		handler.sendMessage(m);
	}
	
	
	/**
	 * 计算窗口大小 ，占其空间一半
	 */
	@SuppressLint("NewApi")
	private void makeContentHalf() {
		contentView = this.getActivity().findViewById(R.id.page_main_content_window_id);
		contentView.getViewTreeObserver().addOnPreDrawListener(
				new ViewTreeObserver.OnPreDrawListener() {
					public boolean onPreDraw() {
						int contentViewWidth = contentView.getWidth();
						mMainFragment.setWidth(contentViewWidth);
						RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
								contentViewWidth / 2 + 24,
								LayoutParams.MATCH_PARENT);
						layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,
								RelativeLayout.TRUE);
						mDetailContain.setLayoutParams(layoutParams);
						return true;
					}
				});
		mDetailContain.setVisibility(View.GONE);
	}

	
	/**
	 * 计算滑动位置
	 */
	@SuppressLint("NewApi")
	private void recompute() {
		if (mDetailContain.getX() != 0) {
			mDetailContain.setPadding(21, 0, 0, 0);
			mDetailContain.setBackgroundResource(R.drawable.shadow);
			mDetailContain.setX(mMainFragment.getWidth() / 2 - 24);
		}
		mDetailContain.setVisibility(View.VISIBLE);	
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
	public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {}

	@Override
	public FragmentActivity getActivity() {
		return this;
	}

	@Override
	public Fragment getFragment() {
		return null;
	}
}
