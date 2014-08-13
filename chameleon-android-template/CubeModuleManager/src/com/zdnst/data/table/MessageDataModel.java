package com.zdnst.data.table;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zdnst.data.DataProvider;
import com.zdnst.module.MessageInfo;

public class MessageDataModel {

	// 上下文
	protected Context mContext;

	// 数据持久化执行者
	protected DataProvider mDataProvider = null;

	public MessageDataModel(Context context) {
		mContext = context;
		mDataProvider = DataProvider.getInstance(context,"");
	}

	/**
	 * 查询信息
	 * 
	 * @param selection
	 *            orderby
	 */
	public ArrayList<MessageInfo> getAllMessageInfo() {
		Cursor cursor = mDataProvider.getAllRecord(MessageTable.TABLENAME,
				null, null, null, MessageTable.SENDTIME + " DESC");
		ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						MessageInfo messageInfo = createMessageInfo(cursor);
						messageInfos.add(messageInfo);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return messageInfos;
	}
	
	public ArrayList<String> getAllIdentifier() {
		Cursor cursor = mDataProvider.getAllRecord(MessageTable.TABLENAME,
				null, null, null, MessageTable.SENDTIME + " DESC");
		ArrayList<String> identifiers = new ArrayList<String>();
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						String identifier = cursor.getString(cursor
								.getColumnIndex(MessageTable.IDENTIFIER));
						if (!identifiers.contains(identifier)){
							identifiers.add(identifier);
						}
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return identifiers;
	}
	
	
	/**
	 * 查询信息
	 * 
	 * @param selection
	 *            orderby
	 */
	public ArrayList<MessageInfo> getAllMessageInfoByIdentifier(String identifier) {
		String selection = MessageTable.IDENTIFIER + " = " + "'" + identifier + "'";
		Cursor cursor = mDataProvider.getAllRecord(MessageTable.TABLENAME,
				null, selection, null, MessageTable.SENDTIME + " DESC");
		ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						MessageInfo messageInfo = createMessageInfo(cursor);
						messageInfos.add(messageInfo);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return messageInfos;
	}
	
	/**
	 * 查询信息
	 * 
	 * @param selection
	 *            orderby
	 */
	public int getUnReadCount() {
		String selection = MessageTable.HASREAD + " = " + "'" + "0" + "'";
		Cursor cursor = mDataProvider.getAllRecord(MessageTable.TABLENAME,
				null, selection, null, MessageTable.SENDTIME + " DESC");
		int count = 0;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						count++;
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return count;
	}
	
	
	public int getIdentifierUnReadCount(String identifier) {
		String selection = MessageTable.HASREAD + " = " + "'" + "0" + "'" + " AND "
				+ MessageTable.IDENTIFIER + " = " + "'" + identifier + "'";
		
		
		
//		String selection = MessageTable.HASREAD + " = " + "'" + "0" + "'";
		Cursor cursor = mDataProvider.getAllRecord(MessageTable.TABLENAME,
				null, selection, null, MessageTable.SENDTIME + " DESC");
		int count = 0;
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						count++;
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return count;
	}

	/**
	 * 添加用戶的信息
	 * 
	 * @param userInfo
	 */
	public int addMessageInfo(MessageInfo messageInfo) {
		if (messageInfo == null) {
			return -1;
		}
		ContentValues contentValues = createValues(messageInfo);
		int id = (int) mDataProvider.insertRecord(MessageTable.TABLENAME,
				contentValues);
		contentValues.clear();
		contentValues = null;

		return id;
	}
	
	
	/**
	 * 添加批量用戶的信息
	 * 
	 * @param userInfo
	 */
	public boolean addMessageInfoList(ArrayList<MessageInfo> messageInfos) {
		boolean ret = false;

		DataProvider.getInstance(mContext).beginTransaction();
		try {

			for (MessageInfo messagetInfo : messageInfos) {
				addMessageInfo(messagetInfo);
			}
			DataProvider.getInstance(mContext).setTransactionSuccessful();
			ret = true;
		} catch (Exception e) {
		} finally {
			DataProvider.getInstance(mContext).endTransaction();
		}

		return ret;

	}

	/**
	 * 删除用户信息
	 * 
	 * @param number
	 */
	public void deleteMessageInfo(MessageInfo messageInfo) {
		String selection = null;
		if (!"".equals(messageInfo.getMesssageid())) {
			selection = MessageTable.MESSSAGEID + " = " + "'" + messageInfo.getMesssageid() + "'";
		}
		mDataProvider.delRecord(MessageTable.TABLENAME, selection);
	}
	
	public void deleteMessageInfo(String messageid) {
		String selection = null;
		if (!"".equals(messageid)) {
			selection = MessageTable.MESSSAGEID + " = " + "'" + messageid + "'";
		}
		mDataProvider.delRecord(MessageTable.TABLENAME, selection);
	}
	
