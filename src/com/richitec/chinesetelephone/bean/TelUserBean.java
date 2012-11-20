package com.richitec.chinesetelephone.bean;

import com.richitec.commontoolkit.user.UserBean;

public class TelUserBean extends UserBean{
	private String countryCode = "";
	private String areaCode = "";
	private String vosphone = "";
	private String vosphone_pwd = "";
	
	public String getCountryCode(){
		return countryCode;
	}
	
	public void setCountryCode(String code){
		this.countryCode = code;
	}
	
	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getVosphone() {
		return vosphone;
	}

	public void setVosphone(String vosphone) {
		this.vosphone = vosphone;
	}

	public String getVosphone_pwd() {
		return vosphone_pwd;
	}

	public void setVosphone_pwd(String vosphone_pwd) {
		this.vosphone_pwd = vosphone_pwd;
	}

	public String toString(){
		String s = super.toString();
		StringBuilder builder = new StringBuilder();
		builder.append("countryCode: ").append(countryCode).append("\n");
		builder.append("areaCode: ").append(areaCode).append("\n");
		builder.append("vosphone: ").append(vosphone).append("\n");
		builder.append("vosphone_pwd: ").append(vosphone_pwd).append("\n");
		
		return s + builder.toString();
	}
}
