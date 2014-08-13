package com.zdnst.data.table;

public class UserInfoTable {
	/**
	 * 表名
	 */
	public static final String TABLENAME = "userifno";

	/**
	 * 表字段: 用户名
	 */
	public static final String USERNAME = "username";
	
	public static final String PASSWORD = "password";
	
	public static final String USERINFOTABLESQL = "create table userinfo " + "("
			+ "username text, "  + "password text " + ")";
}
