package org.acra;


import android.os.Environment;

import com.midea.mmp2.R;



/**
 * 错误报告配置 通过这个类配置
 * 
 * @author kuangsunny
 * 
 */
public final class CrashReportConfig {
	/** 通知栏用到的资源 */
	public final static int RES_NOTIF_ICON = android.R.drawable.stat_notify_error;
	public final static int RES_NOTIF_TICKER_TEXT = R.string.crash_notif_ticker_text;
	public final static int RES_NOTIF_TITLE = R.string.crash_notif_title;
	public final static int RES_NOTIF_TEXT = R.string.crash_notif_text;

	/** 对话框用到的资源 */
	public final static int RES_DIALOG_ICON = android.R.drawable.ic_dialog_info;
	public final static int RES_DIALOG_TITLE = R.string.crash_dialog_title;
	public final static int RES_DIALOG_TEXT = R.string.crash_dialog_text;

	/** 邮件标题的字符串id */
	public final static int RES_EMAIL_SUBJECT = R.string.crash_subject;

	/** 收件邮箱 */
	public final static String EMAIL_RECEIVER = "kuanghaojun@foreveross.com";


	/** 程序名 */
	public final static String APP_NAME = "com.zdnst.chameleon";

	
	
	/** 崩溃日志保存路径 */
	public final static String LOG_PATH = 
			Environment.getExternalStorageDirectory().getPath() + "/" + "CUBESDK"  +"/Log/";

	/**
	 * 是否搜集额外的包信息 为ture需要配置 {@link #ADDITIONAL_TAG} 和
	 * {@link #ADDITIONAL_PACKAGES}
	 * */
	public final static boolean REPORT_ADDITIONAL_INFO = true;

	/** 额外的程序包标签 */
	public final static String ADDITIONAL_TAG = "GOWidget";

	/** 额外显示的包信息 (eg.GOWidget) */
	public final static String[] ADDITIONAL_PACKAGES = {

	};
}
