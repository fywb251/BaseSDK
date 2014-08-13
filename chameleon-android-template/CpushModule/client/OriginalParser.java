package com.zdnst.push.client;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zdnst.bsl.CmanagerModuleActivity;
import com.zdnst.bsl.manager.BroadCastManager;
import com.zdnst.bsl.manager.CubeModuleManager;
import com.zdnst.bsl.model.CubeModule;
import com.zdnst.bsl.util.BroadcastConstans;
import com.zdnst.chameleon.AppStatus;
import com.zdnst.chameleon.MessageActivity;
import com.zdnst.chameleon.MessageFragmentModel;
import com.zdnst.chameleon.manager.R;
import com.zdnst.chameleon.push.cubeparser.type.AbstractMessage;
import com.zdnst.chameleon.push.cubeparser.type.BaseModel;
import com.zdnst.chameleon.push.cubeparser.type.ChanmeleonMessage;
import com.zdnst.chameleon.push.cubeparser.type.CommonModuleMessage;
import com.zdnst.chameleon.push.cubeparser.type.NoticeModuleMessage;
import com.zdnst.chameleon.push.cubeparser.type.PatchMessageModelEvent;
import com.zdnst.chameleon.push.cubeparser.type.SystemMessage;
import com.zdnst.chameleonsdk.config.CubeConstants;
import com.zdnst.chameleonsdk.config.URL;
import com.zdnst.data.table.MessageDataModel;
import com.zdnst.data.table.MessageStubDataModel;
import com.zdnst.module.MessageInfo;
import com.zdnst.push.tool.PropertiesUtil;
import com.zdnst.push.url.MessageConstants;
import com.zdnst.router.MappingModel;
import com.zdnst.router.RoutingParserHelper;

public class OriginalParser implements PacketListener{

	private Context context;
	private DelayQueue<Delayed> delayQueue = new DelayQueue<Delayed>();
	private PropertiesUtil propertiesUtil;
	public  Intent intent = null;
	public OriginalParser(final Context context) {
		propertiesUtil = PropertiesUtil.readProperties(context, CubeConstants.CUBE_CONFIG);
		this.context = context;
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
				List<Delayed> subBuffer = null;
				synchronized (OriginalParser.this) {
					subBuffer = new ArrayList<Delayed>(buffer);
					buffer.clear();
				}
				PatchMessageModelEvent messageModelEvent = new PatchMessageModelEvent();
				PatchNoticeModelEvent noticModelEvent = new PatchNoticeModelEvent();
				ArrayList<String> identifierlist = new ArrayList<String>();
				for (Delayed delayed : subBuffer) {
					ChanmeleonMessage chanmeleonMessage = ChanmeleonMessage.class
							.cast(delayed);
					if (chanmeleonMessage.savable()) {
						MessageInfo messageInfo = chanmeleonMessage
								.getPackedMessage();
						
						if (messageInfo.getTitle().equals(MessageConstants.MESSAGE_TYPE_SECURITY_CONTENT)){
							if (AppStatus.USERLOGIN){
								context.sendBroadcast(new Intent(BroadcastConstans.SecurityChange));
							} else {
								continue;
							}
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
						MessageStubDataModel messageStubDataModel = new MessageStubDataModel(context);
						messageStubDataModel.addMessageInfo(messageInfo);
					}
					messageModelEvent
					.addChanmeleonMessage(chanmeleonMessage);
				}

				if (!messageModelEvent.isEmpty()) {
					delayQueue.add(messageModelEvent);
				}
				if (!noticModelEvent.isEmpty()) {
					delayQueue.add(noticModelEvent);
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




	/**
	 * [锟斤拷锟藉Ο��筹拷娑�锟介�寸��锟斤拷锟斤拷<BR>
	 * [锟斤拷锟界��锟斤拷锟斤拷���]
	 * 
	 * @param moduleContent
	 *            2013-8-22 娑�锟斤拷12:01:52
	 */
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

	private List<Delayed> buffer = Collections
			.synchronizedList(new ArrayList<Delayed>());


	@Override
	public void processPacket(Packet packet) {
		System.out.println("packet"+packet.toXML());
		if (packet instanceof Message) {
			Message message = Message.class.cast(packet);
			
				synchronized (OriginalParser.this) {
					try {
						buffer.addAll(NotificationPushContent.parseRemoteModel(message,context));
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
		}

		
	}

}
