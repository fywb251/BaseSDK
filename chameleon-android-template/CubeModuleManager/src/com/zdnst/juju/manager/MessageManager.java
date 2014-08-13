package com.zdnst.juju.manager;

import java.util.ArrayList;

import android.content.Context;

import com.zdnst.data.table.MessageDataModel;
import com.zdnst.juju.model.CubeModule;
import com.zdnst.juju.model.MessageModuleInfo;
import com.zdnst.module.MessageInfo;
import com.zdnst.push.url.MessageConstants;

public class MessageManager {
	
	private static MessageManager messageManager ;
	private static ArrayList<ArrayList<MessageInfo>> list = new ArrayList<ArrayList<MessageInfo>>();
	
	private MessageManager() {
		
	}

	public static MessageManager getInstance() {
		if(messageManager== null) {
			messageManager = new MessageManager();
		}
		return messageManager;
	}
	//添加一条记录
	public void addMessageInfo(Context context,MessageInfo Info) {
		MessageDataModel messageDataModel = new MessageDataModel(context);
		messageDataModel.addMessageInfo(Info);
	}
	//删
	public void deleteMessageInfo(Context context,String messageId){
		MessageDataModel messageDataModel = new MessageDataModel(context);
		messageDataModel.deleteMessageInfo(messageId);
		deleteMessageInfoByid(messageId);
	}
	
	public void deleteMessageInfobyidentifier(Context context,String identifier) {
		if(identifier != null&& !identifier.equals("")) {
			MessageDataModel messageDataModel = new MessageDataModel(context);
			ArrayList<MessageInfo> list = messageDataModel.getAllMessageInfoByIdentifier(identifier);
			if(list!=null) {
				messageDataModel.deleteMessageInfoList(list);
				list.remove(list);
			}
		}
	}
	
	public ArrayList<MessageModuleInfo> getAllMessageInfo(Context context) {
		list.clear();
		ArrayList<MessageModuleInfo> date =new ArrayList<MessageModuleInfo>();
		MessageDataModel messageDataModel = new MessageDataModel(context);
		ArrayList<String> identifiers = messageDataModel.getAllIdentifier();
		for (String  identifier : identifiers) {
			ArrayList<MessageInfo>  infoList = messageDataModel.getAllMessageInfoByIdentifier(identifier);
			MessageModuleInfo  info = new MessageModuleInfo(infoList);
			date.add(info);
			if (infoList.size() > 0){
				list.add(infoList);
			}
		}
		return date;
	}
	
	public void  openModule(Context context,String messageid) {
		getAllMessageInfo(context);
		MessageInfo info = getMessageInfoByid(messageid);
		CubeModule module = CubeModuleManager.getInstance().getModuleByIdentify(info.getIdentifier());
		if(module !=null) {
			if(!module.getIdentifier().equals( MessageConstants.MESSAGE_IDENTIFIER)) {
				context.startActivity(CubeModuleManager.getInstance().showModule(context,module));
			}
		}
	}
	
	public void mart(Context context,String messageid) {
		
		MessageInfo info = getMessageInfoByid(messageid);
		if(info!=null) {
			info.setHasread(true);
			MessageDataModel messageDataModel = new MessageDataModel(context);
			messageDataModel.updateInfo(info);
		}
	}
	
	public MessageInfo getMessageInfoByid(String messageid) {
		MessageInfo info = null;
		if(messageid !=null) {
			for(int i =0;i<list.size();i++) {
				for(int j =0;j<list.get(i).size();j++) {
					if(messageid.equals(list.get(i).get(j).getMesssageid())) {
						info = list.get(i).get(j);
					}
				}
			}
		}
		return info;
	}
	public MessageInfo deleteMessageInfoByid(String messageid) {
		MessageInfo info = null;
		if(messageid !=null) {
			for(int i =0;i<list.size();i++) {
				for(int j =0;j<list.get(i).size();j++) {
					if(messageid.equals(list.get(i).get(j).getMesssageid())) {
						list.get(i).remove(j);
					}
				}
			}
		}
		return info;
	}
	

}
