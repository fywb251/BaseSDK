package com.zdnst.bsl.util;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * @Title: Preferences.java
 * @Description: 偏好控制类
 * @author
 */
public class Preferences
{
	private static Preferences instance = null;
	
	static SharedPreferences preference;
	public static Preferences getInstance(Context context){
		if(instance == null){
			instance = new Preferences(context);
		}
		return instance;
	}
	
	public Preferences(Context context) {
		preference = PreferenceManager.getDefaultSharedPreferences(context); 
	}
	
	public static final String APPMAINVIEW ="appmainview";
	
	/** 第一次安装运行应用标识*/
    public static final String FIRST_TIME ="fristTime";
    /** 存储于SP文件中的用户名USERNAME值 */
    public static final String USERNAME = "username";
    /** 存储当前登录的用户名 */
    public static final String CURRENT_USERNAME = "currentUserName";
    
    /** 存储于SP文件中的用户名PASSWORD值 */
    public static final String PASSWORD = "password";
    
    public static final String SESSIONID ="sessionId";
    
    public static final String PASSWORD_BAK = "passwordbak";
    
    public static final String ISREMEMBER = "isremember";
//    /** 存储于SP文件中的密码TOKEN值 */
//    public static final String TOKEN = "token";
    /** 存储于SP文件中的密码SESSION值 */
    public static final String SESSION = "session";
    
    /** 存储于SP文件中的聊天的JID值 */
    public static final String CHATJID = "chatjid";
    
    /** 存储当前用户Jid*/
    public static final String JID= "jid";
    /** 中文名称 */
    public static final String ZHNAME = "zhName";
    /** 性别 */
    public static final String SEX = "sex";
    /** 电话 */
    public static final String PHONE = "phone";
    /** 用户标签 */
    public static final String PRIVILEGES = "privileges";
    
    public static final String TOKEN = "appToken";
    
    public static final String EXPIRED = "expired";
    /** 用户昵称 */
    public static final String EXPERT_DATE = "expertDate";
    
    /** openfire服务器名称 */
    public static final String SERVICENAME = "serviceName";
    
    /** appbuild */
    public static final String VERSIONCODE = "versionCode";
    
    /** CUBEJSON */
    public static final String CUBEJSON = "cubejson";
    
    public static void saveFirsttime(Boolean fristTime) {
    	Editor editor = preference.edit();
        editor.putBoolean(FIRST_TIME, fristTime);
        editor.commit();
    }
    public static void saveSessionID(Long sessionid ) {
    	Editor editor = preference.edit();
        editor.putLong(SESSIONID, sessionid);
        editor.commit();
    }
    public static Long getSessionID( ) {
    	return preference.getLong(SESSIONID, 0);
    }
    
    public static String getExpired( ) {
    	return preference.getString(EXPIRED, "");
    }
    
    public static void saveToken(String token,String expired) {
    	Editor editor = preference.edit();
        editor.putString(TOKEN, token);
        editor.putString(EXPIRED, expired);
        editor.commit();
    }
    public static String getToken( ) {
    	return preference.getString(TOKEN, "");
    }
    
    public static void saveAppMainView(Boolean isView ) {
    	Editor editor = preference.edit();
        editor.putBoolean(APPMAINVIEW, isView);
        editor.commit();
    }
    public static boolean getAppMainView( ) {
    	
   	 return preference.getBoolean(APPMAINVIEW, false);
   }
    
    public static boolean getFirsttime( ) {
    	
    	 return preference.getBoolean(FIRST_TIME, true);
    }
    public static void saveUserInfo(String username, String session)
    {
        Editor editor = preference.edit();
        editor.putString(USERNAME, username);
        editor.putString(SESSION, session);
//        editor.putString(EXPERT_DATE, expertDate);
        editor.commit();
    }
    
    /* 记住账户实现* */
    public static void saveUser(String password, String username)
    {
        Editor editor = preference.edit();
        editor.putString(PASSWORD, password);
        editor.putString(USERNAME, username);
        editor.commit();
    }
    
    /* 记住账户实现* */
    public static void saveUser(String username)
    {
        Editor editor = preference.edit();
        editor.putString(USERNAME, username);
        editor.commit();
    }
    
    /* 记住账户实现* */
    public static void saveUserJid(String userJid)
    {
        Editor editor = preference.edit();
        editor.putString(JID, userJid);
        editor.commit();
    }
    
    
    
