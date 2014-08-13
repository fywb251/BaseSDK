package com.zdnst.zillasdk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.zdnst.bsl.util.Preferences;
import com.zdnst.bsl.util.PropertiesUtil;
import com.zdnst.chameleon.httputil.HttpRequestAsynTask;
import com.zdnst.chameleon.httputil.HttpUtil;
import com.zdnst.chameleon.util.DeviceInfoUtil;
import com.zdnst.zdnstsdk.config.CubeConstants;
import com.zdnst.zdnstsdk.config.URL;

public class Zilla {

	private static Zilla zilla = null;

	public static Zilla getZilla() {
		if (zilla == null) {
			zilla = new Zilla();
		}
		return zilla;
	}

	public void appAuth(Context context, final ZillaDelegate callback) {

		HttpRequestAsynTask loginTask = doHttp(context, callback);
		loginTask.setDialogContent("正在验证...");
		loginTask.setLockScreen(true);
		loginTask.setShowProgressDialog(true);
		loginTask.setNeedProgressDialog(true);
		StringBuilder sb = new StringBuilder();
		String appKey = PropertiesUtil.readProperties(context,
				CubeConstants.CUBE_CONFIG).getString("appKey", "");
		String secret = PropertiesUtil.readProperties(context,
				CubeConstants.CUBE_CONFIG).getString("secret", "");
		sb = sb.append("Form:appKey=").append(appKey).append(";secret=")
				.append(secret);
		String s = sb.toString();
		String url = URL.AUTH;
		loginTask.execute(url, s, HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_POST);
		// loginTask.run(url, new Setting(HttpUtil.HTTP_POST, s, HTTP, timeout))
	}

	public void syncModule(Context context, final ZillaDelegate callback,
			boolean dialogNeed, final String dialogContent) {
		HttpRequestAsynTask task = doHttp(context, callback);
		String token = Preferences.getToken();
		if (token.equals("")) {
			Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
			return;
		}
		long time = new Date().getTime();
		String url = URL.BASE_WS + "mam/api/mam/clients/apps/modules/" + token
				+ "?timeStamp=" + time;
		System.out.println("网络请求url == " + url);
		task.setLockScreen(false);
		task.setDialogContent(dialogContent);
		task.setShowProgressDialog(dialogNeed);
		task.setNeedProgressDialog(dialogNeed);
		task.run(url);
	}

	public void syncPrivilege(Context context, ZillaDelegate callback,
			String username, boolean dialogNeed, final String dialogContent) {
		HttpRequestAsynTask task = doHttp(context, callback);
		task.setDialogContent(dialogContent);
		task.setShowProgressDialog(dialogNeed);
		task.setNeedProgressDialog(dialogNeed);
		String url = URL.BASE_WS + "mam/api/mam/clients/apps/"
				+ URL.APP_PACKAGENAME + "/" + username + "/auth?" + "appKey="
				+ URL.APPKEY;
		task.run(url);
	}

	public void snapshot(Context context, ZillaDelegate callback,
			String appKey, String identifier, String version) {
		HttpRequestAsynTask task = doHttp(context, callback);
		task.setShowProgressDialog(false);
		task.setNeedProgressDialog(false);
		String url = URL.SNAPSHOT + identifier + "/" + version
				+ "/snapshot?appKey=" + appKey;
		Log.i("ljltest", "url = " + url);
		task.run(url);
	}

