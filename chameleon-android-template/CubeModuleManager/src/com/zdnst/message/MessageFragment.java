package com.zdnst.message;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.chameleon.manager.R;
import com.zdnst.bsl.util.BroadcastConstans;
import com.zdnst.data.table.MessageDataModel;
import com.zdnst.message.MessageAdapter.IMessageEventListener;
import com.zdnst.module.MessageInfo;
import com.zdnst.push.tool.PadUtils;


public class MessageFragment extends Fragment  implements IMessageEventListener{


	/**
	 * [导航左侧按钮（返回）]
	 */
	private LinearLayout titlebar_left;
	/**
	 * [导航右侧按钮（取消，编辑）]
	 */
	private Button titlebar_right;
	/**
	 * [标题内容]
	 */
	private TextView titlebar_content;
	/**
	 * [可伸展列表]
	 */
	private ExpandableListView msglist;
	/**
	 * [全选框]
	 */
	private CheckBox allselected;
	/**
	 * [编辑面板（最下面）]
	 */
	private RelativeLayout editcheckbox;

	/**
	 * [删除按钮]
	 */
	private Button delete;
	/**
	 * [已读按钮]
	 */
	private Button mark;

	/**
	 * [msglist对应数据源adpater]
	 */
	private MessageAdapter messageAdapter;


	/**
	 * [本面板对应数据模型（单例）]
	 */

	private ArrayList<ArrayList<MessageInfo>> list;
	
	private HashMap<String , ArrayList<MessageInfo>> messageMap;
	
	private boolean allCheck;
	
	private boolean allCheckBoxVisibility;
	
	private ArrayList<MessageInfo> seleteList;
	
	IntentFilter intentFilter = new IntentFilter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}


	private void uneditState() {
		titlebar_right.setText("编辑");
		editcheckbox.setVisibility(View.GONE);
		allCheckBoxVisibility = false;
	}

	private void editState() {
		titlebar_right.setText("取消");
		titlebar_right.setVisibility(View.VISIBLE);
		editcheckbox.setVisibility(View.VISIBLE);
		allCheckBoxVisibility = true;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		return inflater.inflate(R.layout.message, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		list = new ArrayList<ArrayList<MessageInfo>>();
		messageMap = new HashMap<String, ArrayList<MessageInfo>>();
		seleteList = new ArrayList<MessageInfo>();
		titlebar_left = (LinearLayout) view.findViewById(R.id.title_barleft);
		titlebar_left.setOnClickListener(clickListener);
		titlebar_right = (Button) view.findViewById(R.id.title_barright);
		titlebar_right.setOnClickListener(clickListener);
		titlebar_right.setBackgroundResource(R.drawable.normal_button_clickbg);
		titlebar_right.setText("编辑");
		titlebar_content = (TextView) view.findViewById(R.id.title_barcontent);
		titlebar_content.setText("消息推送");
		msglist = (ExpandableListView) view.findViewById(R.id.msglist);
		editcheckbox = (RelativeLayout) view.findViewById(R.id.editcheckbox);
		allselected = (CheckBox) view.findViewById(R.id.allselected);
		allselected.setOnCheckedChangeListener(checkedChangeListener);
		delete = (Button) view.findViewById(R.id.delete);
		delete.setOnClickListener(clickListener);
		mark = (Button) view.findViewById(R.id.mark);
		mark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 设置为已读，并存数据库
				MessageDataModel messageDataModel = new MessageDataModel(getActivity());
				for (MessageInfo messageInfo : seleteList) {
					messageInfo.setHasread(true);
					messageDataModel.updateInfo(messageInfo);
				}
				messageAdapter.notifyDataSetChanged();
			}
		});
		init(getActivity());
		messageAdapter = new MessageAdapter(getActivity(),
				list);
		messageAdapter.setListener(this);
		msglist.setGroupIndicator(null);
		msglist.setAdapter(messageAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		// *告知MessageFragment处于显示状态
//		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(
//				new PresenceEvent(TmpConstants.VIEW_MESSAGE_PRESENCE, true));
		messageAdapter.notifyDataSetChanged();	
		// * 如果当前牌resume阶段，不应该发送message通知
	}

	@Override
	public void onPause() {
		super.onPause();
		// * 如果当前牌pause阶段，应该发送message通知
	}

	@Override
	public void onStop() {
		super.onStop();
		// *告知MessageFragment处于非显示状态
//		EventBus.getEventBus(TmpConstants.EVENTBUS_COMMON).post(
//				new PresenceEvent(TmpConstants.VIEW_MESSAGE_PRESENCE, false));

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
	}


	OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.title_barleft) {
				if (PadUtils.isPad(getActivity())) {
					//TODO 
					//((FacadeActivity) getActivity()).popRight();
				} else {
					getActivity().finish();
				}
			}else  if (v.getId()==R.id.title_barright) {
				if(editcheckbox.getVisibility() == View.GONE){
					editState();
				} else {
					uneditState();
				}
			}else  if (v.getId()==R.id.delete) {
					new AlertDialog.Builder(getActivity())
							.setTitle("提示")
							.setMessage("确定要删除么？")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface paramDialogInterface,
												int paramInt) {
											showCustomDialog(true);
											MessageDataModel messageDataModel = new MessageDataModel(getActivity());
											for (MessageInfo info : seleteList) {
												messageDataModel.deleteMessageInfo(info);
												 ArrayList<MessageInfo> infoList = messageMap.get(info.getIdentifier());
												 if (infoList != null){
													 infoList.remove(info);
													 if (infoList.size() == 0){
														 messageMap.remove(info.getIdentifier());
														 list.remove(infoList);
													 }
												 }
											}
											messageAdapter.notifyDataSetChanged();
											cancelDialog();
										}
									}).setNegativeButton("取消", null).show();
			}
