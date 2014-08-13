package com.zdnst.zdnstsdk.config;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Preferences
{
	
	public static final String APPMAINVIEW ="appmainview";
	
    public static final String FIRST_TIME ="fristTime";
    public static final String USERNAME = "username";
    public static final String CURRENT_USERNAME = "currentUserName";
    
    public static final String PASSWORD = "password";
    
    public static final String SESSIONID ="sessionId";
    
    public static final String PASSWORD_BAK = "passwordbak";
    
    public static final String ISREMEMBER = "isremember";
//    public static final String TOKEN = "token";
    public static final String SESSION = "session";
    
    public static final String CHATJID = "chatjid";
    
    public static final String JID= "jid";
    public static final String ZHNAME = "zhName";
    public static final String SEX = "sex";
    public static final String PHONE = "phone";
    public static final String PRIVILEGES = "privileges";
    
    public static final String TOKEN = "appToken";
    
    public static final String EXPIRED = "expired";
    public static final String EXPERT_DATE = "expertDate";
    public static void saveFirsttime(Boolean fristTime,SharedPreferences preference) {
    	Editor editor = preference.edit();
        editor.putBoolean(FIRST_TIME, fristTime);
        editor.commit();
    }
    public static void saveSessionID(Long sessionid,SharedPreferences preference) {
    	Editor editor = preference.edit();
        editor.putLong(SESSIONID, sessionid);
        editor.commit();
    }
    public static Long getSessionID(SharedPreferences preference) {
    	return preference.getLong(SESSIONID, 0);
    }
    
    public static String getExpired(SharedPreferences preference) {
    	return preference.getString(EXPIRED, "");
    }
    
    public static void saveToken(String token,String expired,SharedPreferences preference) {
    	Editor editor = preference.edit();
        editor.putString(TOKEN, token);
        editor.putString(EXPIRED, expired);
        editor.commit();
    }
    public static String getToken(SharedPreferences preference) {
    	return preference.getString(TOKEN, "");
    }
    
    public static void saveAppMainView(Boolean isView,SharedPreferences preference) {
    	Editor editor = preference.edit();
        editor.putBoolean(APPMAINVIEW, isView);
        editor.commit();
    }
    public static boolean getAppMainView(SharedPreferences preference) {
    	
   	 return preference.getBoolean(APPMAINVIEW, false);
   }
    
    public static boolean getFirsttime(SharedPreferences preference) {
    	
    	 return preference.getBoolean(FIRST_TIME, true);
    }
    public static void saveUserInfo(String username, String session, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(USERNAME, username);
        editor.putString(SESSION, session);
//        editor.putString(EXPERT_DATE, expertDate);
        editor.commit();
    }
    
    public static void saveUser(String password, String username, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(PASSWORD, password);
        editor.putString(USERNAME, username);
        editor.commit();
    }
    
    public static void saveUserJid(String userJid, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(JID, userJid);
        editor.commit();
    }
    
    
    
    public static void saveUser(String password, String username,boolean isRemember, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(PASSWORD, password);
        editor.putString(USERNAME, username);
        editor.putBoolean(ISREMEMBER,isRemember);
        editor.commit();
    }
    
    public static void savePWD(String password,SharedPreferences preference)
    {
    	 Editor editor = preference.edit();
         editor.putString(PASSWORD_BAK, password);
         editor.commit();
    }
    
    
    public static void saveAutoDownload(String UserName,boolean auto,SharedPreferences preference) {
    	 Editor editor = preference.edit();
         editor.putBoolean(UserName, auto);
         editor.commit();
    }
    
    public static Boolean getAutoDownload(String UserName ,SharedPreferences preference)
    {
        return preference.getBoolean(UserName, true);
    }
    
    
    public static String getUserName(SharedPreferences preference)
    {
        return preference.getString(USERNAME, "");
    }
    
    public static String getPassword(SharedPreferences preference)
    {
        return preference.getString(PASSWORD, "");
    }
    public static String getPasswordbak(SharedPreferences preference)
    {
        return preference.getString(PASSWORD_BAK, "");
    }
    public static String getSESSION(SharedPreferences preference)
    {
        return preference.getString(SESSION, "");
    }
    
    public static String getUserJID(SharedPreferences preference)
    {
        return preference.getString(JID, "");
    }
    
    public static String getExpertDate(SharedPreferences preference)
    {
        return preference.getString(EXPERT_DATE, "");
    }
    
    
    public static void putIsDeleteFloder(SharedPreferences preference,boolean isDelete){
    	Editor editor = preference.edit();
        editor.putBoolean("IsDeleteFloder",isDelete );
        editor.commit();
    }
    
    public static boolean getIsDeleteFloder(SharedPreferences preference){
    	return preference.getBoolean("IsDeleteFloder", false);
    }
    
    public static boolean getIsRemember(SharedPreferences preference){
    	return preference.getBoolean(ISREMEMBER, false);
    	
    }
    public static void saveCurrentUserName(String username, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(CURRENT_USERNAME, username);
        editor.commit();
    }
    public static String getCurrentUserName (SharedPreferences preference) {
    	
    	 return preference.getString(CURRENT_USERNAME, "");
    }
    
    public static void saveChatJid(String chatjid, SharedPreferences preference)
    {
        Editor editor = preference.edit();
        editor.putString(CHATJID, chatjid);
        editor.commit();
    }
    
    public static String getChatJid (SharedPreferences preference) {
   	 return preference.getString(CHATJID, "");
   }
    
    public static void saveZhName(String zhName,SharedPreferences preference)
    {
    	 Editor editor = preference.edit();
         editor.putString(ZHNAME, zhName);
         editor.commit();
    }
    public static void saveSex(String sex,SharedPreferences preference)
    {
    	 Editor editor = preference.edit();
         editor.putString(SEX, sex);
         editor.commit();
    }
    public static void savePhone(String phone,SharedPreferences preference)
    {
    	 Editor editor = preference.edit();
         editor.putString(PHONE, phone);
         editor.commit();
    }
    public static void savePrivileges(String privileges,SharedPreferences preference)
    {
    	 Editor editor = preference.edit();
         editor.putString(PRIVILEGES, privileges);
         editor.commit();
    }
    public static String getZhName (SharedPreferences preference) {
    	
   	 return preference.getString(ZHNAME, "");
   }
    public static String getSex (SharedPreferences preference) {
    	
   	 return preference.getString(SEX, "");
   }
    public static String getPhone (SharedPreferences preference) {
    	
   	 return preference.getString(PHONE, "");
   }
    public static String getPrivileges (SharedPreferences preference) {
    	
   	 return preference.getString(PRIVILEGES, "");
   }
    
    
    
}