	public void pushCheckIn(Context context, final ZillaDelegate callback,
			final String checkInVoJson) {
		new AsyncTask<String, Integer, Boolean>() {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				callback.requestStart();
			}

			@Override
			protected void onPostExecute(Boolean result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (result) {
					callback.requestSuccess("success");
				} else {
					callback.requestFailed("error");
				}
			}

			@Override
			protected Boolean doInBackground(String... arg0) {
				HttpClient httpClient = new DefaultHttpClient();
				HttpPut httpPut = new HttpPut(URL.CHECKIN_URL);
				try {
					httpPut.addHeader("Accept", "application/json");
					httpPut.addHeader("Content-Type", "application/json");
					httpPut.setEntity(new StringEntity(checkInVoJson, "utf-8"));
					HttpResponse httpResponse = httpClient.execute(httpPut);
					int statusCode = httpResponse.getStatusLine()
							.getStatusCode();

					Log.d("openfire Client", "签到 code == " + statusCode);
					if (statusCode == HttpStatus.SC_OK) {
						Log.d("openfire Client", "openfire 签到成功");
					}
					return true;
				} catch (ClientProtocolException e) {
					Log.e("openfire Handler", "MessageContentHandler", e);
					return false;
				} catch (IOException e) {
					Log.e("openfire Handler", "MessageContentHandler", e);
					return false;
				}
			}

		}.execute();

	}

	public void pushGetMessage(Context context, String deviceId, String appId,
			ZillaDelegate callback) {
		callback.requestStart();
		HttpResponse response = null;
		try {
			String getMessageUrl = URL.GETPUSHMESSAGE + deviceId + "/" + appId;
			HttpGet getMethod = new HttpGet(getMessageUrl);
			HttpClient httpClient = new DefaultHttpClient();
			response = httpClient.execute(getMethod);
			System.out.println("拉取推送信息的URL === " + getMethod.getURI());
			String result = null;
			result = EntityUtils.toString(response.getEntity(), "utf-8");
			callback.requestSuccess(result);
		} catch (ClientProtocolException e1) {
			callback.requestFailed(e1.getMessage());
			e1.printStackTrace();
		} catch (IOException e1) {
			callback.requestFailed(e1.getMessage());
			e1.printStackTrace();
		}
	}

	public void pushReceived(Context context, String msgId,
			ZillaDelegate callback) {

		callback.requestStart();
		HttpClient httpClient = new DefaultHttpClient();

		HttpPut httpPut = new HttpPut(URL.FEEDBACK_URL);
		try {
			httpPut.addHeader("Content-Type",
					"application/x-www-form-urlencoded");
			HttpEntity httpEntity = null;
			String appKey = URL.APPKEY;
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("deviceId", DeviceInfoUtil
					.getDeviceId(context)));
			list.add(new BasicNameValuePair("msgId", msgId));
			list.add(new BasicNameValuePair("appId", appKey));
			httpEntity = new UrlEncodedFormEntity(list);
			httpPut.setEntity(httpEntity);
			HttpResponse httpResponse = httpClient.execute(httpPut);

			callback.requestSuccess(httpResponse.getEntity().getContent()
					.toString());
			if (httpResponse.getStatusLine().getStatusCode() < 299
					&& httpResponse.getStatusLine().getStatusCode() >= 200) {
			} else {
			}
		} catch (ClientProtocolException e) {
			callback.requestFailed(e.getMessage());
			Log.e("MessageContentHandler", "MessageContentHandler", e);
		} catch (IOException e) {
			callback.requestFailed(e.getMessage());
			Log.e("MessageContentHandler", "MessageContentHandler", e);
		}
	}

	private HttpRequestAsynTask doHttp(Context context,
			final ZillaDelegate callback) {
		HttpRequestAsynTask task = new HttpRequestAsynTask(context) {
			@Override
			public void doPreExecuteBeforeDialogShow() {

				super.doPreExecuteBeforeDialogShow();
				callback.requestStart();
			}

			@Override
			public void doPreExecuteWithoutDialog() {

				super.doPreExecuteWithoutDialog();
				callback.requestStart();
			}

			@Override
			protected void doPostExecute(String result) {
				super.doPostExecute(result);
				callback.requestSuccess(result);
			}

			@Override
			protected void doHttpFail(Exception e) {

				super.doHttpFail(e);
				callback.requestFailed(e.getMessage());
			}
		};
		return task;
	}

}
