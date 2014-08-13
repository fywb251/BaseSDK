package com.zdnst.chameleon.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.zdnst.juju.R;

public class PageNavigationFragment extends Fragment {
	private static final String KEY_PREFIX = "KEY_PREFIX";

	private static final String STORE_KEY_HISTORY_LIST = "STORE_KEY_HISTORY_LIST";
	private static final String STORE_KEY_MAIN_LAYOUT_ID = "STORE_KEY_MAIN_LAYOUT_ID";

	private ArrayList<String> historyList = new ArrayList<String>();
	private Fragment rootFragment;

	private RelativeLayout mainLayout;
	private int mainLayoutId;


	private boolean isShowAnimation = true;
	private boolean canPopFragment = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.page_navigation, container, false);
		mainLayout = (RelativeLayout) view.findViewById(R.id.page_navigation_layoutMain);
		
		if (savedInstanceState != null) {
			historyList = savedInstanceState.getStringArrayList(STORE_KEY_HISTORY_LIST);
			mainLayoutId = savedInstanceState.getInt(STORE_KEY_MAIN_LAYOUT_ID);
			mainLayout.setId(mainLayoutId);
		} else {
			if (mainLayoutId == 0) {
				mainLayoutId = this.hashCode();
			}

			mainLayout.setId(mainLayoutId);
			if (historyList == null) {
				historyList = new ArrayList<String>();
			}

			if (rootFragment != null) {
				FragmentTransaction transation = getChildFragmentManager().beginTransaction();
				transation.add(mainLayout.getId(), rootFragment, KEY_PREFIX + "_0");
				transation.commit();

				historyList.add(KEY_PREFIX + "_0");
				rootFragment = null;
			}
		}
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void init(Fragment rootFragment) {
		this.rootFragment = rootFragment;
	}

	public ArrayList<String> getHistory() {
		return historyList;
	}

	/**
	 * 推下一个页面
	 * @param fragment
	 * @param isAnimantion
	 */
	public void push(Fragment fragment,boolean isAnimantion){
		String key = KEY_PREFIX + "_" + historyList.size();
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		if (isAnimantion) {
			transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
		}

		final Fragment previousfragment = getCurrentFragment();
		if (previousfragment != null) {
			transaction.hide(previousfragment);
		}
		transaction.add(mainLayout.getId(), fragment, key);
		transaction.commitAllowingStateLoss();
		historyList.add(key);
	}
	
	public void push(Fragment fragment) {
		push(fragment,true);
		
	}

	
	public void pop() {
		if (historyList.size() > 1) {
			String key = historyList.get(historyList.size() - 2);
			popToFragmentByKey(key);
		}
	}

	/**
	 * 返回到第一个页面
	 */
	public void popToRootFragment() {
		if (historyList.size() > 0) {
			String key = historyList.get(0);
			popToFragmentByKey(key);
		}
	}
	

	/**
	 * 跳转到指定的fagment
	 * @param key
	 */
	public void popToFragmentByKey(String key) {
		if(!canPopFragment){
			return;
		}
		int keyIndex = historyList.indexOf(key);
		if (keyIndex >= 0) {
			if (keyIndex == historyList.size() - 1) {
				return;
			}

			List<String> removeKeyList = new ArrayList<String>();

			FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
			if (isShowAnimation) {
				transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
			}

			for (int i = keyIndex + 1; i < historyList.size(); i++) {
				String tag = historyList.get(i);
				removeKeyList.add(tag);
				Fragment fragment = getChildFragmentManager().findFragmentByTag(tag);
				transaction.remove(fragment);
			}

			historyList.removeAll(removeKeyList);

			Fragment fragment =  getChildFragmentManager().findFragmentByTag(key);
			
			transaction.show(fragment);
			transaction.commit();
		}
	}

	/**
	 * 获取当前的fragment
	 * @return
	 */
	public Fragment getCurrentFragment() {
		Fragment fragment = null;
		if (historyList.size() > 0) {
			String currentKey = historyList.get(historyList.size() - 1);
			fragment =  getChildFragmentManager().findFragmentByTag(currentKey);
		}
		return fragment;
	}
	
	/**
	 * 获取第一个fragment
	 * @return
	 */
	public Fragment getRootFragment(){
		Fragment fragment = null;
		if (historyList.size() > 0) {
			String currentKey = historyList.get(0);
			fragment =  getChildFragmentManager().findFragmentByTag(currentKey);
		}
		return fragment;
	}
	
	/**
	 * 移除所有fragment
	 */
	public void removeFragment(){
		List<String> removeKeyList = new ArrayList<String>();
		for (int i = 0; i < historyList.size(); i++) {
			String tag = historyList.get(i);
			removeKeyList.add(tag);
			final Fragment lastFragment = getChildFragmentManager().findFragmentByTag(tag);
			getChildFragmentManager().beginTransaction().remove(lastFragment).commit();
		}
		historyList.removeAll(removeKeyList);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i("123", "PageNavigationFragment  onSaveInstanceState  >>>>  ");
		outState.putStringArrayList(STORE_KEY_HISTORY_LIST, historyList);
		if (mainLayout == null) {

		}
		// TODO 有問題
		if (mainLayout != null) {
			outState.putInt(STORE_KEY_MAIN_LAYOUT_ID, mainLayout.getId());
		}
	}

	/**
	 * 返回处理
	 * @return
	 */
	public boolean handleBack() {
		if (historyList.size() > 1) {
			this.pop();
			return true;
		}
		return false;
	}
}
