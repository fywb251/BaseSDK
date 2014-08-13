package com.zdnst.data.table;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zdnst.data.DataProvider;
import com.zdnst.module.UserInfo;

public class UserInfoDataModel {

	// 上下文
	protected Context mContext;
	protected String mDBName;

	// 数据持久化执行者
	protected DataProvider mDataProvider = null;

	public UserInfoDataModel(Context context,String mDBName) {
		mContext = context;
		mDataProvider = DataProvider.getInstance(context,mDBName);
	}

	/**
	 * 查询车辆类型
	 * 
	 * @param selection
	 *            orderby
	 */
	public ArrayList<UserInfo> getAllUserInfo() {
		Cursor cursor = mDataProvider.getAllRecord(UserInfoTable.TABLENAME,
				null, null, null, null);
		ArrayList<UserInfo> userInfos = new ArrayList<UserInfo>();
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						UserInfo userInfo = createUserInfo(cursor);
						userInfos.add(userInfo);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return userInfos;
	}

	/**
	 * 添加用戶的信息
	 * 
	 * @param userInfo
	 */
	public int addUserInfo(UserInfo userInfo) {
		if (userInfo == null) {
			return -1;
		}
		ContentValues contentValues = createValues(userInfo);
		int id = (int) mDataProvider.insertRecord(UserInfoTable.TABLENAME,
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
	public boolean addUserInfoList(ArrayList<UserInfo> userInfos) {
		boolean ret = false;

		DataProvider.getInstance(mContext,mDBName).beginTransaction();
		try {

			// 将新的数据保存到播放列表中
			for (UserInfo userInfo : userInfos) {
				addUserInfo(userInfo);
			}
			DataProvider.getInstance(mContext,mDBName).setTransactionSuccessful();
			ret = true;
		} catch (Exception e) {
		} finally {
			DataProvider.getInstance(mContext,mDBName).endTransaction();
		}

		return ret;

	}

	/**
	 * 删除用户信息
	 * 
	 * @param number
	 */
	public void deleteUserInfo(UserInfo userInfo) {
		String selection = null;
		if (!"".equals(userInfo.userName)) {
			selection = UserInfoTable.USERNAME + " = " + userInfo.userName;
		}
		mDataProvider.delRecord(UserInfoTable.TABLENAME, selection);
	}
	
	/**
	 * 批量删除用戶信息
	 */
	public boolean deleteUserInfoList(ArrayList<UserInfo> userInfos) {
		boolean secsuss = false;
		if (userInfos == null) {
			return secsuss;
		}
		
		DataProvider.getInstance(mContext,mDBName).beginTransaction();
		try {
			for (UserInfo userInfo : userInfos) {
				deleteUserInfo(userInfo);
			}
			DataProvider.getInstance(mContext,mDBName).setTransactionSuccessful();
			secsuss = true;

		} catch (Exception e) {
			secsuss = false;
		} finally {
			DataProvider.getInstance(mContext,mDBName).endTransaction();
		}
		return secsuss;
	}

	public boolean hasData() {
		Cursor cursor = mDataProvider.getAllRecord(UserInfoTable.TABLENAME);
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
	
	public ArrayList<UserInfo> quertByNumber(String userName) {
		Cursor cursor = mDataProvider.getRecordByString(UserInfoTable.TABLENAME,
				UserInfoTable.USERNAME, userName);
		UserInfo userInfo = null;
		ArrayList<UserInfo> userInfos = new ArrayList<UserInfo>();
		if (cursor != null) {
			try {
				if (cursor.moveToFirst()) {
					do {
						userInfo = createUserInfo(cursor);
						userInfos.add(userInfo);
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				cursor.close();
			}
		}
		return userInfos;
	}

	/**
	 * 更新用户的信息
	 * 
	 * @param carInfo
	 */
	public void updateInfo(UserInfo userInfo) {
		ContentValues contentValues = createValues(userInfo);
		mDataProvider.updateRecord(UserInfoTable.TABLENAME, contentValues,
				UserInfoTable.USERNAME + " = " + userInfo.userName);
		contentValues.clear();
		contentValues = null;
	}

	/**
	 * 创建用户信息的ContentValues
	 * 
	 * @param userInfo
	 * @return
	 */
	private ContentValues createValues(UserInfo userInfo) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(UserInfoTable.USERNAME, userInfo.userName);
		contentValues.put(UserInfoTable.PASSWORD, userInfo.passWord);
		return contentValues;
	}

	/**
	 * 创建用户信息的userInfo
	 * 
	 * @param cursor
	 * @return
	 */
	private UserInfo createUserInfo(Cursor cursor) throws Exception {
		UserInfo userInfo = new UserInfo();
		userInfo.userName = cursor.getString(cursor
				.getColumnIndex(UserInfoTable.USERNAME));
		userInfo.passWord = cursor.getString(cursor
				.getColumnIndex(UserInfoTable.PASSWORD));
		return userInfo;
	}
}
