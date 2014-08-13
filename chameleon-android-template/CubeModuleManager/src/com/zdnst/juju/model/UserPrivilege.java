package com.zdnst.juju.model;

import java.util.ArrayList;


public class UserPrivilege {  
	
	public final static String GET = "GET";
	
	public final static String DELETE = "DELETE";
	
	private static UserPrivilege sUserPrivilege;
	
	private ArrayList<String> roleList;
	
	private ArrayList<String> getList;
	
	private ArrayList<String> deleteList;
	
	private ArrayList<String> privilegeList;

	static public synchronized final UserPrivilege getInstance() {
		if (null == sUserPrivilege) {
			sUserPrivilege = new UserPrivilege();
		}
		return sUserPrivilege;
	}
	
	

	public UserPrivilege() {
		super();
		roleList = new ArrayList<String>();
		getList = new ArrayList<String>();
		deleteList = new ArrayList<String>();
		privilegeList = new ArrayList<String>();
	}



	public ArrayList<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(ArrayList<String> roleList) {
		this.roleList = roleList;
	}

	public ArrayList<String> getGetList() {
		return getList;
	}

	public void setGetList(ArrayList<String> getList) {
		this.getList = getList;
	}

	public ArrayList<String> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(ArrayList<String> deleteList) {
		this.deleteList = deleteList;
	}

	public ArrayList<String> getPrivilegeList() {
		return privilegeList;
	}

	public void setPrivilegeList(ArrayList<String> privilegeList) {
		this.privilegeList = privilegeList;
	}
	
}
