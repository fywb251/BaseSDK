package com.zdnst.chameleon.util;

import java.util.UUID;

import android.content.Context;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceInfoUtil {

	/**
	 * deviceid
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		String androidid = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String tmDevice, tmSerial;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();

		Log.d("cube", " androidid=" + androidid + " tmDevice=" + tmDevice
				+ " tmSerial=" + tmSerial);

		if (androidid == null) {
			androidid = "";
		}
		if (tmSerial == null) {
			tmSerial = "";
		}

		Log.d("cube", " androidid=" + androidid + " tmDevice=" + tmDevice
				+ " tmSerial=" + tmSerial);

		UUID deviceUuid = new UUID(androidid.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		String uniqueId = deviceUuid.toString();
		Log.d("cube", "uniqueId = " + uniqueId);
		return uniqueId;
		// url = URLEncoder.encode(url, HttpRequestAsynTask.QUERY_ENCODING);
		// String encryptedUniqueId = DESEncryptAndGzipUtil.encrypt(uniqueId)
		// .trim();
		// try {
		// encryptedUniqueId = URLEncoder.encode(encryptedUniqueId,
		// HttpUtil.ENCODING);
		// } catch (UnsupportedEncodingException e) {
		// e.printStackTrace();
		// Log.e("mytag", "deviceid url encode澶辫触��);
		// }
		// Log.d("mytag", "缂�����uniqueId=" + encryptedUniqueId);
		// return encryptedUniqueId;

	}

	
}
