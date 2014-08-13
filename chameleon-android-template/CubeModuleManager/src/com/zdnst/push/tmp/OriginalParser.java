package com.zdnst.push.tmp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.foreveross.chameleon.manager.R;
import com.google.gson.Gson;
import com.zdnst.bsl.util.BroadcastConstans;
import com.zdnst.chameleon.AppStatus;
import com.zdnst.chameleon.util.DeviceInfoUtil;
import com.zdnst.data.table.MessageDataModel;
import com.zdnst.juju.CmanagerModuleActivity;
import com.zdnst.juju.manager.CubeModuleManager;
import com.zdnst.juju.model.CubeModule;
import com.zdnst.juju.model.MessageModuleInfo;
import com.zdnst.message.push.cubeparser.type.ChanmeleonMessage;
import com.zdnst.message.push.cubeparser.type.PatchMessageModelEvent;
import com.zdnst.module.MessageInfo;
import com.zdnst.push.NotificationPushContent;
import com.zdnst.push.client.BaseParser;
import com.zdnst.push.client.Constants;
import com.zdnst.push.client.Notifier;
import com.zdnst.push.url.MessageConstants;
import com.zdnst.router.MappingModel;
import com.zdnst.router.RoutingParserHelper;
import com.zdnst.zdnstsdk.config.URL;
import com.zdnst.zillasdk.Zilla;
import com.zdnst.zillasdk.ZillaDelegate;
//import com.zdnst.chameleonsdk.config.CubeConstants;
//import com.zdnst.push.tool.PropertiesUtil;

/**
 * @author apple
 *
 */
