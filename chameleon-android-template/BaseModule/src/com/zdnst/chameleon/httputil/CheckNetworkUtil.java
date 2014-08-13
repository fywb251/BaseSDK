package com.zdnst.chameleon.httputil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetworkUtil {
	public static boolean checkNetWork(Context context) {
		
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info == null || !info.isAvailable()) {
					return false;
				} else {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
