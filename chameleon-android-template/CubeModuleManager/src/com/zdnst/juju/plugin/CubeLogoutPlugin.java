package com.zdnst.juju.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;

import com.zdnst.bsl.util.BroadcastConstans;
import com.zdnst.bsl.util.Preferences;
import com.zdnst.chameleon.AppStatus;
import com.zdnst.juju.CheckInUtil;
import com.zdnst.juju.manager.CubeModuleManager;
import com.zdnst.juju.model.CubeApplication;
import com.zdnst.juju.model.CubeModule;
import com.zdnst.juju.model.Privilege;
import com.zdnst.juju.model.UserPrivilege;
 
/**
 * <BR>
 * [功能详细描述] 登出插件
 * 
 */
public class CubeLogoutPlugin extends CordovaPlugin {

	public Dialog dialog;

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		if (action.equals("logout")) {
			logout(callbackContext);
		}
		return true;
	}

	private void logout(final CallbackContext callbackContext) {
		AppStatus.USERLOGIN = false;
		callbackContext.success("success");

		String privileges = Preferences.getPrivileges( );
		saveGuestApp(privileges);
//		CubeApplication remote_app = CubeApplication
//				.buildGuestApplication(privileges);
//		CubeApplication comparedCubeApp = cubeApp.compareAndSetApp(cubeApp,
//				remote_app);
//		CubeModuleManager.getInstance().init(comparedCubeApp);
//
//		cubeApp.save(cubeApp);
		cordova.getActivity().sendBroadcast(new Intent(BroadcastConstans.SecurityRefreshMainPage));
		
		// 签到
		Preferences.saveUser("guest");
		CheckInUtil.pushSecurity(cordova.getActivity(), "guest");
	}
	
	private void saveGuestApp(String result){
		try {
			JSONObject jb = new JSONObject(result);
			JSONArray jay = jb.getJSONArray("priviliges");
			int len = jay.length();
			ArrayList<String> getList = UserPrivilege.getInstance().getGetList();
			ArrayList<String> deleteList = UserPrivilege.getInstance().getDeleteList();
			ArrayList<String> privilegeList = UserPrivilege.getInstance().getPrivilegeList();
			deleteList.clear();
			getList.clear();
			privilegeList.clear();
			for(int i = 0;i < len; i ++){
				JSONArray jay2 = jay.getJSONArray(i);
				if (!privilegeList.contains(jay2.getString(1))){
					privilegeList.add(jay2.getString(1));
				}
				
				if(UserPrivilege.GET.equals(jay2.getString(0))){
					getList.add(jay2.getString(1));
				}
				
				if (UserPrivilege.DELETE.equals(jay2.getString(0))){
					deleteList.add(jay2.getString(1));
				}
			}
			CubeApplication cubeApplication = CubeApplication.getInstance(cordova.getActivity());
			Set<CubeModule> modules = cubeApplication.getModules();
			for (CubeModule cubeModule : modules) {
				if (privilegeList.contains(cubeModule.getIdentifier())){
					List<Privilege> prList = new ArrayList<Privilege>();
					cubeModule.setPrivileges(prList);
				} else {
					cubeModule.setPrivileges(null);
				}
			}
			CubeModuleManager.getInstance().init(cubeApplication);
			cubeApplication.save(cubeApplication);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}