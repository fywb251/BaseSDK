package com.zdnst.juju;

import android.content.Context;

import com.google.gson.Gson;
import com.zdnst.bsl.util.Preferences;
import com.zdnst.chameleon.util.DeviceInfoUtil;
import com.zdnst.push.client.DeviceCheckinVo;
import com.zdnst.push.client.TagEntryVo;
import com.zdnst.push.tool.MD5Util;
import com.zdnst.zdnstsdk.config.URL;
import com.zdnst.zillasdk.Zilla;
import com.zdnst.zillasdk.ZillaDelegate;

public class CheckInUtil {
	
	public static void registerPush(final Context application,final String serviceName){
		String token = CheckInUtil.createMD5Token(application) + "@" + serviceName;
		final DeviceCheckinVo checkinVo = new DeviceCheckinVo();
		checkinVo.setDeviceId(DeviceInfoUtil.getDeviceId(application));
		checkinVo.setAppId(URL.APPKEY);
		checkinVo.setChannelId("openfire");
		checkinVo.setDeviceName(android.os.Build.MODEL);
		checkinVo.setOsName("android");
		checkinVo.setOsVersion(android.os.Build.VERSION.RELEASE);
		checkinVo.setPushToken(token);
		checkinVo.setTags(new TagEntryVo[] { new TagEntryVo("platform", "Android") });
		
		ZillaDelegate delegate = new ZillaDelegate() {
			
			@Override
			public void requestSuccess(String result) {
				
			}
			
			@Override
			public void requestStart() {
				
			}
			
			@Override
			public void requestFailed(String errorMessage) {
				
			}
		};
		
		Zilla.getZilla().pushCheckIn(application, delegate, new Gson().toJson(checkinVo));
	}
	
	public static void pushSecurity(final Context application , final String role){
		String serviceName = Preferences.getServiceName();
		String token = CheckInUtil.createMD5Token(application) + "@" + serviceName;
		final DeviceCheckinVo checkinVo = new DeviceCheckinVo();
		checkinVo.setDeviceId(DeviceInfoUtil.getDeviceId(application));
		checkinVo.setAppId(URL.APPKEY);
		String alias = Preferences.getUserName();
		checkinVo.setAlias(alias);
		checkinVo.setChannelId("openfire");
		checkinVo.setDeviceName(android.os.Build.MODEL);
		checkinVo.setOsName("android");
		checkinVo.setOsVersion(android.os.Build.VERSION.RELEASE);
		checkinVo.setPushToken(token);
		checkinVo.setTags(new TagEntryVo[] { 
				new TagEntryVo("platform", "Android") ,
				new TagEntryVo("role", role) });
		
		
		ZillaDelegate delegate = new ZillaDelegate() {
			
			@Override
			public void requestSuccess(String result) {
				
			}
			
			@Override
			public void requestStart() {
				
			}
			
			@Override
			public void requestFailed(String errorMessage) {
				
			}
		};
		
		Zilla.getZilla().pushCheckIn(application, delegate, new Gson().toJson(checkinVo));
	}
	
	
	public  static String createMD5Token(Context ctx){
		String token = DeviceInfoUtil.getDeviceId(ctx)
				+ "_" + URL.APPKEY
				;
		return MD5Util.toMD5(token);
	}
	    
}
