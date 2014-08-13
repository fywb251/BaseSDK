package com.zdnst.template;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.midea.mmp2.R;
import com.zdnst.bsl.util.FileCopeTool;
import com.zdnst.bsl.util.Preferences;
import com.zdnst.bsl.util.ZipUtils;
import com.zdnst.push.tool.PadUtils;
import com.zdnst.zdnstsdk.CModule;
import com.zdnst.zdnstsdk.config.URL;

public class MainActivity extends Activity {

	private TemplateApplication temphetApplication;
	
	
	private RelativeLayout layout;
	
//	static final String LoginSuccess = "com.zdnst.loginmodule.loginsuccess";  

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		Preferences.saveToken("", "");
		if (PadUtils.isPad(MainActivity.this)){
			setContentView(R.layout.pad_splash_screen);
		} else {
			setContentView(R.layout.phone_splash_screen);
		}
		temphetApplication = TemplateApplication.class.cast(MainActivity.this.getApplication());
//		goToFirstModule();
//		IntentFilter intentFilter = new IntentFilter(LoginSuccess); 
//		registerReceiver( loginModuleReceiver , intentFilter); 
		
		layout = (RelativeLayout) findViewById(R.id.welcome_loadinglayout);
		
		String basePath = Environment.getExternalStorageDirectory().getPath() + "/"
				+ URL.APP_PACKAGENAME;
		
		//应用是否第一次使用
		Boolean isFirstTime = Preferences.getFirsttime();
		//www.zip是否已经被解压缩
		boolean hasUnZip = true;
		//www文件夹是否存在于手机sdcard内
		File file = new File(basePath + "/www");
		if (!file.exists()) {
			hasUnZip = false;
		}
		
		if (isFirstTime) {
			//第一次使用，必须解压www.zip
			Preferences.saveFirsttime(false);
			hasUnZip = false;
			Preferences.saveVersionCode(getVersionCode());
		} else {
			int versionCode = Preferences.getVersionCode();
			Log.i("versionCode","save_versionCode = "+versionCode);
			Log.i("versionCode","cur_versionCode = "+getVersionCode());
			if (versionCode < getVersionCode()){
				//升级应用后，重新解压www.zip覆盖旧文件
				Preferences.saveVersionCode(getVersionCode());
				hasUnZip = false;
			} 
		}
		
		if(!hasUnZip){
			//符合解压条件，开始解压
			file.mkdirs();
			UnZipAsynTask unZipAsynTask = new UnZipAsynTask(MainActivity.this);
			unZipAsynTask.execute("www/www.zip" , basePath + "/www/www.zip" ,basePath +"/www");
		}else{
			init();
		}
			
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		unregisterReceiver(loginModuleReceiver);
	}
	public void goToFirstModule(){
		HashMap<String, CModule> modules = temphetApplication.getCApplication().getModules();
		for (CModule module : modules.values()) {
			String activity = module.getActivity();
			if(module.isFirstStart()){
				Intent intent = new Intent();
				intent.setClassName(this, activity);
				this.startActivity(intent);
				finish();
			}
		}
	}
	
	
	/*
	 * 初始化方法实现
	 */
	private void init() {
		// 启动加载页面方式：一
		final long currentStart = System.currentTimeMillis();
		new Thread(new Runnable() {

			public void run() {
				// 休眠标识
				boolean flag = false;
				while (!flag) {
					// 如果休眠时间小于两秒，继续休眠
					if (System.currentTimeMillis() - currentStart > 1000) {
						flag = true;
						goToFirstModule();
					}
				}
			}
		}).start();
	}
	
//	private BroadcastReceiver loginModuleReceiver = new BroadcastReceiver() { 
//	       @Override 
//
//	       public void onReceive(Context context, Intent intent) { 
//	    	   Activity activity = temphetApplication.getCApplication().getActivity();
//	    	   activity.finish();
//	       }
//	    }; 
	
	class UnZipAsynTask extends AsyncTask<String, Integer, Boolean> {

		protected Context context;
		
		public UnZipAsynTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			layout.setVisibility(View.VISIBLE);
			System.out.println(System.currentTimeMillis() + "");
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result){
				System.out.println(System.currentTimeMillis() + "");
				layout.setVisibility(View.GONE);
				Preferences.saveFirsttime(false);
				goToFirstModule();;
			}else{
				Toast.makeText(MainActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
				finish();
			}
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			boolean result = false;
			try{
				FileCopeTool tool = new FileCopeTool(context);
				boolean copySuccess = tool.CopyAssetsFile(params[0], params[1]);
				if(copySuccess){
					if (ZipUtils.unZipFile(params[1], params[2])){
//						FileUtil.deleteFile(params[1]);
						result = true;
					} else {
						result = false;
					}
				} else {
					result = false;
				}
			}catch(Exception e){
				result = true;
				e.printStackTrace();
			}
			return result;
		}

	}
	
	public int getVersionCode() {
		PackageManager pm = MainActivity.this.getPackageManager();// context为当前Activity上下文
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(MainActivity.this.getPackageName(), 0);
			return pi.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
