package com.zdnst.juju.settings;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.chameleon.manager.R;
import com.zdnst.juju.model.CubeApplication;
import com.zdnst.message.update.CheckUpdateTask;
import com.zdnst.message.update.ManualCheckUpdateListener;
import com.zdnst.push.tool.PadUtils;

public class SettingsActivity extends Activity {
	private LinearLayout titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;
	private RelativeLayout setting_about;
	private RelativeLayout setting_update;
//	private RelativeLayout setting_pushsetting;
	private Button logOff;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		PadUtils.setSceenSize(this);
		initValue();
	}

	private void initValue() {
		titlebar_left = (LinearLayout) findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(listener);
		titlebar_right = (Button) findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) findViewById(R.id.title_barcontent);
		titlebar_content.setText("设置");

		setting_about = (RelativeLayout) findViewById(R.id.setting_btn_about);
		setting_update = (RelativeLayout) findViewById(R.id.setting_btn_update);
		//setting_pushsetting = (RelativeLayout) findViewById(R.id.setting_btn_pushstting);
		logOff = (Button) findViewById(R.id.logoff);
		logOff.setOnClickListener(listener);
		setting_about.setOnClickListener(listener);
		setting_update.setOnClickListener(listener);
	//	setting_pushsetting.setOnClickListener(listener);


	}


	OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			
			if (v.getId() == R.id.title_barleft) {
				finish();
				return;
			} else if (v.getId() == R.id.setting_btn_about) {
				Intent i = new Intent();
				i.setClass(SettingsActivity.this, AboutActivity.class);
				startActivity(i);
				return;
			} else if (v.getId() == R.id.setting_btn_update) {
				new CheckUpdateTask(
						CubeApplication.getInstance(SettingsActivity.this),
						new ManualCheckUpdateListener(SettingsActivity.this))
				.execute();
				return;	
			} 
//			else if (v.getId() == R.id.setting_btn_pushstting) {
//				Intent intent = new Intent();
//				intent.setClass(SettingsActivity.this,
//						PushSettingActivity.class);
//				startActivity(intent);
//				return;
//			} 
			else if (v.getId() == R.id.logoff) {
				Dialog dialog = new AlertDialog.Builder(SettingsActivity.this)
				.setTitle("提示")
				.setMessage("确认要注销？")
				.setNegativeButton("取消", null)
				.setPositiveButton("确认",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
//									/.logOff();

								}
							}).create();
				dialog.show();
				return;
			} 
		
//			switch (v.getId()) {
//			case R.id.title_barleft:
//				finish();
//				break;
//			case R.id.setting_btn_about:
//				Intent i = new Intent();
//				i.setClass(SettingsActivity.this, AboutActivity.class);
//				startActivity(i);
//				break;
//			case R.id.setting_btn_update:
////				if (application.getLoginType() == TmpConstants.LOGIN_OUTLINE) {
////					Toast.makeText(v.getContext(), "离线登录不能使用该功能",
////							Toast.LENGTH_SHORT).show();
////					return;
////				} else {
////					new CheckUpdateTask(
////							application.getCubeApplication(),
////							new ManualCheckUpdateListener(SettingsActivity.this))
////							.execute();
////				}
//				break;
//			case R.id.setting_btn_pushstting:
////				if (application.getLoginType() == TmpConstants.LOGIN_OUTLINE) {
////					Toast.makeText(v.getContext(), "离线登录不能使用该功能",
////							Toast.LENGTH_SHORT).show();
////					return;
////				} else {
//					Intent intent = new Intent();
//					intent.setClass(SettingsActivity.this,
//							PushSettingActivity.class);
//					startActivity(intent);
////				}
//				break;
//			case R.id.logoff:
//				Dialog dialog = new AlertDialog.Builder(SettingsActivity.this)
//					.setTitle("提示")
//					.setMessage("确认要注销？")
//					.setNegativeButton("取消", null)
//					.setPositiveButton("确认",
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
////										/.logOff();
//
//									}
//								}).create();
//				dialog.show();
//				break;
//			default:
//				break;
//			}
		}

	};
}
