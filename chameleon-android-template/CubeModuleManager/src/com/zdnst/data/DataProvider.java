package com.zdnst.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.zdnst.push.tool.PropertiesUtil;
import com.zdnst.zdnstsdk.config.CubeConstants;

public class DataProvider {

	private DatabaseHelper mDBOpenHelper;
	private int mCurDBVersion;
	private String mDBName;
	private Context mContext;
	private static DataProvider sDataProviderSelf;

	static public synchronized final DataProvider getInstance(Context context) {
		if (null == sDataProviderSelf) {
			sDataProviderSelf = new DataProvider(context);
		}
		return sDataProviderSelf;
	}
	static public synchronized final DataProvider getInstance(Context context,String dbName) {
		if (null == sDataProviderSelf) {
			sDataProviderSelf = new DataProvider(context,dbName);
		}
		return sDataProviderSelf;
	}

	private DataProvider(Context context) {
		mContext = context;
		mDBName = PropertiesUtil.readProperties(mContext, CubeConstants.CUBE_CONFIG)
				.getString("STORE_DB_NAME", "CUBE");
		mCurDBVersion = Integer.valueOf(PropertiesUtil.readProperties(mContext, CubeConstants.CUBE_CONFIG)
				.getString("STORE_DB_VERSION", "1"));
		mDBOpenHelper = new DatabaseHelper(mContext, mDBName + "new", mCurDBVersion);
	}
	private DataProvider(Context context,String dbName) {
		mContext = context;
		mDBName = PropertiesUtil.readProperties(mContext, CubeConstants.CUBE_CONFIG)
				.getString("STORE_DB_NAME", "CUBE");
		mCurDBVersion = Integer.valueOf(PropertiesUtil.readProperties(mContext, CubeConstants.CUBE_CONFIG)
				.getString("STORE_DB_VERSION", "1"));
		mDBOpenHelper = new DatabaseHelper(mContext, dbName, mCurDBVersion);
	}

	// 添加表记录
	public boolean addRecord(String tableName, ContentValues values) {
		try {
			mDBOpenHelper.insert(tableName, values);
			return true;
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 添加表记录
	public long insertRecord(String tableName, ContentValues values) {
		try {
			return mDBOpenHelper.insert(tableName, values);

		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	// 更新表记录
	public boolean updateRecord(String tableName, ContentValues values,
			String selection) {
		try {
			mDBOpenHelper.update(tableName, values, selection, null);
			return true;
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return false;
	}

	// 删除表记录
	public boolean delRecord(String tableName, String selection) {
		try {
			mDBOpenHelper.delete(tableName, selection, null);
			return true;
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Cursor getAllRecord(String tableName) {
		return mDBOpenHelper.query(tableName, null, null, null, null);
	}

	public Cursor getRecordById(String tableName, String selectionId, int id) {
		String selection = selectionId + " = " + id;
		return mDBOpenHelper.query(tableName, null, selection, null, null);
	}
	
	public Cursor getRecordByString(String tableName, String selectionKey, String selectionString) {
		String selection = selectionKey + " = " + selectionString;
		return mDBOpenHelper.query(tableName, null, selection, null, null);
	}


	public Cursor getAllRecord(String tableName, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		return mDBOpenHelper.query(tableName, projection, selection,
				selectionArgs, sortOrder);
	}

	public void beginTransaction() {
		mDBOpenHelper.beginTransaction();
	}

	public void setTransactionSuccessful() {
		mDBOpenHelper.setTransactionSuccessful();
	}

	public void endTransaction() {
		mDBOpenHelper.endTransaction();
	}

	/**
	 * 关闭数据库，释放数据库资源
	 */
	public void close() {
		if (mDBOpenHelper != null) {
			try {
				mDBOpenHelper.close();
			} catch (Exception e) {
				// do nothing
				e.printStackTrace();
			}
		}
	}

	/**
	 * open数据库，使用外部可读模式
	 */
	public boolean openWithWorldReadable() {
		if (mDBOpenHelper != null) {
			try {
				return mDBOpenHelper.openDBWithWorldReadable();
			} catch (Exception e) {
				// do nothing
			}
		}
		return false;
	}

	/**
	 * open数据库，使用默认的MODE_PRIVATE模式
	 */
	public void openWithDefaultMode() {
		if (mDBOpenHelper != null) {
			try {
				close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	public static void destroy() {
		if (sDataProviderSelf != null) {
			sDataProviderSelf.close();
		}
		sDataProviderSelf = null;
	}
}
