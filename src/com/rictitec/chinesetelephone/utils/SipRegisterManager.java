package com.rictitec.chinesetelephone.utils;

import com.richitec.chinesetelephone.bean.TelUserBean;
import com.richitec.chinesetelephone.sip.SipRegisterBean;
import com.richitec.chinesetelephone.sip.SipUtils;
import com.richitec.chinesetelephone.sip.listeners.SipRegistrationStateListener;
import com.richitec.commontoolkit.user.UserManager;

public class SipRegisterManager {
	
	public static void registSip(SipRegistrationStateListener sipRegistrationStateListener){
		
		TelUserBean userBean = (TelUserBean) UserManager.getInstance().getUser();	
		String sipName = userBean.getVosphone();
		String sipPsw = userBean.getVosphone_pwd();
		
		if(sipName!=null&&!sipName.equals("")&&sipPsw!=null&&!sipPsw.equals("")){
			// test by ares
			// generate sip register account
			SipRegisterBean _sipAccount = new SipRegisterBean();
	
			// set test sip account
			_sipAccount.setSipUserName(sipName);
			_sipAccount.setSipPwd(sipPsw);
			_sipAccount.setSipServer("112.132.217.13");
			_sipAccount.setSipDomain("richitec.com");
			_sipAccount.setSipRealm("richitec.com");
		
			SipUtils.registerSipAccount(_sipAccount,sipRegistrationStateListener);
		}
	}

}
