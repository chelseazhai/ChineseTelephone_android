package com.rictitec.chinesetelephone.utils;

import com.richitec.chinesetelephone.bean.DialPreferenceBean;

public class DialPreferenceManager {
	private DialPreferenceBean dialPreferenceBean;
	private static DialPreferenceManager manager;
	
	private DialPreferenceManager(){
		if(dialPreferenceBean==null)
			dialPreferenceBean = new DialPreferenceBean();
	}
	
	public static DialPreferenceManager getInstance(){
		if(manager==null){
			synchronized(DialPreferenceManager.class){
				if(manager==null){
					manager = new DialPreferenceManager();
				}
			}
		}
		return manager;
	}
	
	public DialPreferenceBean getDialPreferenceBean(){
		if(dialPreferenceBean==null)
			dialPreferenceBean = new DialPreferenceBean(); 
		return dialPreferenceBean;
	}
	
	public void setDialPreferenceBean(DialPreferenceBean b){
		dialPreferenceBean = b;
	}
}
