package com.richitec.chinesetelephone.bean;

import com.richitec.commontoolkit.user.UserBean;

public class TelUserBean extends UserBean{
	private String registcountryCode = "";
	private String dialcountryCode="";
	private String areaCode = "";
	private String vosphone = "";
	private String vosphone_pwd = "";
	private String bindPhone = "";
	private String bindPhoneCountryCode = "";
	
	public String getDialCountryCode(){
		return dialcountryCode;
	}
	
	public void setDialCountryCode(String code){
		this.dialcountryCode = code;
	}
	
	public String getRegistCountryCode(){
		return registcountryCode;
	}
	
	public void setRegistCountryCode(String code){
		this.registcountryCode = code;
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

	public String getBindPhone() {
		return bindPhone;
	}

	public void setBindPhone(String bindPhone) {
		this.bindPhone = bindPhone;
	}

	public String getBindPhoneCountryCode() {
		return bindPhoneCountryCode;
	}

	public void setBindPhoneCountryCode(String bindPhoneCountryCode) {
		this.bindPhoneCountryCode = bindPhoneCountryCode;
	}

	public String toString(){
		String s = super.toString();
		StringBuilder builder = new StringBuilder();
		builder.append("countryCode: ").append(registcountryCode).append("\n");
		builder.append("dialcountryCode: ").append(dialcountryCode).append("\n");
		builder.append("areaCode: ").append(areaCode).append("\n");
		builder.append("vosphone: ").append(vosphone).append("\n");
		builder.append("vosphone_pwd: ").append(vosphone_pwd).append("\n");
		builder.append("bindphone: ").append(bindPhone).append("\n");
		builder.append("bindphone country code: ").append(bindPhoneCountryCode).append("\n");
		return s + builder.toString();
	}
}
