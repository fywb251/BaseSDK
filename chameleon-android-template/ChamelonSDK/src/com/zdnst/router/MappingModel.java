package com.zdnst.router;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

public class MappingModel {
	private String [] linkURL;
	private String [] parameters;
	private String pageIdentifier;
	
	
	

	public String[] getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String[] linkURL) {
		this.linkURL = linkURL;
	}


	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public String getPageIdentifier() {
		return pageIdentifier;
	}

	public void setPageIdentifier(String pageIdentifier) {
		this.pageIdentifier = pageIdentifier;
	}
	
	public void setParameters(String key) throws JSONException {
		  Pattern p = Pattern.compile(".+?(\\{.+?\\})");
		  Matcher m = p.matcher(key);
		  String parameter = "";
		  while(m.find()) {
			  String value = m.group(1);
			  parameter = parameter+"/"+value.replace("{", "").replace("}", "");
		  }
		  if(!parameter.equals("")&&parameter !=null) {
			  parameter = parameter.substring(1);
			  this.parameters = parameter.split("/");
		  }
	}
	
	public void setLinkUrlParameters(String key) throws JSONException{
		setParameters(key);
		setLinkURL(key);
	}
	
	public void setLinkURL(String key) {
		String value = key.split("\\{")[0];
		String url [] =value.substring(1).split("/");
		this.linkURL = url;
	}
	

}
