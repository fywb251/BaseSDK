package com.zdnst.juju.plugin;

import java.util.ArrayList;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import com.google.gson.Gson;
import com.zdnst.juju.model.UserPrivilege;

public class PrivilegePlugin extends CordovaPlugin{

	@Override
	public boolean execute(String action, JSONArray args,CallbackContext callbackContext) throws JSONException {
		String result = null ;
		if(action.equals("getPrivileges")) {
			
			result = new Gson().toJson(getPrivilege());
		}
		if (result != null) {
			System.out.println("Privilege = "+result);
			callbackContext.success(result);
			
		}
		return super.execute(action, args, callbackContext);
	}
	
	public ArrayList<String > getPrivilege() {
		ArrayList<String > list = UserPrivilege.getInstance().getPrivilegeList();
		return list;
	}
}
