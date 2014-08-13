package com.zdnst.push.client;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jivesoftware.smack.packet.Message;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.zdnst.chameleon.push.cubeparser.type.ChanmeleonMessage;
import com.zdnst.chameleonsdk.config.URL;
import com.zdnst.module.MessageInfo;
import com.zdnst.push.tool.DeviceInfoUtil;
import com.zdnst.push.url.MessageConstants;

public class NotificationPushContent {
	/**
	 * title:"", content:"", messageType:1, sendId:"", sendTime:"xx", extras:{
	 * moduleIdentifer:"", //模块的identifer，例如，公告模块为com.foss.announcement ...
	 * //根据不同模块自身需求，定义自已需要的键值，例如，公告模块定义announceId表示公告ID announceId:""
	 * //公告模块的公告ID }
	 * */
	 static String getMessageUrl;
	 
	 public static List<Delayed> parseRemoteModel(final Message message,final 	Context context) throws SQLException {
		final List<Delayed> l = new ArrayList<Delayed>();
		
			String deviceId = DeviceInfoUtil.getDeviceId(context);
			String appId = URL.APPKEY;
			
			
					String sendid = "";
					HttpResponse response = null;
					try {
						
						getMessageUrl = URL.GETPUSHMESSAGE+deviceId+"/"+appId;
//						getMessageUrl = URL.PUSH_BASE_URL+"receipts/none-receipts/" + tokenId + "/" + deviceId + "/"+ appId;
						HttpGet getMethod = new HttpGet(getMessageUrl);
						HttpClient httpClient = new DefaultHttpClient();
						response = httpClient.execute(getMethod);
						System.out.println("拉取推送信息的URL === "+getMethod.getURI());
					} catch (ClientProtocolException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();	
					} // 发起GET请求					
					String result = null;
					try {
						result = EntityUtils.toString(response.getEntity(),"utf-8");
						if(result == null||result.equals("[]")) {
							return null;
						}
						
						try
						{
							JSONArray jsonArray = new JSONArray(result);
							for (int j = 0; j < jsonArray.length(); j++)
							{
								JSONObject jsonObject = jsonArray.getJSONObject(j);
								
								String messsageId = jsonObject.getString("id");
//								if(jsonObject.isNull("sendId"))continue;
//								String messsageId = jsonObject.getString("sendId");
								String title = jsonObject.getString("title");
								String content = jsonObject.getString("content");
								String messageType = jsonObject.getString("messageType");
	//							String sendTime =jsonObject.getString ("sendTime")
								long sendTime = System.currentTimeMillis();
								if(messsageId!=null&&!messsageId.equals("") ) {
									sendid += messsageId+",";
								}
//								if(String.valueOf(StaticReference.userMf.queryBuilder(NoticeModuleMessage.class).where().eq("messsageId",messsageId).countOf())!=null) {
//									return null;
//								}
								ChanmeleonMessage messageDelay = null;
								String moudleUrl = "";
								//系统信息
								if (MessageConstants.MESSAGE_TYPE_SYSTEM.equals(messageType) || MessageConstants.MESSAGE_TYPE_SECURITY.equals(messageType)) {
									if(!jsonObject.isNull("extras")) {
										JSONObject jsonObjectExtras = new JSONObject(jsonObject.getString("extras"));
										if(!jsonObjectExtras.isNull("moduleUrl")) {
											moudleUrl= jsonObjectExtras.getString("moduleUrl");
										}
									}
									MessageInfo messageInfo = new MessageInfo();
									messageInfo.setModuleurl(moudleUrl);
									messageInfo.setMesssageid(messsageId);
									messageInfo.setSendtime(sendTime);
									messageInfo.setTitle(title);
									messageInfo.setContent(content);
									messageInfo.setIdentifier(MessageConstants.MESSAGE_IDENTIFIER);
									messageInfo.setGroupBelong(MessageConstants.MESSAGE_SYSTEM_NAME);
									messageDelay = new ChanmeleonMessage(messageInfo);
								}
								//模块信息
								else if (MessageConstants.MESSAGE_TYPE_MODULE.equals(messageType)) {
									
									JSONObject jsonObjects = new JSONObject(jsonObject.getString("extras"));
									String moduleIdentifer =jsonObjects.getString("moduleIdentifer");
									String moduleName = jsonObjects.getString("moduleName");;
									if(!jsonObjects.isNull("moduleUrl")) {
										moudleUrl= jsonObjects.getString("moduleUrl");
									}
									boolean moduleBadgeBool = true;
									boolean busiDetailBool = true;
									
									try {
										String moduleBadge =jsonObjects.getString("moduleBadge") ;
										
										moduleBadgeBool = moduleBadge == null ? false : Boolean
												.valueOf(moduleBadge);
										String busiDetail = jsonObjects.getString("busiDetail");
										busiDetailBool = busiDetail == null ? false : Boolean
												.valueOf(busiDetail);
										
									} catch (Exception e) {
										e.printStackTrace();
									}
									
										MessageInfo messageInfo = new MessageInfo();
										messageInfo.setSendtime(sendTime);
										messageInfo.setMesssageid(messsageId);
										messageInfo.setTitle(title);
										messageInfo.setContent(content);
										messageInfo.setModuleurl(moudleUrl);
										messageInfo.setIdentifier(moduleIdentifer);
										messageInfo.setGroupBelong(MessageConstants.MESSAGE_TYPE_MODULE);
										String groupBelong = moduleName == null ? moduleIdentifer
												: moduleName;
										messageInfo.setGroupBelong(groupBelong);
										messageInfo.setLinkable(busiDetailBool);
										messageDelay = new ChanmeleonMessage(messageInfo);
//									if(messageDelay != null){
//										ModuleMessage.class.cast(messageDelay.getPackedMessage())
//										.setLinkable(busiDetailBool);
//									}
									
								}
								if (messageDelay != null) {
									l.add(messageDelay);
								}
							}
							///		do something recode;
							receiptsMessage(context,sendid);
							sendid = "";
							
						}
						catch (JSONException e)
						{
							e.printStackTrace();
						}
						
					} catch (ParseException e2) {
						e2.printStackTrace();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
		return l;
		
		
		
		
	}
	 //婵���烽��ゆ����锟斤拷锟斤拷��拷�斤拷锟斤拷锟姐�锟界�锟斤拷锟斤拷��拷�斤拷锟斤拷锟姐�锟斤拷
	public static void receiptsMessage(Context context, String t) {
//		System.out.println("锟斤拷锟斤拷锟姐�锟界�锟斤拷锟斤拷��拷�斤拷锟界�锟介��ゆ�����凤拷锟斤拷锟斤拷��拷�斤拷锟斤拷锟姐�锟界�锟斤拷锟斤拷��拷�斤拷锟斤拷锟姐�锟界�锟斤拷��拷");
		HttpClient httpClient = new DefaultHttpClient();

		HttpPut httpPut = new HttpPut(URL.FEEDBACK_URL);
		try {
			// httpPost.addHeader("Accept", "application/json");
			httpPut.addHeader("Content-Type",
					"application/x-www-form-urlencoded");
			// FeedbackVo feedbackVo = new FeedbackVo();
			// feedbackVo.setDeviceId(DeviceInfoUtil.getDeviceId(context));
			// feedbackVo.setSendId(t.getId());
			HttpEntity httpEntity = null;
			// httpEntity = new
			// StringEntity(gson.toJson(feedbackVo),"utf-8");
			String appKey = URL.APPKEY;
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			list.add(new BasicNameValuePair("deviceId", DeviceInfoUtil.getDeviceId(context)));
//			list.add(new BasicNameValuePair("sendId", t));
			list.add(new BasicNameValuePair("msgId", t));
			list.add(new BasicNameValuePair("appId", appKey));
			httpEntity = new UrlEncodedFormEntity(list);
			httpPut.setEntity(httpEntity);
			HttpResponse httpResponse = httpClient.execute(httpPut);
			System.out.println("锟斤拷锟斤拷锟姐�锟界�锟斤拷锟斤拷��拷�斤拷锟斤拷锟姐�锟斤拷url:"+httpPut.getURI());
	
			
			if (httpResponse.getStatusLine().getStatusCode()<299&&httpResponse.getStatusLine().getStatusCode()>=200) {
//				System.out.println("锟斤拷锟斤拷锟姐�锟界�锟斤拷锟斤拷��拷�斤拷锟界�锟介�浠�拷锟斤拷锟斤拷���锟斤拷锟斤拷�斤拷锟斤拷锟姐��凤拷锟斤拷锟斤拷�斤拷锟斤拷锟姐�锟界�锟斤拷锟斤拷��拷�斤拷锟斤拷锟姐�锟藉��烽��ゆ�锟斤拷锟介�锟�+httpResponse.getStatusLine().getStatusCode()+"锟斤拷锟斤拷锟姐�锟藉��烽�瑙ｏ拷锟斤拷锟界�锟斤拷锟斤拷��拷�斤拷锟斤拷锟姐�锟界�锟斤拷锟斤拷��拷�ゆ����锟斤拷锟斤拷��拷�斤拷锟斤拷锟姐�锟界�锟斤拷娴�拷����凤拷锟斤拷��拷sendid =="+t);
			}else {
//				System.out.println("锟斤拷锟斤拷锟姐�锟界�锟斤拷锟斤拷��拷�斤拷锟界�锟介�浠�拷锟斤拷锟斤拷���锟斤拷锟斤拷�斤拷锟斤拷锟姐��凤拷锟斤拷锟斤拷�斤拷锟斤拷锟姐�锟界�锟斤拷锟斤拷��拷�斤拷锟斤拷锟姐�锟藉��烽��ゆ�锟斤拷锟介�锟�+httpResponse.getStatusLine().getStatusCode()+"锟斤拷锟斤拷锟姐�锟藉��烽�瑙ｏ拷锟斤拷锟界�锟斤拷锟斤拷��拷�斤拷锟斤拷锟姐�锟界�锟斤拷锟斤拷��拷�ゆ�����烽��ゆ�����烽��ゆ�����凤拷锟斤拷锟斤拷��拷锟�endid =="+t);
			}
		} catch (ClientProtocolException e) {
			Log.e("MessageContentHandler", "MessageContentHandler", e);
		} catch (IOException e) {
			Log.e("MessageContentHandler", "MessageContentHandler", e);
		}
	}


}
