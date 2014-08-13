package com.zdnst.juju.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.foreveross.chameleon.manager.R;
import com.zdnst.bsl.util.BroadcastConstans;
import com.zdnst.chameleon.AppStatus;
import com.zdnst.juju.CmanagerModuleActivity;
import com.zdnst.juju.manager.ApplicationSyncListener;
import com.zdnst.juju.model.CubeApplication;
import com.zdnst.push.tool.PadUtils;


/**
 * <BR>
 * [功能详细描述] 策略更新插件
 * 
 */
public class SecurityChangePlugin extends CordovaPlugin {

	public Dialog dialog;
	
	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		if (action.equals("securityChange")) {
			String username = args.getString(0);
			CubeApplication.getInstance(cordova.getActivity()).syncPrivilege(false, cordova.getActivity(),
					username, new ApplicationSyncListener() {

						@Override
						public void syncStart() {
							
							dialog = new Dialog(cordova.getActivity(),R.style.common_dialog);
							LayoutInflater mInflater = (LayoutInflater)cordova.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							View v = mInflater.inflate(R.layout.dialog_layout, null);
							TextView	tv = (TextView) v.findViewById(R.id.dialog_text);
							tv.setText("正在获取帐户权限");
							dialog.setContentView(v);
							dialog.show();
						}

						@Override
						public void syncFinish() {
							callbackContext.success("success");
							if (null != dialog && dialog.isShowing()) {
								try {
									dialog.cancel();
								} catch (Exception e) {
									// TODO: handle exception
								}
								
							}
							AppStatus.USERLOGIN = true;
							AppStatus.FROMLOGIN = true;
							cordova.getActivity().sendBroadcast(
									new Intent(BroadcastConstans.SecurityRefreshMainPage));
							
							if (cordova.getActivity() instanceof CmanagerModuleActivity){
								CmanagerModuleActivity activity = (CmanagerModuleActivity) cordova.getActivity();
								activity.refreshMainPageEvent();
							}
							// 发送广播更新界面
							if(PadUtils.isPad(cordova.getActivity())){
								if(cordova.getActivity() instanceof CmanagerModuleActivity){
									cordova.getActivity().runOnUiThread(new Runnable() {
										@Override
										public void run() {
											((CmanagerModuleActivity)cordova.getActivity()).showDetailContent(false);
										}
									});
								}
							}else{
								cordova.getActivity().finish();
							}
//							cordova.getActivity().runOnUiThread(new Runnable() {
//								
//								@Override
//								public void run() {
//									try {
//										Thread.sleep(200);
//									} catch (InterruptedException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
//
//									
//								}
//							});
							

						}

						@Override
						public void syncFail() {
							if (null != dialog && dialog.isShowing()) {
								try {
									dialog.cancel();
								} catch (Exception e) {
									// TODO: handle exception
								}
								
							}
						}

						@Override
						public void syncFinish(String result) {
							// TODO Auto-generated method stub
							
						}
					}, "正在获取账户权限");
		
		} 
		return true;
	}
}