    /* 记住账户实现* */
    public static void saveUser(String password, String username,boolean isRemember)
    {
        Editor editor = preference.edit();
        editor.putString(PASSWORD, password);
        editor.putString(USERNAME, username);
        editor.putBoolean(ISREMEMBER,isRemember);
        editor.commit();
    }
    
    public static void savePWD(String password)
    {
    	 Editor editor = preference.edit();
         editor.putString(PASSWORD_BAK, password);
         editor.commit();
    }
    
    
    public static void saveAutoDownload(String UserName,boolean auto) {
    	 Editor editor = preference.edit();
         editor.putBoolean(UserName, auto);
         editor.commit();
    }
    
    public static Boolean getAutoDownload(String UserName)
    {
        return preference.getBoolean(UserName, true);
    }
    
    
    /**
     * 从SP文件中获取用户名
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户名
     */
    public static String getUserName()
    {
        return preference.getString(USERNAME, "");
    }
    
    /**
     * 从SP文件中获取用户密码
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户名
     */
    public static String getPassword()
    {
        return preference.getString(PASSWORD, "");
    }
    /**
     * 从SP文件中获取用户密码
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户名
     */
    public static String getPasswordbak()
    {
        return preference.getString(PASSWORD_BAK, "");
    }
    /**
     * 从SP文件中获取SESSION
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户密码
     */
    public static String getSESSION()
    {
        return preference.getString(SESSION, "");
    }
    
    /**
     * 从SP文件中获取SESSION
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户密码
     */
    public static String getUserJID()
    {
        return preference.getString(JID, "");
    }
    
    /**
     * 从SP文件中获取用户密码
     * 
     * @param preference 要取值的SP文件对象
     * @return 用户密码
     */
    public static String getExpertDate()
    {
        return preference.getString(EXPERT_DATE, "");
    }
    
    
    public static void putIsDeleteFloder(boolean isDelete){
    	Editor editor = preference.edit();
        editor.putBoolean("IsDeleteFloder",isDelete );
        editor.commit();
    }
    
    public static boolean getIsDeleteFloder(){
    	return preference.getBoolean("IsDeleteFloder", false);
    }
    
    public static boolean getIsRemember(){
    	return preference.getBoolean(ISREMEMBER, false);
    	
    }
    public static void saveCurrentUserName(String username)
    {
        Editor editor = preference.edit();
        editor.putString(CURRENT_USERNAME, username);
        editor.commit();
    }
    public static String getCurrentUserName () {
    	
    	 return preference.getString(CURRENT_USERNAME, "");
    }
    
    /* 记住当前聊天对像* */
    public static void saveChatJid(String chatjid)
    {
        Editor editor = preference.edit();
        editor.putString(CHATJID, chatjid);
        editor.commit();
    }
    
    public static String getChatJid () {
   	 return preference.getString(CHATJID, "");
   }
    
   
    /**
     * 保存用户标签
     * @param privileges
     * @param preference
     */
    public static void savePrivileges(String privileges)
    {
    	 Editor editor = preference.edit();
         editor.putString(PRIVILEGES, privileges);
         editor.commit();
    }
    /**
     * 获取用户标签
     * @param preference
     * @return
     */
    public static String getPrivileges ( ) {
    	
   	 return preference.getString(PRIVILEGES, "");
   }
    
    /**
     * 保存openfire服务名称
     */
    public static void saveServiceName(String serviceName)
    {
    	 Editor editor = preference.edit();
         editor.putString(SERVICENAME, serviceName);
         editor.commit();
    }
    /**
     * 获取openfire服务名称
     * @param preference
     * @return
     */
    public static String getServiceName() {
    	
   	 return preference.getString(SERVICENAME, "");
   }
    
    /**
     * 获取versionCode
     * @return
     */
    public static int getVersionCode() {
   	 return preference.getInt(VERSIONCODE, 0);
   }
    /**
     * 保存versionCode
     */
    public static void saveVersionCode(int versionCode )
    {
    	 Editor editor = preference.edit();
         editor.putInt(VERSIONCODE, versionCode);
         editor.commit();
    }
    
    /**
     * 获取CubeJson
     * @return
     */
    public static String getCubeJson( ) {
   	 return preference.getString(CUBEJSON, "");
   }
    /**
     * 保存CubeJson
     */
    public static void saveCubeJson(String cubejson )
    {
    	 Editor editor = preference.edit();
         editor.putString(CUBEJSON, cubejson);
         editor.commit();
    }
    
}
