package com.zdnst.message;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.foreveross.chameleon.manager.R;
import com.zdnst.bsl.util.TimeUnit;
import com.zdnst.data.table.MessageDataModel;
import com.zdnst.juju.CmanagerModuleActivity;
import com.zdnst.juju.manager.CubeModuleManager;
import com.zdnst.juju.model.CubeModule;
import com.zdnst.module.MessageInfo;
import com.zdnst.push.tool.PadUtils;
import com.zdnst.push.url.TmpConstants;

public class MessageAdapter extends BaseExpandableListAdapter {
	private Context context;

	private ArrayList<ArrayList<MessageInfo>> list;
	
	private IMessageEventListener listener;

	public MessageAdapter(Context context, ArrayList<ArrayList<MessageInfo>> list) {
		this.context = context;
		this.list = list;
	}

	// ---------------------------------------以下是child的回调函数-----------------------------------------//

	@Override
	public MessageInfo getChild(int groupPosition, int childPosition) {
		return list.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int getChild, int childPosition) {
		return 0;
	}

	private void jump(MessageInfo messageInfo) {
		if (!existModule(messageInfo.getIdentifier())) {
			Toast.makeText(this.context, "模块不存在,或被隐藏!", Toast.LENGTH_SHORT).show();
			return;
		}

		CubeModule module = CubeModuleManager.getInstance().getCubeModuleByIdentifier(messageInfo.getIdentifier());
		String [] params = messageInfo.getParams();
		if(PadUtils.isPad(context)){
			if(context instanceof CmanagerModuleActivity){
				Bundle bundle = null;
				boolean isLocal  = false;
				String url = CubeModuleManager.getInstance().getModuleUrl(context, module);
				if(module.getLocal() != null){
					isLocal  = true;
					bundle = new Bundle();
					bundle.putStringArray("parameters", params);
				}
				((CmanagerModuleActivity)context).pushDetailFragment(url,isLocal,bundle);
			}
		}else{
			if(module.getModuleType() ==CubeModule.INSTALLED) {
				Intent i = CubeModuleManager.getInstance().showModule(context, module);
				if (params!=null) {
					i.putExtra("parameters", params);
				}
				context.startActivity(i);
			}else {
				Toast.makeText(context, "文件缺失，请重新下载",Toast.LENGTH_SHORT).show();
			}
		}
		
		
//		if (moduleMessage instanceof NoticeModuleMessageStub) {
//			String messageId = NoticeModuleMessageStub.class.cast(moduleMessage).getMesssageId();
//			Intent intent = new Intent();
//			intent.putExtra("messageId", messageId);
//			if (PadUtils.isPad(context)) {
////				intent.setClass(context, FacadeActivity.class);
////				PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(
////						context, CubeConstants.CUBE_CONFIG);
////				String noticeViewClassName = propertiesUtil.getString(
////						"com.foss.announcement", "");
////				intent.putExtra("direction", 2);
////				intent.putExtra("type", "fragment");
////				intent.putExtra("value", noticeViewClassName);
//
//			} else {
//			//	intent.setClass(context, NoticeActivity.class);
//			}
//			context.startActivity(intent);
//		} else {
//			jump2web(moduleMessage);
//		}
	}

//	private void jump2web(ModuleMessage<?> moduleMessage) {
//		CubeModule module = CubeModuleManager.getInstance()
//				.getCubeModuleByIdentifier(moduleMessage.getIdentifier());
//		if (module != null
//				&& !TmpConstants.MESSAGE_RECORD_IDENTIFIER.equals(module
//						.getIdentifier())
//				&& !TmpConstants.ANNOUCE_RECORD_IDENTIFIER.equals(module
//						.getIdentifier())) {
//			MessageFragmentModel.instance().readAllRecordsByModule(
//					module.getName());
//		}
//		String path = Environment.getExternalStorageDirectory().getPath() + "/"
//				+ URL.APP_PACKAGENAME;
//		String url = path + "/www/" + moduleMessage.getIdentifier();
//		// 检查文件是否存在
//		if (new FileCopeTool(context).isfileExist(url, "index.html")) {
//
//			Intent intent = new Intent();
//			if (PadUtils.isPad(context)) {
//				// intent.setClass(context, FacadeActivity.class);
//				// intent.putExtra("direction", 2);
//				// intent.putExtra("type", "web");
//				// intent.putExtra("value", "file:/" + url + "/index.html");
//			} else {
//				intent.setClass(context, CubeAndroid.class);
//				intent.putExtra("isPad", false);
//				intent.putExtra("from", "main");
//				intent.putExtra("path", Environment
//						.getExternalStorageDirectory().getPath()
//						+ "/"
//						+ URL.APP_PACKAGENAME);
//				intent.putExtra("identify", moduleMessage.getIdentifier());
//			}
//
//			context.startActivity(intent);
//
//		} else {
//			Toast.makeText(context, "文件缺失，请重新下载", Toast.LENGTH_SHORT).show();
//		}
//	}

