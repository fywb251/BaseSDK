package com.zdnst.zdnstsdk.config;

import java.util.Properties;

import android.content.Context;
import android.util.Log;

public class PropertiesUtil {
	private PropertiesUtil() {

	}

	private Properties props = null;

	public static PropertiesUtil readProperties(Context context,
			int rawResourceId) {

		PropertiesUtil propertiesUtil = new PropertiesUtil();
		propertiesUtil.props = new Properties();
		try {

			propertiesUtil.props.load(context.getResources().openRawResource(
					rawResourceId));
		} catch (Exception e) {
			Log.e("PropertiesUtil", "Could not find the properties file.", e);
		}
		return propertiesUtil;
	}

	public String getString(String key, String def) {
		String value = props.getProperty(key);
		return value == null ? def : value;
	}

	public Boolean getBoolean(String key, Boolean def) {
		String value = props.getProperty(key);
		return value == null ? def : Boolean.valueOf(value);
	}

	public Byte getByte(String key, Byte def) {
		String value = props.getProperty(key);
		return value == null ? def : Byte.valueOf(value);
	}

	public Short getShort(String key, Short def) {
		String value = props.getProperty(key);
		return value == null ? def : Short.valueOf(value);
	}

	public Integer getInteger(String key, Integer def) {
		String value = props.getProperty(key);
		return value == null ? def : Integer.valueOf(value);
	}

	public Long getLong(String key, Long def) {
		String value = props.getProperty(key);
		return value == null ? def : Long.valueOf(value);
	}

	public boolean containsValue(String key) {
		return props.containsKey(key);
	}

}
