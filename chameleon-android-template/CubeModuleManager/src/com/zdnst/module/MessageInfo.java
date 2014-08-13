package com.zdnst.module;

import com.zdnst.router.MappingModel;
import com.zdnst.router.RoutingParserHelper;


public class MessageInfo {

	private long sendtime;
	private String messsageid;
	private String title;
	private String content;
	private String groupBelong;
	private String moduleName;
	private String noticeid;
	private String attachment;
	private boolean hasread;
	private boolean linkable;
	private String moduleurl;
	private String identifier;

	public long getSendtime() {
		return sendtime;
	}

	public void setSendtime(long sendtime) {
		this.sendtime = sendtime;
	}

	public String getMesssageid() {
		return messsageid;
	}

	public void setMesssageid(String messsageid) {
		this.messsageid = messsageid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getGroupBelong() {
		return groupBelong;
	}

	public void setGroupBelong(String groupBelong) {
		this.groupBelong = groupBelong;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getNoticeid() {
		return noticeid;
	}

	public void setNoticeid(String noticeid) {
		this.noticeid = noticeid;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public boolean isHasread() {
		return hasread;
	}

	public void setHasread(boolean hasread) {
		this.hasread = hasread;
	}

	public String getModuleurl() {
		return moduleurl;
	}

	public void setModuleurl(String moduleurl) {
		this.moduleurl = moduleurl;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public boolean isLinkable() {
		return linkable;
	}

	public void setLinkable(boolean linkable) {
		this.linkable = linkable;
	}
	public String [] getParams() {
		String []parameters = null ; 
		if(getModuleurl()!=null&&!getModuleurl().equals("")) {
			RoutingParserHelper r = new RoutingParserHelper();
			MappingModel mappingModel = r.redirectToPage(getModuleurl(),getIdentifier());
			if (mappingModel != null) {
				String[] moduleUrlList = getModuleurl().substring(1).split("/");
				parameters = replacelist(moduleUrlList,mappingModel.getLinkURL());
			}
		}
		return parameters;
		
	}
	public String [] replacelist(String [] moduleUrls,String [] linkUrl) {
		int count =0;
		String [] value;
		if(moduleUrls.length >linkUrl.length) {
			count =moduleUrls.length - linkUrl.length;
			value = new String[count];
			for(int i =0;i<value.length;i++) {
				int moduleCount = moduleUrls.length;
				value[i] = moduleUrls[moduleCount-count+i];
			}
			return value;
		}else {
			return value =null;
		}
	}
}
