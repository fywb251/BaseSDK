package com.zdnst.imsdk.chat.push;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

import com.zdnst.imsdk.chat.domain.IMMessage;

public class PushMessage {
	
	public static void sendMessage(IMMessage msg, final MessageSendListener listener)
	{
		
		new AsyncTask<String, Void, String>() {

			@Override
			protected String doInBackground(String... params) {
				HttpClient client = new DefaultHttpClient();
				HttpPut put = new HttpPut("");
				put.addHeader("Content-Type", "application/json");
				String sessionKey = "";
				put.addHeader("sessionKey", sessionKey);
				String reuslt = "";
				try {
					put.setEntity(new StringEntity("", "UTF-8"));
					HttpResponse response =  client.execute(put);
					reuslt = EntityUtils.toString(response.getEntity());
				} catch (UnsupportedEncodingException e) {
					listener.onError(e);
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					listener.onError(e);
					e.printStackTrace();
				} catch (IOException e) {
					listener.onError(e);
					e.printStackTrace();
				}
				return reuslt;
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				listener.onSuccess(result);
			}
		}.execute();
	}
	
	

}