//			switch (v.getId()) {
//			case R.id.title_barleft: {
//				
//			}
//			case R.id.title_barright:
//				
//				break;
//			case R.id.delete:
//				
//				break;
//			default:
//				break;
//			}
		}
	};

	OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (allCheck){
				allCheck = false;
				
				messageAdapter.notifyDataSetChanged();
			} else {
				seleteList.clear();
				for (ArrayList<MessageInfo> infoList : list) {
					seleteList.addAll(infoList);
				}
				allCheck = true;
				messageAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	public Dialog progressDialog;

	public void showCustomDialog(boolean cancelable) {
		if (progressDialog == null) {
			progressDialog = new Dialog(getActivity(), R.style.dialog);
			progressDialog.setContentView(R.layout.dialog_layout);
		}

		if (progressDialog.isShowing()) {
			return;
		}
		progressDialog.setCancelable(cancelable);
		progressDialog.show();
	}

	public void cancelDialog() {
		if (progressDialog == null) {
			return;
		}
		if (progressDialog.isShowing()) {
			progressDialog.cancel();
		}
	}

	@Override
	public String toString() {
		return this.getClass().getCanonicalName();
	}
	
	public synchronized void init(Context context) {
		try {
			intentFilter.addAction(BroadcastConstans.ReceiveMessage);
			context.registerReceiver(receiver, intentFilter);
			MessageDataModel messageDataModel = new MessageDataModel(context);
			ArrayList<String> identifiers = messageDataModel.getAllIdentifier();
			for (String  identifier : identifiers) {
				ArrayList<MessageInfo>  infoList = messageDataModel.getAllMessageInfoByIdentifier(identifier);
				if (infoList.size() > 0){
					list.add(infoList);
					messageMap.put(identifier, infoList);
				}
			}
			
//			ArrayList<MessageInfo>  infoList = messageDataModel.getAllMessageInfoByIdentifier(MessageConstants.MESSAGE_SYSTEM_IDENTIFIER);
//			if (infoList.size() > 0){
//				list.add(infoList);
//				messageMap.put(MessageConstants.MESSAGE_SYSTEM_IDENTIFIER, infoList);
//			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onMessageClickEvent(MessageInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChackBoxEvent(MessageInfo info, boolean check) {
		// TODO Auto-generated method stub
		if(check){
			seleteList.add(info);
		} else {
			if (seleteList.contains(info)){
				seleteList.remove(info);
			}
		}
	}

	public boolean isAllCheck() {
		return allCheck;
	}

	public boolean isAllCheckBoxVisibility() {
		return allCheckBoxVisibility;
	}
	
	BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (BroadcastConstans.ReceiveMessage.equals(intent.getAction())) {
				MessageDataModel messageDataModel = new MessageDataModel(context);
				String identifier = intent.getStringExtra("identifier");
				ArrayList<MessageInfo> messageList = messageMap.get(identifier);
				ArrayList<MessageInfo>  infoList = messageDataModel.getAllMessageInfoByIdentifier(identifier);
				if (messageList != null && messageList.size() > 0){
					
					messageList.clear();
					messageList.addAll(infoList);
				} else {
					list.add(infoList);
					messageMap.put(identifier, infoList);
				}
				messageAdapter.notifyDataSetChanged();
			}
		}
	};
}
