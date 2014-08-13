package com.zdnst.push.client;


public class TagEntryVo {
	
	private String key;
	private String value;
	
	public TagEntryVo() {
	}
	
	public TagEntryVo(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