	/**
	 * 批量删除用戶信息
	 */
	public boolean deleteMessageInfoList(ArrayList<MessageInfo> messageInfos) {
		boolean secsuss = false;
		if (messageInfos == null) {
			return secsuss;
		}
		
		DataProvider.getInstance(mContext).beginTransaction();
		try {
			for (MessageInfo messageInfo : messageInfos) {
				deleteMessageInfo(messageInfo);
			}
			DataProvider.getInstance(mContext).setTransactionSuccessful();
			secsuss = true;

		} catch (Exception e) {
			secsuss = false;
		} finally {
			DataProvider.getInstance(mContext).endTransaction();
		}
		return secsuss;
	}

	public boolean hasData() {
		Cursor cursor = mDataProvider.getAllRecord(MessageTable.TABLENAME);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				cursor.close();
				return true;
			} else {
				cursor.close();
				return false;
			}
		}
		return false;
	}

	/**
	 * 查询用戶信息
	 * 
	 * @param number
	 */
	
	public ArrayList<MessageInfo> quertByGroupBelong(String groupBelong) {
		Cursor cursor = mDataProvider.getRecordByString(MessageTable.TABLENAME,
				MessageTable.GROUPBELONG, groupBelong);
		MessageInfo messageInfo = null;
		ArrayList<MessageInfo> messageInfos = new ArrayList<MessageInfo>();
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						messageInfo = createMessageInfo(cursor);
						messageInfos.add(messageInfo);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return messageInfos;
	}

	/**
	 * 更新用户的信息
	 * 
	 * @param carInfo
	 */
	public void updateInfo(MessageInfo messageInfo) {
		ContentValues contentValues = createValues(messageInfo);
		String selection = MessageTable.MESSSAGEID + " = " + "'" + messageInfo.getMesssageid() + "'";
		mDataProvider.updateRecord(MessageTable.TABLENAME, contentValues,
				selection);
		contentValues.clear();
		contentValues = null;
	}

	/**
	 * 创建用户信息的ContentValues
	 * 
	 * @param userInfo
	 * @return
	 */
	private ContentValues createValues(MessageInfo messageInfo) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(MessageTable.SENDTIME, messageInfo.getSendtime());
		contentValues.put(MessageTable.MESSSAGEID, messageInfo.getMesssageid());
		contentValues.put(MessageTable.TITLE, messageInfo.getTitle());
		contentValues.put(MessageTable.CONTENT, messageInfo.getContent());
		contentValues.put(MessageTable.GROUPBELONG, messageInfo.getGroupBelong());
		contentValues.put(MessageTable.MODULENAME, messageInfo.getModuleName());
		contentValues.put(MessageTable.IDENTIFIER, messageInfo.getIdentifier());
		contentValues.put(MessageTable.NOTICEID, messageInfo.getNoticeid());
		contentValues.put(MessageTable.ATTACHMENT, messageInfo.getAttachment());
		int hasRead = messageInfo.isHasread() ? 1 : 0;
		contentValues.put(MessageTable.HASREAD, hasRead);
		int linkable = messageInfo.isLinkable() ? 1 : 0;
		contentValues.put(MessageTable.LINKABLE, linkable);
		contentValues.put(MessageTable.MODULEURL, messageInfo.getModuleurl());
		return contentValues;
	}
	
	/**
	 * 创建用户信息的userInfo
	 * 
	 * @param cursor
	 * @return
	 */
	private MessageInfo createMessageInfo(Cursor cursor) throws Exception {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setSendtime(cursor.getLong(cursor
				.getColumnIndex(MessageTable.SENDTIME)));
		messageInfo.setMesssageid(cursor.getString(cursor
				.getColumnIndex(MessageTable.MESSSAGEID)));
		messageInfo.setTitle(cursor.getString(cursor
				.getColumnIndex(MessageTable.TITLE)));
		messageInfo.setContent(cursor.getString(cursor
				.getColumnIndex(MessageTable.CONTENT)));
		messageInfo.setGroupBelong(cursor.getString(cursor
				.getColumnIndex(MessageTable.GROUPBELONG)));
		messageInfo.setModuleName(cursor.getString(cursor
				.getColumnIndex(MessageTable.MODULENAME)));
		messageInfo.setNoticeid(cursor.getString(cursor
				.getColumnIndex(MessageTable.NOTICEID)));
		messageInfo.setAttachment(cursor.getString(cursor
				.getColumnIndex(MessageTable.ATTACHMENT)));
		messageInfo.setIdentifier(cursor.getString(cursor
				.getColumnIndex(MessageTable.IDENTIFIER)));
		messageInfo.setModuleurl(cursor.getString(cursor
				.getColumnIndex(MessageTable.MODULEURL)));
		
		boolean hasRead = false;
		int hasReadInt = cursor.getInt(cursor
				.getColumnIndex(MessageTable.HASREAD));
		if (hasReadInt == 1){
			hasRead = true;
		}
		messageInfo.setHasread(hasRead);
		boolean linkable = false;
		int linkableInt = cursor.getInt(cursor
				.getColumnIndex(MessageTable.LINKABLE));
		if (linkableInt == 1){
			linkable = true;
		}
		messageInfo.setLinkable(linkable);
		messageInfo.setModuleurl(cursor.getString(cursor
				.getColumnIndex(MessageTable.MODULEURL)));
		return messageInfo;
	}
	
}
