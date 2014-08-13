package com.zdnst.data;

import java.util.ArrayList;

import com.zdnst.data.table.MessageStubTable;
import com.zdnst.data.table.MessageTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private final static int DB_VERSION_ONE = 1;

	private final static int DB_VERSION_MAX = 1;
	private final static String DATABASE_NAME = "chameleon.db";


	// just for test
	private boolean mIsNewDB = false;

	// 可以执行多表关联查询
	SQLiteQueryBuilder msqlQB = null;

	private final Context mContext;

	private boolean mUpdateResult = true; // 更新数据库结果，默认是成功的。

	public DatabaseHelper(Context context, String dataBaseName,
			int dataBaseVersion) {
		super(context, dataBaseName, null, dataBaseVersion);
		mContext = context;
		// just for test
		msqlQB = new SQLiteQueryBuilder();
		SQLiteDatabase db = null;
		try {
			db = getWritableDatabase();
			if (!mUpdateResult) {
				// 更新失败，则删除数据库，再行创建。
				if (db != null) {
					db.close();
				}
				mContext.deleteDatabase(DATABASE_NAME);
				// mContext.openOrCreateDatabase(DATABASE_NAME,
				// Context.MODE_PRIVATE, null);
				getWritableDatabase();
			}
		} catch (SQLiteException ex) {
		} catch (IllegalStateException ie) {
			// TODO 数据库打开失败，是否要重启应用程序？？？？
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		mUpdateResult = true;
		mIsNewDB = true;
		db.execSQL(MessageTable.MESSAGETABLESQL);
		db.execSQL(MessageStubTable.MESSAGESTUBTABLESQL);
		
	}

	private boolean isExistTable(final SQLiteDatabase db, String tableName) {
		boolean result = false;
		Cursor cursor = null;
		String where = "type='table' and name='" + tableName + "'";
		try {
			cursor = db.query("sqlite_master", null, where, null, null, null,
					null);
			if (cursor != null && cursor.moveToFirst()) {
				result = true;
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return result;
	}



	// 只针对安卓3.0系统以上
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 默认支持向下兼容。（oldVersion = 2, newVersion = 1）
		// 后期在做版本降级处理时，在此可根据需要做相应处理
		Log.i("DatabaseHelper", "onDowngrade oldVersion=" + oldVersion
				+ ", newVersion=" + newVersion);
		return;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO:根据版本号，更新数据库表结构
		onUpgrade2(db, oldVersion, newVersion);
	}

	private void onUpgrade2(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion < DB_VERSION_ONE || oldVersion > newVersion || newVersion > DB_VERSION_MAX) {
			Log.i("testDataBase", "onUpgrade() false oldVersion = " + oldVersion
					+ ", newVersion = " + newVersion);
			return;
		}
		ArrayList<UpgradeDB> upgradeDBFuncS = new ArrayList<DatabaseHelper.UpgradeDB>();
		upgradeDBFuncS.add(new UpgradeDBOneToTwo());
		Log.i("testUpdate", "onupgrade");
		for (int i = (oldVersion - 1); i < (newVersion - 1); i++) {
			mUpdateResult = upgradeDBFuncS.get(i).onUpgradeDB(db);
			if (!mUpdateResult) {
				// 中间有任何一次升级失败，则直接返回
				break;
			}
		}
		upgradeDBFuncS.clear();
	}

	/**
	 * 用于单表查询
	 */
	public Cursor query(String tableName, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {

		return query(tableName, projection, selection, selectionArgs, null,
				null, sortOrder);
	}

	/**
	 * 用于单表查询
	 */
	public Cursor query(String tableName, String[] projection,
			String selection, String[] selectionArgs, String groupBy,
			String having, String sortOrder) {
		Cursor result = null;
		try {
			SQLiteDatabase db = getReadableDatabase();
			result = db.query(tableName, projection, selection, selectionArgs,
					groupBy, having, sortOrder);
		} catch (SQLException e) {
			Log.i("data", "SQLException when query in " + tableName + ", "
					+ selection);
		} catch (IllegalStateException e) {
			Log.i("data", "IllegalStateException when query in " + tableName
					+ ", " + selection);
		}
		return result;
	}

	/**
	 * 用于多表查询
	 */
	public Cursor queryCrossTables(String tableName, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		Cursor result = null;
		synchronized (msqlQB) {
			msqlQB.setTables(tableName);
			try {
				SQLiteDatabase db = getReadableDatabase();
				result = msqlQB.query(db, projection, selection, selectionArgs,
						null, null, sortOrder);
			} catch (SQLException e) {
				// e.printStackTrace();
				Log.i("data", "SQLException when query in " + tableName + ", "
						+ selection);
			} catch (IllegalStateException e) {
				// e.printStackTrace();
				Log.i("data", "IllegalStateException when query in "
						+ tableName + ", " + selection);
			}
		}
		return result;
	}

	public long insert(String tableName, ContentValues initialValues)
			throws DatabaseException {
		SQLiteDatabase db = getWritableDatabase();
		long rowId = 0;
		try {
			rowId = db.insert(tableName, null, initialValues);
		} catch (Exception e) {
			Log.i("data", "Exception when insert in " + tableName);
			throw new DatabaseException(e);
		}
		return rowId;
	}

	public int delete(String tableName, String selection, String[] selectionArgs)
			throws DatabaseException {
		SQLiteDatabase db = getWritableDatabase();
		int count = 0;
		try {
			count = db.delete(tableName, selection, selectionArgs);
		} catch (Exception e) {
			Log.i("data", "Exception when delete in " + tableName + ", "
					+ selection);
			throw new DatabaseException(e);
		}
		return count;
	}

	public int update(String tableName, ContentValues values, String selection,
			String[] selectionArgs) throws DatabaseException {
		SQLiteDatabase db = getWritableDatabase();
		int count = 0;
		try {
			count = db.update(tableName, values, selection, selectionArgs);
		} catch (Exception e) {
			Log.i("data", "Exception when update in " + tableName + ", "
					+ selection);
			throw new DatabaseException(e);
		}
		return count;
	}

	/**
	 * 
	 * @param sql
	 * @throws DatabaseException
	 */
	public void exec(String sql) throws DatabaseException {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			Log.i("data", "Exception when exec " + sql);
			throw new DatabaseException(e);
		}
	}

	public boolean isNewDB() {
		return mIsNewDB;
	}

	public static String getDBName() {
		return DATABASE_NAME;
	}

	/**
	 * <br>
	 * 功能简述: <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public boolean openDBWithWorldReadable() {
		try {
			close();
			if (mContext.openOrCreateDatabase(DATABASE_NAME,
					Context.MODE_WORLD_READABLE, null) == null) {
				return false;
			}
		} catch (Exception e) {
			// TODO: handle exception
			close();
			return false;
		}
		return true;
	}

	public void beginTransaction() {
		try {
			SQLiteDatabase db = getWritableDatabase();
			if (null != db) {
				db.beginTransaction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTransactionSuccessful() {
		try {
			SQLiteDatabase db = getWritableDatabase();
			if (null != db) {
				db.setTransactionSuccessful();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void endTransaction() {
		try {
			SQLiteDatabase db = getWritableDatabase();
			db.endTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getDbCurrVersion() {
		return DB_VERSION_MAX;
	}

	/**
	 * 抽象升级类，只在每个连续的数据库版本间作升级
	 * 
	 * @author 
	 * 
	 */
	abstract class UpgradeDB {
		abstract boolean onUpgradeDB(SQLiteDatabase db);
	}

	/**
	 * 更新：1到2
	 * @author 
	 *
	 */
	class UpgradeDBOneToTwo extends UpgradeDB {
		@Override
		boolean onUpgradeDB(SQLiteDatabase db) {
			return onUpgrade1To2(db);
		}
	}
	
	/**
	 * 从1版升级到2版
	 * 
	 * @author sunny
	 * @param db
	 */
	private boolean onUpgrade1To2(SQLiteDatabase db) {
		boolean result = false;
		return result;
	
	}

}