package com.zdnst.juju;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.zdnst.bsl.util.Preferences;
import com.zdnst.bsl.util.TimeUnit;
import com.zdnst.zillasdk.Zilla;
import com.zdnst.zillasdk.ZillaDelegate;
//import com.zdnst.bsl.util.PropertiesUtil;
//import com.zdnst.chameleon.httputil.HttpRequestAsynTask;
//import com.zdnst.chameleon.httputil.HttpUtil;
//import com.zdnst.chameleon.manager.R;
//import com.zdnst.chameleonsdk.config.CubeConstants;
//import com.zdnst.chameleonsdk.config.URL;


public class CAdminActivity extends Activity {
	public static final int REQUEST_CODE = 1;
	public static final int RESULT_CODE = -1;

	public boolean isAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String token = Preferences.getToken();
		String expired = Preferences.getExpired();
		
		if (!"".equals(token)){
			if (!"".equals(expired)){
				long expiredLong = TimeUnit.convert2long(expired , TimeUnit.LONG_FORMAT);
				long now = System.currentTimeMillis();
				if (now < expiredLong){
					finish();
					actionActivity();
					return;
				}
			}
		}
		authentication();
	}

	// 跳转
	public void actionActivity() {
//		// 平板
//		if (PadUtils.isPad(application)) {
//			Intent i = new Intent(AdminActivity.this, FacadeActivity.class);
//			i.putExtra("url", URL.PAD_MAIN_URL);
//			i.putExtra("direction", 1);
//			i.putExtra("type", "web");
//			i.putExtra("isPad", true);
//			startActivity(i);
//		} else {// 手机
			Intent i = new Intent(CAdminActivity.this, CmanagerModuleActivity.class);
//			i.putExtra("url", URL.PHONE_MAIN_URL);
//			i.putExtra("isPad", false);
			startActivity(i);
//		}
		finish();
	}

	private void authentication() {
		final Context context = CAdminActivity.this;
		Log.i("ljltest","CAdminActivity, authentication()");
		ZillaDelegate delegate = new ZillaDelegate() {
			
			@Override
			public void requestSuccess(String json) {
				try {
					Log.i("AAAAA", "json = " + json);
					if (json.equals("403")) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setTitle("提示");
						builder.setMessage("应用验证失败");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						Dialog dialog = builder.create();
						dialog.show();
					} else if (json.equals("400")) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setTitle("提示");
						builder.setMessage("应用已删除");
						builder.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								});
						Dialog dialog = builder.create();
						dialog.show();
					}else{
						JSONObject jb = new JSONObject(json);
						String token = jb.getString("token");
						String expired = jb.getString("expired");
						// 保存token和expired
						Preferences.saveToken(token, expired);
						finish();
						actionActivity();
					}

				} catch (JSONException e) {
					e.printStackTrace();

				}
				
			}
			
			@Override
			public void requestStart() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void requestFailed(String errorMessage) {
				Toast.makeText(CAdminActivity.this, "应用验证失败，请检查网络", Toast.LENGTH_LONG).show();
				finish();
			}
		};
		Zilla.getZilla().appAuth(context, delegate);
//		HttpRequestAsynTask loginTask = new HttpRequestAsynTask(CAdminActivity.this) {
//			@Override
//			protected void doPostExecute(String json) {
//				
//			}
//
//			@Override
//			public void doHttpFail(Exception e) {
//				super.doHttpFail(e);
//				Toast.makeText(CAdminActivity.this, "应用验证失败，请检查网络", Toast.LENGTH_SHORT).show();
//				finish();
//			}
//		};
//		loginTask.setDialogContent("正在验证...");
//		loginTask.setLockScreen(true);
//		loginTask.setShowProgressDialog(true);
//		loginTask.setNeedProgressDialog(true);
//		StringBuilder sb = new StringBuilder();
//		String appKey = PropertiesUtil.readProperties(CAdminActivity.this,
//				CubeConstants.CUBE_CONFIG).getString("appKey", "");
//		String secret = PropertiesUtil.readProperties(CAdminActivity.this,
//				CubeConstants.CUBE_CONFIG).getString("secret", "");
//
//		sb = sb.append("Form:appKey=").append(appKey).append(";secret=")
//				.append(secret);
//		String s = sb.toString();
//		
//		String url = URL.AUTH;
//		loginTask.execute(url, s, HttpUtil.UTF8_ENCODING, HttpUtil.HTTP_POST);
	}
	
}
