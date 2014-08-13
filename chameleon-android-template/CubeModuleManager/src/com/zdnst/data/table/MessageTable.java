package com.zdnst.data.table;



public class MessageTable {
	
	/**
	 * 表名
	 */
	public static final String TABLENAME = "MessageTable";

	/**
	 * 表字段: 用户名
	 */
	public static final String SENDTIME = "sendtime";
	
	public static final String MESSSAGEID = "messsageid";
	
	public static final String TITLE = "title";
	
	public static final String CONTENT = "content";
	
	public static final String GROUPBELONG = "groupbelong";
	
	public static final String MODULENAME = "modulename";
	
	public static final String IDENTIFIER = "identifier";
	
	public static final String NOTICEID = "noticeid";
	
	public static final String ATTACHMENT = "attachment";
	
	public static final String HASREAD = "hasread";
	
	public static final String LINKABLE = "linkable";
	
	public static final String MODULEURL = "moduleurl";
	
	public static final String MESSAGETABLESQL = "create table MessageTable " + "("
			+ "sendtime numeric, "  + "messsageid text, " + "title text, " + "content text, " 
			+ "groupbelong text, " + "modulename text, " + "identifier text, " 
			+ "noticeid text, " + "attachment text, "
			+ "hasread numeric, "  + "linkable numeric, " + "moduleurl text " +  ")";
}
