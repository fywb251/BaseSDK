package com.zdnst.juju.plugin;


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

import com.google.gson.Gson;
import com.zdnst.juju.manager.MessageManager;

public class MessagePlugin extends CordovaPlugin  {
	
 
	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {
		Log.i("messagePlugin", "action is =" + action);
		String result = null;
		if(action.equals("getAllMessages")) {
			result = new Gson().toJson(MessageManager.getInstance().getAllMessageInfo(cordova.getActivity()));
		}else if(action.equals("deleteMessages")) {
			String identifier = args.getString(0);
			if(isNull(identifier)){
			deleteByidentifier(identifier);
			callbackContext.success("");
			}
		}else if(action.equals("deleteMessage")) {
			String messageId = args.getString(0);
			if(isNull(messageId)){
			delete(messageId);
			callbackContext.success("");
			}
		}else if(action.equals("setMessageRead")) {
			String messageId = args.getString(0);
			if(isNull(messageId)){
			mart(messageId);
			callbackContext.success("");
			}
		}else if (action.equals("redirectMessagePage")) {
			String messageId = args.getString(0);
			if(isNull(messageId)){
				openMessage(messageId);
				callbackContext.success("");
			}
		}
		if (isNull(result)) {
			System.out.println("messagePlugin= "+result);
			callbackContext.success(result);
		}
		return super.execute(action, args, callbackContext);
	}
	//判空
	public Boolean isNull(String messageid){
		
		return messageid!=null&&!messageid.equals("");
	}
	
	
	//根据messageid 删除单条信息
	public void delete(String messageId) {
		
		MessageManager.getInstance().deleteMessageInfo(cordova.getActivity(),messageId);
	}
	//根据identifier 删除此组信息
	public void deleteByidentifier(String identifier) {
		MessageManager.getInstance().deleteMessageInfobyidentifier(cordova.getActivity(), identifier);
	}
	//标记已读
	public void  mart(String messageId) {
		MessageManager.getInstance().mart(cordova.getActivity(),messageId);
	}
	//打开模块
	public void openMessage(String messageId) {
		MessageManager.getInstance().openModule(cordova.getActivity(), messageId);
	}

	
}
