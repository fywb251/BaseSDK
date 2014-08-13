package com.zdnst.juju.settings;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.chameleon.manager.R;
import com.zdnst.push.client.NotificationService;

public class PushSettingActivity extends Activity {

//	private final static Logger log = LoggerFactory
//			.getLogger(PushSettingActivity.class);
	// title concern
	private LinearLayout titlebar_left;
	private Button titlebar_right;
	private TextView titlebar_content;

	private TextView cbStatusXmpp;
	private CheckBox xmppCheckBox;

	private TextView cbStatusMina;
	private CheckBox minaCheckBox;

	private NotificationService notificationService = null;
//	private MinaPushService minaPushService = null;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_pushsetting);
		initValue();
//		EventBus.getEventBus(TmpConstants.COMMNAD_INTENT, ThreadEnforcer.MAIN)
//				.register(this);
	}

//	@Subscribe
//	public void onConnectStatusChangeEvent(
//			ConnectStatusChangeEvent connectStatusChangeEvent) {
////		log.debug("receive connection status changed Event...");
//		if (connectStatusChangeEvent.getChannel().equals(
//				ConnectStatusChangeEvent.CONN_CHANNEL_CHAT)) {
////			log.debug("xmpp connection status changed...");
//			if (connectStatusChangeEvent.getStatus().equals(
//					ConnectStatusChangeEvent.CONN_STATUS_ONLINE)) {
////				log.debug("xmpp connection online...");
//				cbStatusXmpp.setText("已连接");
//				xmppCheckBox.setChecked(true);
//			} else {
////				log.debug("xmpp connection offline...");
//				cbStatusXmpp.setText("未连接");
//				xmppCheckBox.setChecked(false);
//			}
//		} else if (connectStatusChangeEvent.getChannel().equals(
//				ConnectStatusChangeEvent.CONN_CHANNEL_OPENFIRE)) {
////			log.debug("mina connection status changed...");
//			if (connectStatusChangeEvent.getStatus().equals(
//					ConnectStatusChangeEvent.CONN_STATUS_ONLINE)) {
////				log.debug("mina connection online...");
//				cbStatusMina.setText("已连接");
//				minaCheckBox.setChecked(true);
//			} else {
////				log.debug("mina connection offline...");
//				cbStatusMina.setText("未连接");
//				minaCheckBox.setChecked(false);
//			}
//		}
//	}

	private void initValue() {
//		notificationService = application.getNotificationService();
		
//		minaPushService = application.getMinaPushService();
//		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
//				.register(this);
		// PadUtils.setSceenSize(this);
		titlebar_left = (LinearLayout) findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);
		titlebar_right = (Button) findViewById(R.id.title_barright);
		titlebar_right.setVisibility(View.GONE);
		titlebar_content = (TextView) findViewById(R.id.title_barcontent);
		titlebar_content.setText("推送设置");

		cbStatusMina = (TextView) findViewById(R.id.pushsetting_cbstatus_mina);
		cbStatusXmpp = (TextView) findViewById(R.id.pushsetting_cbstatus_xmpp);
		xmppCheckBox = (CheckBox) findViewById(R.id.pushsetting_cb_xmpp);
		xmppCheckBox.setClickable(false);
		xmppCheckBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

//				if (notificationService != null
//						&& !NetworkUtil.isNetworkConnected(PushSettingActivity.this)) {
//					Toast.makeText(PushSettingActivity.this, "网络异常，请检查设置！",
//							Toast.LENGTH_SHORT).show();
//					cbStatusMina.setText("未连接");
//					minaCheckBox.setChecked(false);
//					cbStatusXmpp.setText("未连接");
//					xmppCheckBox.setChecked(false);
//					return;
//				}

//				if (xmppCheckBox.isChecked()) {
//					cbStatusXmpp.setText("正在打开...");
//					notificationService.reconnect(application.getChatManager());
//				} else {
//					cbStatusXmpp.setText("正在关闭...");
//					notificationService.disconnect(application.getChatManager());
//				}
			}
		});
//		minaCheckBox = (CheckBox) findViewById(R.id.pushsetting_cb_mina);
//		minaCheckBox.setClickable(true);
//		minaCheckBox.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (notificationService != null && !NetworkUtil.isNetworkConnected(PushSettingActivity.this)) {
//					Toast.makeText(PushSettingActivity.this, "网络异常，请检查设置！",
//							Toast.LENGTH_SHORT).show();
//					cbStatusMina.setText("未连接");
//					minaCheckBox.setChecked(false);
//					cbStatusXmpp.setText("未连接");
//					xmppCheckBox.setChecked(false);
//					return;
//				}
//				if (minaCheckBox.isChecked()) {
//					cbStatusMina.setText("正在打开...");
////					minaPushService.reconnect();
//					notificationService.reconnect(application.getPushManager());
//					SharedPreferencesUtil.getInstance(PushSettingActivity.this)
//							.saveBoolean(TmpConstants.SELECT_OPEN, true);
//				} else {
//					
//					cbStatusMina.setText("正在关闭...");
////					minaPushService.disConnected();
//					notificationService.disconnect(application.getPushManager());
//					SharedPreferencesUtil.getInstance(PushSettingActivity.this)
//							.saveBoolean(TmpConstants.SELECT_OPEN, false);
//				}
//			}
//		});

//		if (application.getNotificationService().isOnline(application.getChatManager())) {
//			cbStatusXmpp.setText("已连接");
//			xmppCheckBox.setChecked(true);
//		} else {
//			cbStatusXmpp.setText("未连接");
//			xmppCheckBox.setChecked(false);
//		}

//		if (application.getMinaPushService().isOnline()) {
//		if (application.getNotificationService().isOnline(application.getPushManager())) {
//			cbStatusMina.setText("已连接");
//			minaCheckBox.setChecked(true);
//		} else {
//			cbStatusMina.setText("未连接");
//			minaCheckBox.setChecked(false);
//		}
	}

	OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getId()==R.id.title_barleft){
				finish();
			}
		}
	};

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述] 2013-10-19 上午11:36:05
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

}
