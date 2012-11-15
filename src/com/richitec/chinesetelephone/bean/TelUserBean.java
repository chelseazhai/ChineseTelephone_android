package com.richitec.chinesetelephone.bean;

import com.richitec.commontoolkit.user.UserBean;

public class TelUserBean extends UserBean{
	private String countryCode = "";
	
	public String getCountryCode(){
		return countryCode;
	}
	
	public void setCountryCode(String code){
		this.countryCode = code;
	}
	
	public String toString(){
		String s = super.toString();
		return s + "countryCode:"+countryCode+"\n";
	}
}