public class OriginalParser extends BaseParser{

//	private Context context;
	private DelayQueue<Delayed> delayQueue = new DelayQueue<Delayed>();
	public  Intent intent = null;
	public OriginalParser(final Context context) {
		super(context);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Delayed delayed = delayQueue.take();
						if (delayed instanceof ChanmeleonMessage) {
						} else {
							sendMessage((PatchMessageModelEvent)(delayed));
						}

					} catch (InterruptedException e) {
						Log.e("","take queue error!"+ e);
					}
				}
			}
		}).start();

		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
								
				if (buffer.isEmpty()) {
					return;
				}
				ArrayList<MessageModuleInfo> list = new ArrayList<MessageModuleInfo>();
				List<Delayed> subBuffer = null;
				synchronized (OriginalParser.this) {
					subBuffer = new ArrayList<Delayed>(buffer);
					buffer.clear();
				}
				PatchMessageModelEvent messageModelEvent = new PatchMessageModelEvent();
				ArrayList<String> identifierlist = new ArrayList<String>();
				for (Delayed delayed : subBuffer) {
					ChanmeleonMessage chanmeleonMessage = ChanmeleonMessage.class
							.cast(delayed);
					if (chanmeleonMessage.savable()) {
						MessageInfo messageInfo = chanmeleonMessage
								.getPackedMessage();
						
						if (MessageConstants.MESSAGE_TYPE_SECURITY_CONTENT.equals(messageInfo.getModuleName())){
							
							String roles = messageInfo.getModuleurl();
							if (MessageConstants.MESSAGE_SECURITY_ROLES.equals(roles)){
								// 退出登录
								if (AppStatus.USERLOGIN) {
									context.sendBroadcast(new Intent(
											BroadcastConstans.SecurityRoleChange));
								} else {
									continue;
								}
								
								
							} else if (MessageConstants.MESSAGE_SECURITY_PRIVILEGE.equals(roles)){
								context.sendBroadcast(new Intent(BroadcastConstans.SecurityChange));
							}
							
//							if (AppStatus.USERLOGIN){
//								context.sendBroadcast(new Intent(BroadcastConstans.SecurityChange));
//							} else {
//								continue;
//							}
						}
						String identifier = messageInfo.getIdentifier();
						if (!identifierlist.contains(identifier)){
							identifierlist.add(identifier);
						}
						CubeModule module = CubeModuleManager.getInstance()
								.getCubeModuleByIdentifier(identifier);
						
//						BroadCastManager.receiveMessage(context, module);
						MessageDataModel messageDataModel = new MessageDataModel(context);
						messageDataModel.addMessageInfo(messageInfo);
						MessageModuleInfo m  = new MessageModuleInfo(messageInfo);
						list.add(m);
						String json =new Gson().toJson(list);
						System.out.println("getPushMessage = "+json);
						Intent in = new Intent(BroadcastConstans.ReceiveMessages);
						in.putExtra("message", json);
						context.sendBroadcast(in);
					}
					messageModelEvent.addChanmeleonMessage(chanmeleonMessage);
				}

				if (!messageModelEvent.isEmpty()) {
					delayQueue.add(messageModelEvent);
				}
				//通知界面进行界面刷新
				for (String string : identifierlist) {
					Intent intent = new Intent(BroadcastConstans.ReceiveMessage);
					intent.putExtra("identifier", string);
					context.sendBroadcast(
							new Intent(intent));
				}
			}
		}, 0, 3000);
	}

	private List<Delayed> buffer = Collections
			.synchronizedList(new ArrayList<Delayed>());


	@Override
	public void onReceive(Packet packet) {
		System.out.println("packet"+packet.toXML());
		if (packet instanceof Message) {
			Message message = Message.class.cast(packet);
				
				synchronized (OriginalParser.this) {
					pushReceiveMessage(context);
				}
		}
		
	}


	public void sendMessage(PatchMessageModelEvent pathMessageModelEvent ) {
		if (isWork()) {
			sendMessageNotification(pathMessageModelEvent);
		} else {
			startApp(pathMessageModelEvent);
		}
	}
	public void startApp(PatchMessageModelEvent patchMessageModelEvent) {
		
		intent = new Intent();
		MappingModel mappingModel;
		String className = "";
		String moduleUrl = patchMessageModelEvent.lastChanmeleonMessage().getPackedMessage().getModuleurl();
		String moduleIdentifer = patchMessageModelEvent.lastChanmeleonMessage().getPackedMessage().getIdentifier();
		String []value = null ;
			RoutingParserHelper r = new RoutingParserHelper();
			mappingModel = r.redirectToPage(moduleUrl,moduleIdentifer);
			if(mappingModel!=null) {
				String[] moduleUrlList = moduleUrl.substring(1).split("/");
				value = replacelist(moduleUrlList, mappingModel.getLinkURL());
				className = mappingModel.getPageIdentifier();
			}
		if(className!=null&&!className.equals("")) {
			if(value!=null&&!value.equals("")) {
				intent.putExtra("parameters", value);
			}
		}else {
			intent.putExtra("parameters", moduleUrl);
		}
		
		intent.putExtra("moduleIdentifier", moduleIdentifer);
		intent.setClass(context, CmanagerModuleActivity.class);
		ChanmeleonMessage chanmeleonMessage = patchMessageModelEvent.lastChanmeleonMessage();
		Notifier.notifyInfo(context, R.drawable.appicon,
				Constants.ID_MESSAGE_NOTIFICATION, chanmeleonMessage
				.getPackedMessage().getTitle(),
				chanmeleonMessage.getPackedMessage().getContent(),intent);
	}
	//判断程序是否已打开
	public Boolean isWork() {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(100);
		boolean isAppRunning = false;
		String MY_PKG_NAME = URL.APP_PACKAGENAME;
		for (RunningTaskInfo info : list) {
			if (info.topActivity.getPackageName().equals(MY_PKG_NAME) || info.baseActivity.getPackageName().equals(MY_PKG_NAME)) {
				isAppRunning = true;
				break;
			}
		}
		return isAppRunning;
	}
	//模块跳转
	private void sendMessageNotification(final PatchMessageModelEvent patchMessageModelEvent ) {
		intent = new Intent();
		String moduleUrl = patchMessageModelEvent.lastChanmeleonMessage().getPackedMessage().getModuleurl();
		String moduleId = patchMessageModelEvent.lastChanmeleonMessage().getPackedMessage().getIdentifier();
		CubeModule cubeModule =CubeModuleManager.getInstance().getModuleByIdentify(moduleId);
		//验证模块是否存在，是否有权限，不存在跳消息盒子
		intent.putExtra("moduleIdentifier", moduleId);
		intent.setClass(context, CmanagerModuleActivity.class);
		if(cubeModule == null) {
			intent.putExtra("moduleIdentifier", MessageConstants.MESSAGE_IDENTIFIER);
		}else {//本地模块
			if(cubeModule.getLocal() !=null) {
				intent = localjump(moduleUrl,intent,moduleId);
			}else {//html模块
				intent.putExtra("parameters", moduleUrl);
				//intent = CubeModuleManager.getInstance().showModule(context,cubeModule);
			}
		}
		
		ChanmeleonMessage chanmeleonMessage = patchMessageModelEvent.lastChanmeleonMessage();
		Notifier.notifyInfo(context,
				R.drawable.appicon,
				Constants.ID_MESSAGE_NOTIFICATION, 
				chanmeleonMessage.getPackedMessage().getTitle(),
				chanmeleonMessage.getPackedMessage().getContent(),
				intent);

	}
	//本
	public Intent localjump(String moduleUrl,Intent intent,String moduleId) {
//		if(moduleUrl ==null||moduleUrl.equals("")) {
//			intent.setClass(context, MessageActivity.class);
//		}
		RoutingParserHelper r = new RoutingParserHelper();
		MappingModel mappingModel = r.redirectToPage(moduleUrl,moduleId);
		if(mappingModel ==null) {
			 mappingModel = r.redirectToPage("/index",moduleId);	
		}
		String className = "";
		String []parameters = null; 
		if (mappingModel != null) {
			if(moduleUrl != null&&!moduleUrl.equals("")) {
				String[] moduleUrlList = moduleUrl.substring(1).split("/");
				parameters = replacelist(moduleUrlList,mappingModel.getLinkURL());
			}
			className = mappingModel.getPageIdentifier();
			if (className != null && !className.equals("")) {
				if (parameters != null && !parameters.equals("")) {
					intent.putExtra("parameters", parameters);
				}
				intent.putExtra("className", className);
			}
		}
		return intent;
	}
	
	public String  replaceString (String moduleUrls, String linkUrl) {
		String linkUrls= moduleUrls.replace(linkUrl, "");
		 return linkUrls;
	}
	
	
	public String [] replacelist(String [] moduleUrls,String [] linkUrl) {
		int count =0;
		String [] value;
		if(moduleUrls.length >linkUrl.length) {
			count =moduleUrls.length - linkUrl.length;
			value = new String[count];
			for(int i =0;i<value.length;i++) {
				int moduleCount = moduleUrls.length;
				value[i] = moduleUrls[moduleCount-count+i];
			}
			return value;
		}else {
			return value =null;
		}
		
		
	}

	
	
	
	
	/**
	 * 接收推送拉取消息
	 * @param context
	 * @param msgId
	 */
	public void pushReceiveMessage(final Context context){
		try {
			//接收推送时拉取消息
			String deviceId = DeviceInfoUtil.getDeviceId(context);
			String appId = URL.APPKEY;
			Zilla.getZilla().pushGetMessage(context, deviceId, appId, new ZillaDelegate() {
				
				@Override
				public void requestSuccess(String result) {
					try {
						//拉取消息后解析成对象模型
						List<Delayed> messageContent = NotificationPushContent.parseRemoteModel(result,context); 
						buffer.addAll(messageContent);
						

						//发送回执
						StringBuffer sendIDs = new StringBuffer();
						for(Delayed d:messageContent){
							ChanmeleonMessage msg = (ChanmeleonMessage)d;
							String sendid = msg.getPackedMessage().getMesssageid();
							if(sendid!=null&&!sendid.equals("")){
								sendIDs.append(sendid+",");
							}
						}
						if(sendIDs.length()!=0){
							receiptsMessage(context,sendIDs.substring(0,sendIDs.length()-1));
						}
						
						
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void requestStart() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void requestFailed(String errorMessage) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 发送回执至服务器
	 * @param context
	 * @param msgId
	 */
	public void receiptsMessage(Context context, String msgId) {
		Zilla.getZilla().pushReceived(context, msgId, new ZillaDelegate() {
			
			@Override
			public void requestSuccess(String result) {
				
			}
			
			@Override
			public void requestStart() {
				
			}
			
			@Override
			public void requestFailed(String errorMessage) {
				
			}
		});
	}
}