	private boolean existModule(String identifier) {
		if (identifier.equals(TmpConstants.ANNOUCE_RECORD_IDENTIFIER)) {
			return true;
		}
		return CubeModuleManager.getInstance().getCubeModuleByIdentifier(
				identifier) != null;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final MessageInfo messageInfo = list.get(groupPosition).get(
				childPosition);

		ChildHolder msgContentItem = null;
		if (null == convertView) {
			msgContentItem = new ChildHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.msg_content, null, false);
			msgContentItem.msg_title = (TextView) convertView
					.findViewById(R.id.msg_title);
			msgContentItem.msg_content = (TextView) convertView
					.findViewById(R.id.msg_content);
			msgContentItem.msg_time = (TextView) convertView
					.findViewById(R.id.msg_time);
			msgContentItem.msg_checkbox = (CheckBox) convertView
					.findViewById(R.id.msgcheckbox);
			msgContentItem.msg_readStatus = (TextView) convertView
					.findViewById(R.id.msg_readStatus);
			msgContentItem.msgBody = convertView.findViewById(R.id.msgbody);

			convertView.setTag(msgContentItem);
		} else {
			msgContentItem = (ChildHolder) convertView.getTag();
		}
		
		msgContentItem.msgBody.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean linkable = messageInfo.isLinkable();
				messageInfo.setHasread(true);
				MessageDataModel messageDataModel = new MessageDataModel(context);
				messageDataModel.updateInfo(messageInfo);
				notifyDataSetChanged();
				// 保存数据库
				if (linkable) {
					jump(messageInfo);
				}
			}

		});
		msgContentItem.msg_title.setText(messageInfo.getTitle());
		msgContentItem.msg_content.setText(messageInfo.getContent());
		msgContentItem.msg_time.setText(trans(System.currentTimeMillis(),
				messageInfo));
		// msgContentItem.msg_checkbox.setChecked(messageModule.isSelected());
		msgContentItem.msg_checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				listener.onChackBoxEvent(messageInfo, isChecked);
			}
		});
		
		if (messageInfo.isHasread()) {
			msgContentItem.msg_time.setTextColor(Color.parseColor("#212121"));
			msgContentItem.msg_readStatus.setText("已读");
			msgContentItem.msg_readStatus.setTextColor(Color.BLACK);
		} else {
			msgContentItem.msg_time.setTextColor(Color.parseColor("#478ac9"));
			msgContentItem.msg_readStatus.setText("未读");
			msgContentItem.msg_readStatus.setTextColor(Color.RED);
		}
		
		MessageFragment frameFragment = (MessageFragment) listener;
		if(frameFragment.isAllCheck()){
			msgContentItem.msg_checkbox.setVisibility(View.VISIBLE);
			msgContentItem.msg_checkbox.setChecked(true);
		} else {
			msgContentItem.msg_checkbox.setVisibility(View.VISIBLE);
			msgContentItem.msg_checkbox.setChecked(false);
		}
		
		if(frameFragment.isAllCheckBoxVisibility()){
			msgContentItem.msg_checkbox.setVisibility(View.VISIBLE);
		} else {
			msgContentItem.msg_checkbox.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ChildHolder {
		TextView msg_title;
		TextView msg_content;
		TextView msg_time;
		TextView msg_read;
		TextView msg_readStatus;
		CheckBox msg_checkbox;
		View msgBody;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return list.get(groupPosition).size();
	}

	// ---------------------------------------以下是group的回调函数-----------------------------------------//

	@Override
	public ArrayList<MessageInfo> getGroup(int groupPosition) {
		return list.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return list.size();
	}

	@Override
	public long getGroupId(int groupPosition) {

		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// 初始化组件
		GroupViewHolder msgTitleItem;
		if (convertView == null) {
			msgTitleItem = new GroupViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.msg_title, null);
			msgTitleItem.msgIcon = (ImageView) convertView
					.findViewById(R.id.msg_icon);
			msgTitleItem.msgSort = (TextView) convertView
					.findViewById(R.id.msg_sort);
			msgTitleItem.msgNum = (TextView) convertView
					.findViewById(R.id.msg_num);
			msgTitleItem.delete_icon = (TextView) convertView
					.findViewById(R.id.delete_icon);
			convertView.setTag(msgTitleItem);

		} else {
			msgTitleItem = (GroupViewHolder) convertView.getTag();
		}
		// 得到组模型
		final ArrayList<MessageInfo> messageInfos = list.get(groupPosition);


		if (messageInfos != null && messageInfos.size()>0){
			if (messageInfos.get(0) != null){
				if (isExpanded) {
					msgTitleItem.msgIcon.setImageResource(R.drawable.arrow_dowm);
				} else {
					msgTitleItem.msgIcon.setImageResource(R.drawable.arrow);
				}
				msgTitleItem.msgSort.setText(messageInfos.get(0).getGroupBelong());
				
//				CubeModule module = CubeModuleManager.getInstance()
//						.getModuleByIdentify(messageInfos.get(0).getIdentifier());
//				if (module != null) {
//					msgTitleItem.msgSort.setText(module.getName());
//				} else {
//					msgTitleItem.msgSort.setText(messageInfos.get(0).getGroupBelong());
//				}
				int unreadMsgCount = 0;
				int msgCount = messageInfos.size();
				for (MessageInfo messageInfo : messageInfos) {
					if (!messageInfo.isHasread()){
						unreadMsgCount++;
					}
				}
				msgTitleItem.msgNum.setText(unreadMsgCount + "/" + msgCount);
				if (unreadMsgCount > 0) {
					msgTitleItem.msgNum.setTextColor(Color.RED);
				} else {
					msgTitleItem.msgNum.setTextColor(Color.WHITE);
				}
			}
		}
		return convertView;
	}

	class GroupViewHolder {
		/** 向下的箭头 */
		ImageView msgIcon;
		/** 消息标题 */
		TextView msgSort;
		/** 消息条数的圆背景 */
		TextView msgNum;
		TextView delete_icon;
	}

	/** 点击item是否变色 */
	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	private String trans(long current, MessageInfo info) {
		long diff = current - info.getSendtime();
		long day = diff / 86400000;
		long hour = (diff % 86400000) / 3600000;
		long min = (diff % 86400000 % 3600000) / 60000;
		String resultDate = null;
		if (day > 3) {
			resultDate = TimeUnit.LongToStr(info.getSendtime(),
					TimeUnit.SHORT_FORMAT);
		} else if (day <= 3 && day > 0) {
			resultDate = day + "天前";
		} else if (day == 0 && hour > 0) {
			resultDate = hour + "小时前";
		} else if (hour == 0 && min > 0) {
			resultDate = min + "分钟前";
		} else {
			resultDate = "最新信息";
		}
		return resultDate;
	}
	
	/**
	 * 
	 * @version 1.0
	 */
	public interface IMessageEventListener {
		/**
		 * 发生了事件回调
		 * 
		 */
		public void onMessageClickEvent(MessageInfo info);
		
		public void onChackBoxEvent(MessageInfo info , boolean check);

	}

	public void setListener(IMessageEventListener listener) {
		this.listener = listener;
	}
	
}
