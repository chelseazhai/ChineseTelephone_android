package com.richitec.chinesetelephone.utils;

import android.util.Log;

import com.richitec.chinesetelephone.constant.SystemConstants;
import com.richitec.chinesetelephone.constant.TelUser;
import com.richitec.chinesetelephone.sip.SipRegisterBean;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.commontoolkit.user.UserBean;
import com.richitec.commontoolkit.user.UserManager;

public class SipRegisterManager {
	
	public static void registSip(SipRegistrationStateListener sipRegistStateListener, String vosServerAddress){
		Log.d(SystemConstants.TAG, "SipRegisterManager - registSip");
		UserBean userBean = UserManager.getInstance().getUser();	
		String sipName = (String) userBean.getValue(TelUser.vosphone.name());
		String sipPsw = (String) userBean.getValue(TelUser.vosphone_pwd.name());
		
		if(sipName!=null&&!sipName.equals("")&&sipPsw!=null&&!sipPsw.equals("")){
			// generate sip register account
			SipRegisterBean _sipAccount = new SipRegisterBean();
	
			// set sip account
			_sipAccount.setSipUserName(sipName);
			_sipAccount.setSipPwd(sipPsw);
			_sipAccount.setSipServer(vosServerAddress);
			_sipAccount.setSipPort(7788);
			_sipAccount.setSipDomain("richitec.com");
			_sipAccount.setSipRealm("richitec.com");
		
			SipUtils.registerSipAccount(_sipAccount,sipRegistStateListener);
		}
	}
}
