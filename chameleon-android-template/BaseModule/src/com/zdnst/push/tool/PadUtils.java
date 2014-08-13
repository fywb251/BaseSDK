package com.zdnst.push.tool;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class PadUtils {

	public static final double MIN_PAD_SIZE = 6.5;
	public static boolean isPad(Context applicationContext) {
//		DisplayMetrics dm = new DisplayMetrics();
//		WindowManager windowManager = (WindowManager)applicationContext.getSystemService(Context.WINDOW_SERVICE);
//		windowManager.getDefaultDisplay().getMetrics(dm);
//		int screenWidth = dm.widthPixels;
//		int screenHeight = dm.heightPixels;
//		float density = dm.density; // 屏幕密度（0.75 / 1.0 / 1.5）
//		// int densityDpi = dm.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
//		double diagonalPixels = Math.sqrt(Math.pow(screenWidth, 2) + Math.pow(screenHeight, 2));
//		double screenSize = diagonalPixels / (160 * density);
//		return screenSize>MIN_PAD_SIZE;
		
		boolean isPad = false;
		// {treat Galaxy Note as phone.N7000
		String model = Build.MODEL;
		if(model.contains("N7000") || model.contains("I9200") || model.contains("C6802") || model.contains("C6833")){
			return false;
		}
		Configuration config = applicationContext.getResources().getConfiguration();
		if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
			isPad = false;
		} else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
			isPad = false;
		} else if ((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4) {
			isPad = true;
		} else {
			isPad = true;
		}
		return isPad;
	}
	
	public static void setSceenSize(Activity activity){
		WindowManager m = activity.getWindowManager();    
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		LayoutParams p = activity.getWindow().getAttributes(); // 获取对话框当前的参数值
		if(isPad(activity)){
			p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的1.0
			p.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.8
		}
//		else{
//			p.height = (int)(d.getHeight() * 1);
//			p.width = (int)(d.getHeight() * 1);
//		}
		p.alpha = 1.0f; // 设置本身透明度
		p.dimAmount = 0.7f; // 设置黑暗度
		activity.getWindow().setAttributes(p);
	}
	
	
//	public static boolean isPad(Context context) {
//		
//	}
	
}
