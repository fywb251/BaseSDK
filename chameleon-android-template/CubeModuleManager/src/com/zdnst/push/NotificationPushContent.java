package com.zdnst.push;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.zdnst.message.push.cubeparser.type.ChanmeleonMessage;
import com.zdnst.module.MessageInfo;
import com.zdnst.push.url.MessageConstants;

public class NotificationPushContent {
	/**
	 * title:"", content:"", messageType:1, sendId:"", sendTime:"xx", extras:{
	 * moduleIdentifer:"", //模块的identifer，例如，公告模块为com.foss.announcement ...
	 * //根据不同模块自身需求，定义自已需要的键值，例如，公告模块定义announceId表示公告ID announceId:""
	 * //公告模块的公告ID }
	 * */
	 public static List<Delayed> parseRemoteModel(final String message,final 	Context context) throws SQLException {
		final List<Delayed> l = new ArrayList<Delayed>();
//		StringBuffer sendid = new StringBuffer();
		if (message == null || message.equals("[]")) {
			return l;
		}

		try {
			JSONArray jsonArray = new JSONArray(message);
			for (int j = 0; j < jsonArray.length(); j++) {
				JSONObject jsonObject = jsonArray.getJSONObject(j);

				String messsageId = jsonObject.getString("id");
				String title = jsonObject.getString("title");
				String content = jsonObject.getString("content");
				String messageType = jsonObject.getString("messageType");
				ChanmeleonMessage messageDelay = null;
				String moudleUrl = "";
				String securityMessage = "";
				// 系统信息
				if (MessageConstants.MESSAGE_TYPE_SYSTEM.equals(messageType)
						|| MessageConstants.MESSAGE_TYPE_SECURITY
								.equals(messageType)) {
					long sendTime = System.currentTimeMillis();
//					if (messsageId != null && !messsageId.equals("")) {
//						sendid.append(messsageId + ",");
//					}
					if (!jsonObject.isNull("extras")) {
						JSONObject jsonObjectExtras = new JSONObject(jsonObject.getString("extras"));
						if(!jsonObjectExtras.isNull("moduleUrl")) {
							moudleUrl= jsonObjectExtras.getString("moduleUrl");
						} else if (!jsonObjectExtras.isNull("securityKey")){
							moudleUrl= jsonObjectExtras.getString("securityKey");
							securityMessage = MessageConstants.MESSAGE_TYPE_SECURITY_CONTENT;
						}
					}
					MessageInfo messageInfo = new MessageInfo();
					messageInfo.setModuleName(securityMessage);
					messageInfo.setModuleurl(moudleUrl);
					messageInfo.setMesssageid(messsageId);
					messageInfo.setSendtime(sendTime);
					messageInfo.setTitle(title);
					messageInfo.setContent(content);
					messageInfo
							.setIdentifier(MessageConstants.MESSAGE_IDENTIFIER);
					messageInfo
							.setGroupBelong(MessageConstants.MESSAGE_SYSTEM_NAME);
					messageDelay = new ChanmeleonMessage(messageInfo);
				}
				// 模块信息
				else if (MessageConstants.MESSAGE_TYPE_MODULE
						.equals(messageType)) {
					long sendTime = System.currentTimeMillis();
//					if (messsageId != null && !messsageId.equals("")) {
//						sendid.append(messsageId + ",");
//					}
					JSONObject jsonObjects = new JSONObject(jsonObject.getString("extras"));
					String moduleIdentifer = jsonObjects.getString("moduleIdentifer");
					String moduleName = jsonObjects.getString("moduleName");
					
					if (!jsonObjects.isNull("moduleUrl")) {
						moudleUrl = jsonObjects.getString("moduleUrl");
					}
//					boolean moduleBadgeBool = true;
					boolean busiDetailBool = true;
					try {
//						String moduleBadge = jsonObjects.getString("moduleBadge");

//						moduleBadgeBool = moduleBadge == null ? false : Boolean.valueOf(moduleBadge);
						String busiDetail = jsonObjects.getString("busiDetail");
						busiDetailBool = busiDetail == null ? false : Boolean.valueOf(busiDetail);

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
					String groupBelong = moduleName == null ? moduleIdentifer: moduleName;
					messageInfo.setGroupBelong(groupBelong);
					messageInfo.setLinkable(busiDetailBool);
					messageDelay = new ChanmeleonMessage(messageInfo);

				}
				if (messageDelay != null) {
					l.add(messageDelay);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
				
		return l;
		
	}
	 
}
